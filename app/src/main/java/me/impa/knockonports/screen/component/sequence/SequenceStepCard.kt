/*
 * Copyright (c) 2025 Alexander Yaburov
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

package me.impa.knockonports.screen.component.sequence

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import kotlinx.coroutines.launch
import me.impa.knockonports.R
import me.impa.knockonports.constants.CustomConstraints.topPaddingValue
import me.impa.knockonports.constants.ICMP_HEADER_SIZE
import me.impa.knockonports.constants.IP6_HEADER_SIZE
import me.impa.knockonports.constants.MAX_PACKET_SIZE
import me.impa.knockonports.constants.MIN_IP4_HEADER_SIZE
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.SequenceStepType
import me.impa.knockonports.extension.AddressType
import me.impa.knockonports.extension.stringResourceId
import me.impa.knockonports.screen.component.common.ExpandableIconButton
import me.impa.knockonports.screen.component.common.ValueTextField
import me.impa.knockonports.screen.validate.ValidationResult
import me.impa.knockonports.screen.viewmodel.state.sequence.StepUiState
import me.impa.knockonports.screen.viewmodel.state.sequence.UiEvent
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.ui.platform.LocalResources

private const val ANIMATION_DURATION = 300

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.SequenceStepCard(
    step: StepUiState,
    state: ReorderableLazyListState,
    ip4HeaderSize: Int,
    icmpType: IcmpType,
    modifier: Modifier = Modifier,
    onEvent: (UiEvent) -> Unit = {},
) {
    val id = step.id
    val type = step.type
    val icmpSize = step.icmpSize
    val icmpCount = step.icmpCount
    val port = step.port
    val encoding = step.encoding
    val data = step.content ?: ""
    val portValidation = step.portValidation

    StepCardCover(id, state, modifier) {
        DragHandle()
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = 4.dp, bottom = 8.dp, end = 8.dp)
        ) {
            TypeSelector(
                id = id, type = type,
                onUpdateType = { id, type -> onEvent(UiEvent.UpdateStepType(id, type)) },
                onDelete = { onEvent(UiEvent.DeleteStep(id)) })
            var isExpanded by rememberSaveable { mutableStateOf(false) }
            AnimatedContainer(type) { state ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // base
                    if (state == SequenceStepType.ICMP) {
                        IcmpDataEditor(
                            icmpSize, icmpCount, ip4HeaderSize, icmpType,
                            { onEvent(UiEvent.UpdateStepIcmpSize(id, it)) },
                            { onEvent(UiEvent.UpdateStepIcmpCount(id, it)) })
                    } else {
                        TcpUdpBaseConfig(
                            port,
                            portValidation,
                            { onEvent(UiEvent.UpdateStepPort(id, it)) },
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
                        { onEvent(UiEvent.UpdateStepEncoding(id, it)) },
                        { onEvent(UiEvent.UpdateStepContent(id, it)) })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.StepCardCover(
    id: Int, state: ReorderableLazyListState, modifier: Modifier = Modifier,
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
    id: Int,
    type: SequenceStepType,
    onUpdateType: (Int, SequenceStepType) -> Unit = { _, _ -> },
    onDelete: (Int) -> Unit = {}
) {
    val resources = LocalResources.current
    val stateList =
        remember(resources) { SequenceStepType.entries.map { resources.getString(it.stringResourceId()) } }

    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
        SingleChoiceSegmentedButtonRow {
            stateList.forEachIndexed { index, label ->
                SegmentedButton(
                    icon = {},
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = stateList.size),
                    onClick = { onUpdateType(id, SequenceStepType.fromOrdinal(index)) },
                    selected = type.ordinal == index,
                    label = { Text(label) })
            }

        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onDelete(id) }) {
            Icon(Icons.Default.Close, contentDescription = null)
        }
    }
}

@Composable
private fun ExpandAdvanced(isExpanded: Boolean, onStateChanged: () -> Unit) {
    ExpandableIconButton(
        isExpanded = isExpanded,
        onStateChanged = onStateChanged,
        modifier = Modifier
            .padding(top = with(LocalDensity.current) { topPaddingValue.toDp() + 4.dp }),
        expandedContentDescription = stringResource(R.string.hint_collapse),
        collapsedContentDescription = stringResource(R.string.hint_expand)
    )
}

@Composable
private fun ReorderableCollectionItemScope.DragHandle() {
    val view = LocalView.current
    // The drag handle.
    Icon(
        painter = painterResource(R.drawable.drag_indicator_icon),
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
    ) {
        val encodings = ContentEncodingType.entries.map { stringResource(it.stringResourceId()) }

        SingleChoiceSegmentedButtonRow {
            encodings.forEachIndexed { index, label ->
                SegmentedButton(
                    icon = {},
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = encodings.size),
                    onClick = { onEncodingUpdate(ContentEncodingType.fromOrdinal(index)) },
                    selected = encoding.ordinal == index,
                    label = { Text(label) })
            }
        }

        // Text field for entering the content data.
        ValueTextField(
            label = stringResource(R.string.field_data),
            value = data,
            onValueChange = onDataUpdate
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RowScope.IcmpDataEditor(
    size: Int?, count: Int?, ip4HeaderSize: Int, type: IcmpType,
    onSizeUpdate: (Int?) -> Unit = {},
    onCountUpdate: (Int?) -> Unit = {}
) {
    // Text field for ICMP packet size.
    ValueTextField(
        label = stringResource(R.string.field_size),
        value = size,
        onValueChange = onSizeUpdate,
        modifier = Modifier
            .weight(1f),
        trailingIcon = {

            val positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                positioning = TooltipAnchorPosition.Above
            )
            val state = rememberTooltipState(isPersistent = true)
            val coroutineScope = rememberCoroutineScope()
            val interactionSource = remember { MutableInteractionSource() }

            TooltipBox(
                positionProvider = positionProvider,
                tooltip = {
                    RichTooltip(title = { Text(stringResource(R.string.title_icmp_size_tooltip)) }) {
                        Column {
                            IcmpSizeText(size, type, AddressType.IPV4, ip4HeaderSize)
                            IcmpSizeText(size, type, AddressType.IPV6, ip4HeaderSize)
                        }
                    }
                },
                state = state
            ) {
                Icon(
                    Icons.Default.Info, contentDescription = null,
                    modifier = Modifier.clickable(
                        onClick = { coroutineScope.launch { state.show() } },
                        indication = null,
                        interactionSource = interactionSource
                    )
                )
            }
        }
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
private fun TcpUdpBaseConfig(
    port: Int?, validationResult: ValidationResult,
    onPortUpdate: (Int?) -> Unit, modifier: Modifier = Modifier
) {
    // Text field for TCP/UDP port number.
    ValueTextField(
        label = stringResource(R.string.field_port),
        value = port,
        onValueChange = { onPortUpdate(it) },
        validationResult = validationResult,
        modifier = modifier
    )
}

@Composable
private fun IcmpSizeText(size: Int?, type: IcmpType, addressType: AddressType, ip4HeaderSize: Int) {
    val icmpHeader = ICMP_HEADER_SIZE
    val ipHeader = if (addressType == AddressType.IPV6) IP6_HEADER_SIZE else ip4HeaderSize
    val total = ((size ?: 0) + when (type) {
        IcmpType.WITHOUT_HEADERS -> ipHeader + icmpHeader
        IcmpType.WITH_ICMP_HEADER -> ipHeader
        IcmpType.WITH_IP_AND_ICMP_HEADERS -> 0
    }).coerceAtLeast(icmpHeader + ipHeader).coerceAtMost(MAX_PACKET_SIZE)
    val data = total - (icmpHeader + ipHeader)
    Text(
        stringResource(
            if (addressType == AddressType.IPV4) R.string.text_icmp_size_ip4_tooltip
            else R.string.text_icmp_size_ip6_tooltip, total, ipHeader, icmpHeader, data
        )
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
                StepUiState(
                    type = SequenceStepType.TCP,
                    port = 35535,
                    icmpSize = null,
                    icmpCount = null,
                    content = null,
                    id = 1,
                ),
                ip4HeaderSize = MIN_IP4_HEADER_SIZE,
                icmpType = IcmpType.WITHOUT_HEADERS,
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
                StepUiState(
                    type = SequenceStepType.UDP,
                    port = 54841,
                    icmpSize = null,
                    icmpCount = null,
                    content = null,
                    id = 1,
                ),
                ip4HeaderSize = MIN_IP4_HEADER_SIZE,
                icmpType = IcmpType.WITHOUT_HEADERS,
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
                StepUiState(
                    type = SequenceStepType.ICMP,
                    port = null,
                    icmpSize = 666,
                    icmpCount = 6,
                    content = null,
                    id = 1,
                ),
                ip4HeaderSize = MIN_IP4_HEADER_SIZE,
                icmpType = IcmpType.WITHOUT_HEADERS,
                state = reorderableListState
            )
        }
    }
}