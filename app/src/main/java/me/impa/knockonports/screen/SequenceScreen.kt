/*
 * Copyright (c) 2024-2025 Alexander Yaburov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.impa.knockonports.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.collections.immutable.ImmutableList
import me.impa.knockonports.R
import me.impa.knockonports.constants.MAX_CHECK_RETRIES
import me.impa.knockonports.constants.MAX_CHECK_TIMEOUT
import me.impa.knockonports.constants.MIN_CHECK_RETRIES
import me.impa.knockonports.constants.MIN_CHECK_TIMEOUT
import me.impa.knockonports.constants.TAG_EDIT_CONFIG_TAB
import me.impa.knockonports.constants.TAG_EDIT_HOST
import me.impa.knockonports.constants.TAG_EDIT_LOCAL_PORT
import me.impa.knockonports.constants.TAG_EDIT_SEQUENCE_TAB
import me.impa.knockonports.data.type.CheckAccessType
import me.impa.knockonports.extension.stringResourceId
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.screen.component.common.HeaderSection
import me.impa.knockonports.screen.component.common.PrefStepSlider
import me.impa.knockonports.screen.component.common.PrefSwitch
import me.impa.knockonports.screen.component.common.ValueTextField
import me.impa.knockonports.screen.component.sequence.SelectApp
import me.impa.knockonports.screen.component.sequence.SelectIcmpSizeType
import me.impa.knockonports.screen.component.sequence.SelectProtocolVersion
import me.impa.knockonports.screen.component.sequence.SequenceStepCard
import me.impa.knockonports.screen.component.sequence.UpdateAppBar
import me.impa.knockonports.screen.viewmodel.SequenceViewModel
import me.impa.knockonports.screen.viewmodel.state.sequence.StepUiState
import me.impa.knockonports.screen.viewmodel.state.sequence.UiEvent
import me.impa.knockonports.screen.viewmodel.state.sequence.UiState
import me.impa.knockonports.ui.theme.Typography
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState
import timber.log.Timber

private const val DRAGGABLE_LIST_OFFSET = 4

@Composable
fun SequenceScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController,
    viewModel: SequenceViewModel,
    modifier: Modifier
) {
    val state by viewModel.state.collectAsState()
    state.savedSequenceId?.let { navController.NavigateUpAfterSave(it) }
    navController.UpdateAppBar(onComposing, viewModel::saveSequence)

    // Stabilize lambda
    val onEvent = remember(viewModel) { { event: UiEvent -> viewModel.onEvent(event) } }

    if (!state.isLoading) {
        NavScaffold(modifier) { index ->
            val view = LocalView.current
            val listState = rememberLazyListState()
            val reorderableListState = getReorderableListState(
                listState, onMove = { from, to -> onEvent(UiEvent.MoveStep(from, to)) },
                onFeedback = {
                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK)
                })
            if (index == 0) {
                state.newStepId?.let { AutoScroller(listState = listState, state = state, onEvent = onEvent) }
            }

            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                when (index) {
                    0 -> {
                        sequenceBasicConfig(
                            state = state,
                            onEvent = onEvent
                        )
                        stepList(
                            state = state,
                            steps = state.steps,
                            reorderableListState = reorderableListState,
                            onEvent = onEvent
                        )
                    }

                    1 -> sequenceAdvancedConfig(
                        state = state,
                        onEvent = onEvent
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
private fun AutoScroller(listState: LazyListState, state: UiState, onEvent: (UiEvent) -> Unit) {
    LaunchedEffect(state.steps) {
        val index = state.steps.indexOfFirst { it.id == state.newStepId }
        if (index >= 0) {
            listState.animateScrollToItem(index + DRAGGABLE_LIST_OFFSET)
            onEvent(UiEvent.ResetNewStepId)
        }
    }
}

@Composable
private fun getReorderableListState(
    listState: LazyListState, onMove: (Int, Int) -> Unit = { _, _ -> },
    onFeedback: () -> Unit = {}
) = rememberReorderableLazyListState(listState) { from, to ->
    onMove(from.index - DRAGGABLE_LIST_OFFSET, to.index - DRAGGABLE_LIST_OFFSET)
    onFeedback()
}

@Composable
private fun NavScaffold(
    modifier: Modifier = Modifier,
    pageContent: @Composable (page: Int) -> Unit = {}
) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val windowWidthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            item(
                selected = selectedItemIndex == 0,
                onClick = {
                    selectedItemIndex = 0
                },
                icon = {
                    Icon(painterResource(R.drawable.double_arrow_icon), contentDescription = null)
                },
                label = {
                    Text(text = stringResource(R.string.title_nav_sequence))
                },
                modifier = Modifier.testTag(TAG_EDIT_SEQUENCE_TAB)
            )
            item(
                selected = selectedItemIndex == 1,
                onClick = {
                    selectedItemIndex = 1
                },
                icon = {
                    Icon(painterResource(R.drawable.tune_icon), contentDescription = null)
                },
                label = {
                    Text(text = stringResource(R.string.title_nav_config))
                },
                modifier = Modifier.testTag(TAG_EDIT_CONFIG_TAB)
            )
        },
        layoutType = when (windowWidthClass) {
            WindowWidthSizeClass.EXPANDED -> NavigationSuiteType.NavigationDrawer
            WindowWidthSizeClass.MEDIUM -> NavigationSuiteType.NavigationRail
            else -> NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
        }
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            pageContent(selectedItemIndex)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun LazyListScope.sequenceBasicConfig(
    state: UiState,
    onEvent: (UiEvent) -> Unit = {}
) {

    item(key = "name_edit") {
        ValueTextField(
            stringResource(R.string.field_name), state.title,
            onValueChange = { onEvent(UiEvent.UpdateTitle(it)) },
            validationResult = state.titleValidation
        )
    }
    item(key = "host_edit") {
        ValueTextField(
            stringResource(R.string.field_host), state.host,
            onValueChange = { onEvent(UiEvent.UpdateHost(it)) },
            validationResult = state.hostValidation,
            modifier = Modifier.testTag(TAG_EDIT_HOST)
        )
    }
    item(key = "group_edit") {
        var expanded by remember { mutableStateOf(false) }
        val filteredList = remember(state.group) {
            val filter = state.group.trim()
            if (filter.isEmpty()) state.groupList
            else state.groupList.filter { it.contains(filter, false) }
        }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            ValueTextField(
                stringResource(R.string.field_group), state.group,
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                onValueChange = { onEvent(UiEvent.UpdateGroup(it)) }
            )
            if (filteredList.isNotEmpty())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    filteredList.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                onEvent(UiEvent.UpdateGroup(it))
                                expanded = false
                            }
                        )
                    }
                }
        }
    }
    stickyHeader(key = "seq_header") {
        SequenceStepsHeader(onAddNew = { onEvent(UiEvent.AddStep) })
    }
}

private fun LazyListScope.stepList(
    state: UiState,
    steps: ImmutableList<StepUiState>,
    reorderableListState: ReorderableLazyListState,
    onEvent: (UiEvent) -> Unit = {}
) {
    items(steps, key = { it.id }) { step ->
        SequenceStepCard(
            step, ip4HeaderSize = state.ip4HeaderSize,
            icmpType = state.icmpSizeType, state = reorderableListState, onEvent = onEvent
        )
    }
}


@Composable
private fun SequenceStepsHeader(onAddNew: () -> Unit = {}) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val gradientBrush = remember(surfaceColor) {
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
    state: UiState,
    onEvent: (UiEvent) -> Unit = {},
) {
    packetConfig(state, onEvent)
    checkAccessConfig(state, onEvent)
    postKnockConfig(state, onEvent)
}

private fun LazyListScope.packetConfig(state: UiState, onEvent: (UiEvent) -> Unit = {}) {
    item(key = "header_packets") {
        HeaderSection(stringResource(R.string.title_header_sequence_packet_cfg), showDivider = false)
    }
    item(key = "delay_edit") {
        ValueTextField(
            stringResource(R.string.field_delay),
            state.delay,
            onValueChange = { onEvent(UiEvent.UpdateDelay(it)) },
            validationResult = state.delayValidation
        )
    }
    item(key = "local_port") {
        ValueTextField(
            stringResource(R.string.field_local_port),
            state.localPort,
            modifier = Modifier.testTag(TAG_EDIT_LOCAL_PORT),
            onValueChange = { onEvent(UiEvent.UpdateLocalPort(it)) },
            validationResult = state.localPortValidation
        )
    }
    item(key = "ttl") {
        ValueTextField(
            stringResource(R.string.field_ttl), state.ttl,
            onValueChange = { onEvent(UiEvent.UpdateTtl(it)) },
            validationResult = state.ttlValidation
        )
    }
    item(key = "protocol_version") {
        SelectProtocolVersion(state.protocolVersion) { onEvent(UiEvent.UpdateProtocol(it)) }
    }
    item(key = "icmp_type") {
        SelectIcmpSizeType(state.icmpSizeType) { onEvent(UiEvent.UpdateIcmpType(it)) }
    }
}

private fun LazyListScope.postKnockConfig(state: UiState, onEvent: (UiEvent) -> Unit = {}) {
    item(key = "header_post_knock") {
        HeaderSection(stringResource(R.string.title_header_sequence_post_knock))
    }
    item(key = "app_edit") {
        SelectApp(state.appPackage, state.appName)
        { appPackage, appName -> onEvent(UiEvent.UpdateApp(appPackage, appName)) }
    }
    item(key = "uri_edit") {
        ValueTextField(
            stringResource(R.string.field_launch_uri), state.uri ?: "",
            onValueChange = { onEvent(UiEvent.UpdateUri(it)) })
    }
}

private fun LazyListScope.postKnockResourceConfig(state: UiState, onEvent: (UiEvent) -> Unit = {}) {
    item(key = "check_access_resource") {
        Row {
            ValueTextField(
                label = if (state.checkAccessType == CheckAccessType.URL)
                    stringResource(R.string.field_url)
                else
                    stringResource(R.string.field_host),
                value = state.checkAccessHost ?: "",
                onValueChange = { onEvent(UiEvent.UpdateCheckAccessHost(it)) },
                validationResult = state.checkAccessHostValidation,
                modifier = Modifier.weight(if (state.checkAccessType == CheckAccessType.PORT) 2f else 1f)
            )
            if (state.checkAccessType == CheckAccessType.PORT) {
                ValueTextField(
                    label = stringResource(R.string.field_port), value = state.checkAccessPort,
                    onValueChange = { onEvent(UiEvent.UpdateCheckAccessPort(it)) },
                    validationResult = state.checkAccessPortValidation,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
        }
    }
    item(key = "check_access_timeout") {
        PrefStepSlider(
            title = stringResource(R.string.title_check_access_timeout),
            description = pluralStringResource(
                R.plurals.text_check_access_timeout,
                state.checkAccessTimeout, state.checkAccessTimeout
            ),
            value = state.checkAccessTimeout,
            minValue = MIN_CHECK_TIMEOUT,
            maxValue = MAX_CHECK_TIMEOUT,
            onChanged = { onEvent(UiEvent.UpdateCheckAccessTimeout(it)) }
        )
    }
}

private fun LazyListScope.checkAccessConfig(state: UiState, onEvent: (UiEvent) -> Unit = {}) {
    item(key = "header_check_access") {
        HeaderSection(stringResource(R.string.title_header_sequence_access_check))
    }
    item(key = "enable_check_access") {
        PrefSwitch(
            title = stringResource(R.string.title_check_access_enable),
            description = stringResource(R.string.text_check_access_enable),
            value = state.checkAccess, onClick = { onEvent(UiEvent.ToggleCheckAccess) })
    }
    if (state.checkAccess) {
        item(key = "check_access_type") {
            val resources = LocalContext.current.resources
            val checkAccessTypes = remember {
                CheckAccessType.entries.map { resources.getString(it.stringResourceId()) }
            }
            SingleChoiceSegmentedButtonRow {
                checkAccessTypes.forEachIndexed { index, label ->
                    SegmentedButton(
                        icon = {},
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = checkAccessTypes.size),
                        onClick = { onEvent(UiEvent.UpdateCheckAccessType(CheckAccessType.fromOrdinal(index))) },
                        selected = state.checkAccessType.ordinal == index,
                        label = { Text(label) })
                }
            }
        }
        postKnockResourceConfig(state, onEvent)
        item(key = "check_access_post_knock") {
            PrefSwitch(
                title = stringResource(R.string.title_check_access_post_knock),
                description = stringResource(R.string.text_check_access_post_knock),
                value = state.checkAccessPostKnock,
                onClick = { onEvent(UiEvent.ToggleCheckAccessPostKnock) }
            )
        }
        if (state.checkAccessPostKnock) {
            item(key = "check_access_post_knock_retries") {
                PrefStepSlider(
                    title = stringResource(R.string.title_check_access_retries),
                    description = pluralStringResource(
                        R.plurals.text_check_access_retries,
                        state.checkAccessKnockRetries, state.checkAccessKnockRetries
                    ),
                    value = state.checkAccessKnockRetries,
                    minValue = MIN_CHECK_RETRIES,
                    maxValue = MAX_CHECK_RETRIES,
                    onChanged = { onEvent(UiEvent.UpdateCheckAccessMaxRetries(it)) }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewNavScaffold() {
    NavScaffold()
}

@Preview
@Composable
fun PreviewBasicConfig() {
    LazyColumn {
        sequenceBasicConfig(
            UiState(title = "Title", host = "10.5.0.3")
        )
    }
}

@Preview
@Composable
fun PreviewAdvancedConfig() {
    LazyColumn {
        sequenceAdvancedConfig(UiState(checkAccess = true, checkAccessType = CheckAccessType.PORT))
    }
}

