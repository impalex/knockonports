/*
 * Copyright (c) 2025 Alexander Yaburov
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

package me.impa.knockonports.screen.component.sequence

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import me.impa.knockonports.R
import me.impa.knockonports.constants.CustomConstraints.topPaddingValue
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.SequenceStepType
import me.impa.knockonports.extension.stringResourceId
import me.impa.knockonports.screen.component.common.ExpandableIconButton
import me.impa.knockonports.screen.component.common.MultiToggleButton
import me.impa.knockonports.screen.component.common.MultiToggleButtonOrientation
import me.impa.knockonports.screen.component.common.ValueTextField
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

private const val ANIMATION_DURATION = 300

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.SequenceStepCard(
    step: SequenceStep,
    state: ReorderableLazyListState,
    modifier: Modifier = Modifier,
    onDelete: (String) -> Unit = {},
    onUpdateType: (String, SequenceStepType) -> Unit = { _, _ -> },
    onUpdatePort: (String, Int?) -> Unit = { _, _ -> },
    onUpdateIcmpSize: (String, Int?) -> Unit = { _, _ -> },
    onUpdateIcmpCount: (String, Int?) -> Unit = { _, _ -> },
    onUpdateContentEncoding: (String, ContentEncodingType) -> Unit = { _, _ -> },
    onUpdateContent: (String, String) -> Unit = { _, _ -> }
) {
    val id = step.id
    val type = step.type ?: SequenceStepType.UDP
    val icmpSize = step.icmpSize
    val icmpCount = step.icmpCount
    val port = step.port
    val encoding = step.encoding ?: ContentEncodingType.RAW
    val data = step.content ?: ""

    StepCardCover(id, state, modifier) {
        DragHandle()
        Column(modifier = Modifier
            .wrapContentHeight()
            .padding(start = 4.dp, bottom = 8.dp, end = 8.dp)) {
            TypeSelector(id = id, type = type, onUpdateType = onUpdateType, onDelete = onDelete)
            var isExpanded by rememberSaveable { mutableStateOf(false) }
            AnimatedContainer(type) { state ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // base
                    if (state == SequenceStepType.ICMP) {
                        IcmpDataEditor(
                            icmpSize, icmpCount,
                            { onUpdateIcmpSize(id, it) }, { onUpdateIcmpCount(id, it) })
                    } else {
                        TcpUdpBaseConfig(
                            port,
                            { onUpdatePort(id, it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Button to expand/collapse advanced configuration for UDP and ICMP.
                    if (state != SequenceStepType.TCP)
                        ExpandAdvanced(isExpanded) { isExpanded = !isExpanded }
                }
                if (isExpanded && state != SequenceStepType.TCP) {
                    IcmpUdpAdvancedConfig(
                        encoding, data,
                        { onUpdateContentEncoding(id, it) },
                        { onUpdateContent(id, it) })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.StepCardCover(
    id: String, state: ReorderableLazyListState, modifier: Modifier = Modifier,
    content: @Composable ReorderableCollectionItemScope.() -> Unit
) {
    ReorderableItem(state = state, key = id) {
        OutlinedCard(
            colors = CardDefaults.cardColors(),
            modifier = modifier.then(
                Modifier
                    .wrapContentSize()
                    .padding(vertical = 4.dp)
                    .animateContentSize()
            )
        ) {
            // The row containing the card's content.
            Row(
                modifier = Modifier
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                content()
            }
        }
    }
}

@Composable
private fun TypeSelector(
    id: String,
    type: SequenceStepType,
    onUpdateType: (String, SequenceStepType) -> Unit = { _, _ -> },
    onDelete: (String) -> Unit = {}
) {
    val resources = LocalContext.current.resources
    val stateList =
        remember(resources) { SequenceStepType.entries.map { resources.getString(it.stringResourceId()) } }

    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
        // MultiToggleButton to select the type of sequence step (TCP, UDP, ICMP).
        MultiToggleButton(
            states = stateList,
            orientation = MultiToggleButtonOrientation.HORIZONTAL,
            initialStateIndex = type.ordinal,
            unselectedTint = MaterialTheme.colorScheme.surfaceContainer,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            onStateChanged = { onUpdateType(id, SequenceStepType.fromOrdinal(it)) }
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onDelete(id) }) {
            Icon(Icons.Default.Delete, contentDescription = null)
        }
    }
}

@Composable
private fun ExpandAdvanced(isExpanded: Boolean, onStateChanged: () -> Unit) {
    ExpandableIconButton(
        isExpanded = isExpanded,
        onStateChanged = onStateChanged,
        modifier = Modifier
            .padding(top = with(LocalDensity.current) { topPaddingValue.toDp() }),
        expandedContentDescription = stringResource(R.string.hint_collapse),
        collapsedContentDescription = stringResource(R.string.hint_expand)
    )
}

@Composable
private fun ReorderableCollectionItemScope.DragHandle() {
    val view = LocalView.current
    // The drag handle.
    Icon(
        imageVector = Icons.Default.DragIndicator,
        contentDescription = stringResource(R.string.hint_reorder),
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .draggableHandle(
                onDragStarted = {
                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_START)
                },
                onDragStopped = {
                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_END)
                }
            )
            .padding(horizontal = 4.dp)
    )

}

@Composable
private fun AnimatedContainer(
    type: SequenceStepType,
    content: @Composable ColumnScope.(state: SequenceStepType) -> Unit
) {
    AnimatedContent(
        targetState = type,
        label = "StepTypeAnimation",
        modifier = Modifier.wrapContentHeight(),
        transitionSpec = {
            fadeIn(animationSpec = tween(ANIMATION_DURATION)) togetherWith
                    fadeOut(animationSpec = tween(ANIMATION_DURATION))
        }
    ) { state ->
        Column(modifier = Modifier.wrapContentHeight()) {
            content(state)
        }
    }
}

@Composable
private fun IcmpUdpAdvancedConfig(
    encoding: ContentEncodingType, data: String,
    onEncodingUpdate: (ContentEncodingType) -> Unit,
    onDataUpdate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        // MultiToggleButton to select the content encoding type.
        MultiToggleButton(
            states = ContentEncodingType.entries.map { stringResource(it.stringResourceId()) },
            orientation = MultiToggleButtonOrientation.HORIZONTAL,
            initialStateIndex = encoding.ordinal,
            unselectedTint = MaterialTheme.colorScheme.surfaceContainer,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            onStateChanged = { index -> onEncodingUpdate(ContentEncodingType.fromOrdinal(index)) }
        )
        // Text field for entering the content data.
        ValueTextField(
            label = stringResource(R.string.field_data),
            value = data,
            onValueChange = onDataUpdate
        )
    }
}

@Composable
private fun RowScope.IcmpDataEditor(
    size: Int?, count: Int?,
    onSizeUpdate: (Int?) -> Unit,
    onCountUpdate: (Int?) -> Unit
) {
    // Text field for ICMP packet size.
    ValueTextField(
        label = stringResource(R.string.field_size),
        value = size,
        onValueChange = onSizeUpdate,
        modifier = Modifier
            .weight(1f)
    )
    Spacer(modifier = Modifier.width(8.dp))
    // Text field for ICMP packet count.
    ValueTextField(
        label = stringResource(R.string.field_count),
        value = count,
        onValueChange = onCountUpdate,
        modifier = Modifier
            .weight(1f)
    )
}

@Composable
private fun TcpUdpBaseConfig(port: Int?, onPortUpdate: (Int?) -> Unit, modifier: Modifier = Modifier) {
    // Text field for TCP/UDP port number.
    ValueTextField(
        label = stringResource(R.string.field_port),
        value = port,
        onValueChange = { onPortUpdate(it) },
        onValidate = { value -> value in 1..65535 },
        modifier = modifier
    )
}

@Preview
@Composable
fun PreviewTcpSequenceStepCard() {
    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { _, _ -> }
    LazyColumn(state = lazyListState) {
        item {
            SequenceStepCard(
                SequenceStep(
                    type = SequenceStepType.TCP,
                    port = 35535,
                    icmpSize = null,
                    icmpCount = null,
                    content = null,
                    encoding = null
                ),
                state = reorderableListState
            )
        }
    }
}

@Preview
@Composable
fun PreviewUdpSequenceStepCard() {
    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { _, _ -> }
    LazyColumn(state = lazyListState) {
        item {
            SequenceStepCard(
                SequenceStep(
                    type = SequenceStepType.UDP,
                    port = 54841,
                    icmpSize = null,
                    icmpCount = null,
                    content = null,
                    encoding = null
                ),
                state = reorderableListState
            )
        }
    }
}

@Preview
@Composable
fun PreviewIcmpSequenceStepCard() {
    val lazyListState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(lazyListState) { _, _ -> }
    LazyColumn(state = lazyListState) {
        item {
            SequenceStepCard(
                SequenceStep(
                    type = SequenceStepType.ICMP,
                    port = null,
                    icmpSize = 666,
                    icmpCount = 6,
                    content = null,
                    encoding = null
                ),
                state = reorderableListState
            )
        }
    }
}