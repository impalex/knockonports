/*
 * Copyright (c) 2024-2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package me.impa.knockonports.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import kotlinx.collections.immutable.ImmutableList
import me.impa.knockonports.R
import me.impa.knockonports.constants.TAG_EDIT_ADVANCED_TAB
import me.impa.knockonports.constants.TAG_EDIT_BASIC_TAB
import me.impa.knockonports.constants.TAG_EDIT_HOST
import me.impa.knockonports.constants.TAG_EDIT_LOCAL_PORT
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.screen.component.common.ValueTextField
import me.impa.knockonports.screen.component.sequence.SelectApp
import me.impa.knockonports.screen.component.sequence.SelectIcmpSizeType
import me.impa.knockonports.screen.component.sequence.SelectProtocolVersion
import me.impa.knockonports.screen.component.sequence.SequenceStepCard
import me.impa.knockonports.screen.component.sequence.UpdateAppBar
import me.impa.knockonports.screen.viewmodel.SequenceViewModel
import me.impa.knockonports.screen.viewmodel.state.AdvancedSequenceSettings
import me.impa.knockonports.screen.viewmodel.state.BasicSequenceSettings
import me.impa.knockonports.ui.theme.Typography
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState
import timber.log.Timber

@Composable
fun SequenceScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController,
    viewModel: SequenceViewModel,
    modifier: Modifier
) {
    val savedSequenceId by viewModel.savedSequenceId.collectAsState()
    savedSequenceId?.let { navController.NavigateUpAfterSave(it) }
    navController.UpdateAppBar(onComposing, viewModel::saveSequence)
    val isLoading by viewModel.isLoading.collectAsState()
    var autoScrollToEnd by remember { mutableStateOf(false) }

    // Stabilize lambda
    val addNewStep = remember(viewModel) {
        {
            viewModel.addSequenceStep()
            autoScrollToEnd = true
        }
    }

    if (!isLoading) {
        val basicConfig by viewModel.basicConfig.collectAsState()
        val advancedConfig by viewModel.advancedConfig.collectAsState()
        val sequenceSteps by viewModel.sequenceSteps.collectAsState()

        SequenceTabs(modifier) { index ->
            val view = LocalView.current
            val listState = rememberLazyListState()
            val savedStateList = rememberSaveable(saver = LazyListState.Saver) { listState }
            val reorderableListState = getReorderableListState(
                savedStateList, onMove = { from, to -> viewModel.moveSequenceStep(from, to) },
                onFeedback = {
                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK)
                })
            Timber.d("AutoScrollToEnd: $autoScrollToEnd")
            if (index == 0) {
                if (autoScrollToEnd) AutoScroller(savedStateList, sequenceSteps) { autoScrollToEnd = false }
            }

            LazyColumn(state = savedStateList, modifier = Modifier.fillMaxSize()) {
                when (index) {
                    0 -> {
                        sequenceBasicConfig(
                            config = basicConfig,
                            onUpdateName = viewModel::updateName,
                            onUpdateHost = viewModel::updateHost,
                            onAddNew = addNewStep,
                        )
                        stepList(
                            steps = sequenceSteps,
                            reorderableListState = reorderableListState,
                            onDelete = viewModel::deleteSequenceStep,
                            onUpdateType = viewModel::updateStepType,
                            onUpdatePort = viewModel::updateStepPort,
                            onUpdateIcmpSize = viewModel::updateStepIcmpSize,
                            onUpdateIcmpCount = viewModel::updateStepIcmpCount,
                            onUpdateContentEncoding = viewModel::updateStepEncoding,
                            onUpdateContent = viewModel::updateStepContent
                        )
                    }

                    1 -> sequenceAdvancedConfig(
                        config = advancedConfig,
                        onUpdateDelay = { viewModel.updateAdvancedConfig { copy(delay = it) } },
                        onUpdateLocalPort = { viewModel.updateAdvancedConfig { copy(localPort = it) } },
                        onUpdateTtl = { viewModel.updateAdvancedConfig { copy(ttl = it) } },
                        onUpdateProtocolVersion = { viewModel.updateAdvancedConfig { copy(protocolVersion = it) } },
                        onUpdateIcmpSizeType = { viewModel.updateAdvancedConfig { copy(icmpSizeType = it) } },
                        onUpdateUri = { viewModel.updateAdvancedConfig { copy(uri = it) } },
                        onUpdateApp = { appPackage, appName ->
                            viewModel.updateAdvancedConfig { copy(appPackage = appPackage, appName = appName) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavController.NavigateUpAfterSave(sequenceId: Long) {
    LaunchedEffect(sequenceId) {
        Timber.d("Saved sequence id: $sequenceId")
        previousBackStackEntry?.savedStateHandle?.set(FOCUSED_SEQUENCE_ID, sequenceId)
        navigateUp()
    }
}

@Composable
private fun AutoScroller(listState: LazyListState, sequenceSteps: ImmutableList<SequenceStep>, onDone: () -> Unit) {
    LaunchedEffect(sequenceSteps) {
        if (!sequenceSteps.isEmpty()) {
            listState.animateScrollToItem(sequenceSteps.lastIndex)
        }
        onDone()
    }
}

private const val DRAGGABLE_LIST_OFFSET = 3

@Composable
private fun getReorderableListState(
    listState: LazyListState, onMove: (Int, Int) -> Unit = { _, _ -> },
    onFeedback: () -> Unit = {}
) = rememberReorderableLazyListState(listState) { from, to ->
    onMove(from.index - DRAGGABLE_LIST_OFFSET, to.index - DRAGGABLE_LIST_OFFSET)
    onFeedback()
}

@Composable
private fun SequenceTabs(modifier: Modifier, pageContent: @Composable PagerScope.(page: Int) -> Unit) {
    Column(modifier = modifier.fillMaxSize()) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState { 2 }
        println("selectedTabIndex: $selectedTabIndex")
        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }

        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress) {
                selectedTabIndex = pagerState.currentPage
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                modifier = Modifier.testTag(TAG_EDIT_BASIC_TAB),
                text = { Text(stringResource(R.string.title_tab_basic_settings)) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier.testTag(TAG_EDIT_ADVANCED_TAB),
                text = { Text(stringResource(R.string.title_tab_advanced_settings)) }
            )
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize(1f)
                .padding(8.dp),
            pageContent = pageContent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.sequenceBasicConfig(
    config: BasicSequenceSettings,
    onUpdateName: (String) -> Unit = {},
    onUpdateHost: (String) -> Unit = {},
    onAddNew: () -> Unit = {}
) {

    val name = config.name
    val host = config.host
    item(key = "name_edit") {
        ValueTextField(stringResource(R.string.field_name), name, onValueChange = { onUpdateName(it) })
    }
    item(key = "host_edit") {
        ValueTextField(stringResource(R.string.field_host), host, onValueChange = { onUpdateHost(it) },
            modifier = Modifier.testTag(TAG_EDIT_HOST))
    }
    stickyHeader(key = "seq_header") {
        SequenceStepsHeader(onAddNew = { onAddNew() })
    }
}

private fun LazyListScope.stepList(
    steps: ImmutableList<SequenceStep>,
    reorderableListState: ReorderableLazyListState,
    onDelete: (String) -> Unit = {},
    onUpdateType: (String, SequenceStepType) -> Unit = { _, _ -> },
    onUpdatePort: (String, Int?) -> Unit = { _, _ -> },
    onUpdateIcmpSize: (String, Int?) -> Unit = { _, _ -> },
    onUpdateIcmpCount: (String, Int?) -> Unit = { _, _ -> },
    onUpdateContentEncoding: (String, ContentEncodingType) -> Unit = { _, _ -> },
    onUpdateContent: (String, String) -> Unit = { _, _ -> }
) {
    items(steps, key = { it.id }) { step ->
        Timber.d("ReorderableItem ${step.id}")

        SequenceStepCard(
            step, state = reorderableListState,
            onDelete = { onDelete(it) },
            onUpdateType = { id, newType -> onUpdateType(id, newType) },
            onUpdatePort = { id, newPort -> onUpdatePort(id, newPort) },
            onUpdateIcmpSize = { id, newSize -> onUpdateIcmpSize(id, newSize) },
            onUpdateIcmpCount = { id, newCount -> onUpdateIcmpCount(id, newCount) },
            onUpdateContentEncoding = { id, newEncoding -> onUpdateContentEncoding(id, newEncoding) },
            onUpdateContent = { id, newContent -> onUpdateContent(id, newContent) }
        )
    }
}


@Composable
private fun SequenceStepsHeader(onAddNew: () -> Unit = {}) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val gradientBrush = remember {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to surfaceColor.copy(alpha = 1f),
                @Suppress("MagicNumber")
                0.9f to surfaceColor.copy(alpha = 1f),
                1f to surfaceColor.copy(alpha = 0f)
            )
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradientBrush)
    ) {
        Text(
            text = stringResource(R.string.title_list_sequence_steps),
            style = Typography.titleMedium,
            modifier = Modifier.alignByBaseline()
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                onAddNew()
            }, modifier = Modifier
                .padding(4.dp)
                .alignByBaseline()
        ) {
            Text(text = stringResource(R.string.action_add))
        }
    }
}

private fun LazyListScope.sequenceAdvancedConfig(
    config: AdvancedSequenceSettings,
    onUpdateDelay: (Int?) -> Unit = {},
    onUpdateLocalPort: (Int?) -> Unit = {},
    onUpdateTtl: (Int?) -> Unit = {},
    onUpdateProtocolVersion: (ProtocolVersionType) -> Unit = {},
    onUpdateIcmpSizeType: (IcmpType) -> Unit = {},
    onUpdateApp: (String?, String?) -> Unit = { _, _ -> },
    onUpdateUri: (String?) -> Unit = {}
) {
    val delay = config.delay
    val appPackage = config.appPackage
    val appName = config.appName
    val localPort = config.localPort
    val protocolVersion = config.protocolVersion
    val icmpType = config.icmpSizeType
    item(key = "delay_edit") {
        ValueTextField(stringResource(R.string.field_delay), delay, onValueChange = { onUpdateDelay(it) })
    }
    item(key = "local_port") {
        ValueTextField(
            stringResource(R.string.field_local_port),
            localPort,
            modifier = Modifier.testTag(TAG_EDIT_LOCAL_PORT),
            onValueChange = { onUpdateLocalPort(it) },
            onValidate = { it in 1..65535 })
    }
    item(key = "ttl") {
        ValueTextField(stringResource(R.string.field_ttl), config.ttl, onValueChange = { onUpdateTtl(it) })
    }
    item(key = "protocol_version") {
        SelectProtocolVersion(protocolVersion) { onUpdateProtocolVersion(it) }
    }
    item(key = "icmp_type") {
        SelectIcmpSizeType(icmpType) { onUpdateIcmpSizeType(it) }
    }
    item(key = "app_edit") {
        SelectApp(appPackage, appName) { appPackage, appName -> onUpdateApp(appPackage, appName) }
    }
    item(key = "uri_edit") {
        ValueTextField(stringResource(R.string.field_launch_uri), config.uri ?: "", onValueChange = { onUpdateUri(it) })
    }
}

@Preview
@Composable
fun PreviewSequenceTabs() {
    SequenceTabs(modifier = Modifier) { }
}

@Preview
@Composable
fun PreviewBasicConfig() {
    LazyColumn {
        sequenceBasicConfig(
            BasicSequenceSettings("Sequence name", "Sequence host")
        )
    }
}

@Preview
@Composable
fun PreviewAdvancedConfig() {
    LazyColumn {
        sequenceAdvancedConfig(AdvancedSequenceSettings())
    }
}

