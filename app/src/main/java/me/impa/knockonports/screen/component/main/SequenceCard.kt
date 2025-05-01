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

package me.impa.knockonports.screen.component.main

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import me.impa.knockonports.R
import me.impa.knockonports.constants.TAG_SEQUENCE_ITEM
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.extension.sequenceString
import me.impa.knockonports.helper.TextResource
import me.impa.knockonports.helper.formatMillisToTimeString
import me.impa.knockonports.screen.ANIMATION_DURATION
import me.impa.knockonports.screen.viewmodel.state.main.UiEvent
import me.impa.knockonports.service.resource.ResourceState
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.SequenceCard(
    sequence: Sequence,
    resourceState: ResourceState?,
    state: ReorderableLazyListState,
    showSequenceDetails: Boolean,
    isShortcutsAvailable: Boolean,
    modifier: Modifier = Modifier,
    isHighLighted: Boolean = false,
    onHighLightFinished: () -> Unit = {},
    onEvent: (UiEvent) -> Unit = {},
    onPermissionRequest: (() -> Unit)? = null
) {
    val highlightCardColor = MaterialTheme.colorScheme.secondaryContainer
    val defaultCardColor = CardDefaults.cardColors().containerColor
    val animatedCardColor = remember { Animatable(if (isHighLighted) highlightCardColor else defaultCardColor) }
    val highlightContentColor = MaterialTheme.colorScheme.onSecondaryContainer
    val defaultContentColor = CardDefaults.cardColors().contentColor
    val animatedContentColor = remember {
        Animatable(if (isHighLighted) highlightContentColor else defaultContentColor)
    }
    val showShortcutMenu = isShortcutsAvailable && (sequence.name?.isNotBlank() == true)

    LaunchedEffect(isHighLighted) {
        if (isHighLighted) {
            animatedContentColor.snapTo(highlightContentColor)
            val backgroundAnimation = async {
                animatedCardColor.animateTo(
                    highlightCardColor,
                    animationSpec = tween(ANIMATION_DURATION, easing = EaseOut)
                )
                animatedCardColor.animateTo(
                    defaultCardColor,
                    animationSpec = tween(ANIMATION_DURATION, easing = EaseIn)
                )
            }
            val contentAnimation = async {
                animatedContentColor.animateTo(
                    defaultContentColor,
                    animationSpec = tween(ANIMATION_DURATION * 2, easing = EaseIn)
                )
            }
            backgroundAnimation.await()
            contentAnimation.await()
            onHighLightFinished()
        } else {
            animatedCardColor.snapTo(defaultCardColor)
            animatedContentColor.snapTo(defaultContentColor)
        }
    }

    ReorderableItem(state, key = sequence.id ?: 0L) {
        SequenceCardContent(
            sequence = sequence,
            resourceState = resourceState,
            cardColor = animatedCardColor.value,
            textColor = animatedContentColor.value,
            showSequenceDetails = showSequenceDetails,
            isShortcutsAvailable = showShortcutMenu,
            modifier = modifier,
            onEvent = onEvent,
            onPermissionRequest = onPermissionRequest
        )
    }
}

@Composable
private fun ReorderableCollectionItemScope.SequenceCardContent(
    sequence: Sequence,
    resourceState: ResourceState?,
    cardColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    showSequenceDetails: Boolean = true,
    isShortcutsAvailable: Boolean = false,
    onEvent: (UiEvent) -> Unit = {},
    onPermissionRequest: (() -> Unit)? = null
) {

    val interactionSource = remember { MutableInteractionSource() }
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = textColor
        ),
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("${TAG_SEQUENCE_ITEM}${sequence.id}")
        ),
        onClick = debounced({ sequence.id?.let { onEvent(UiEvent.Edit(it)) } }),
        interactionSource = interactionSource
    ) {
        // Display sequence information and controls within a row
        Row(verticalAlignment = Alignment.CenterVertically) {

            SequenceDragHandle(onDragEnded = { onEvent(UiEvent.ConfirmReorder) })
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = if (showSequenceDetails) Alignment.Top else Alignment.CenterVertically
                ) {
                    if (!showSequenceDetails) {
                        resourceState?.let { StateIcon(it) }
                    }
                    SequenceCardInfo(sequence.name?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.text_unnamed_sequence),
                        sequence.host?.takeIf { it.isNotBlank() } ?: stringResource(R.string.text_host_not_set),
                        sequence.sequenceString() ?: stringResource(R.string.text_empty_sequence), showSequenceDetails)
                    if (!showSequenceDetails)
                        KnockIconButton(onKnock = {
                            onPermissionRequest?.invoke()
                            onEvent(UiEvent.Knock(requireNotNull(sequence.id)))
                        })
                    SequenceMenu(
                        sequence = sequence,
                        isShortcutsAvailable = isShortcutsAvailable,
                        onEvent = onEvent
                    )
                }
                if (showSequenceDetails)
                    KnockButtonRow(resourceState = resourceState) {
                        onPermissionRequest?.invoke()
                        onEvent(UiEvent.Knock(requireNotNull(sequence.id)))
                    }
            }
        }
    }
}

@Composable
fun StateIcon(resourceState: ResourceState, tint: Color = LocalContentColor.current) {
    Icon(
        painter = when(resourceState) {
            is ResourceState.Available -> painterResource(R.drawable.sentiment_very_satisfied_icon)
            is ResourceState.Checking -> painterResource(R.drawable.update_icon)
            is ResourceState.Unavailable -> painterResource(R.drawable.block_icon)
            is ResourceState.Unknown -> painterResource(R.drawable.pending_icon)
        },
        modifier = Modifier.padding(end = 4.dp),
        contentDescription = null, tint = tint
    )
}

@Composable
fun ColumnScope.KnockButtonRow(resourceState: ResourceState?, onKnock: () -> Unit = {}) {


    @Composable
    fun RowScope.ResourceStateString(time: Long, text: TextResource) {
        Text(
            text = formatMillisToTimeString(time), maxLines = 1,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Text(
            text = text.asString(), maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(1f)
        )
    }

    Row(modifier = Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (resourceState == null) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            StateIcon(resourceState)
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                when (resourceState) {
                    is ResourceState.Available -> {
                        ResourceStateString(resourceState.date,
                            TextResource.DynamicText(R.string.text_resource_available))
                    }

                    is ResourceState.Checking -> {
                        ResourceStateString(resourceState.date,
                            TextResource.DynamicText(R.string.text_resource_checking))
                    }

                    is ResourceState.Unavailable -> {
                        ResourceStateString(resourceState.date, resourceState.error)
                    }

                    is ResourceState.Unknown -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Button(onClick = { onKnock() }, modifier = Modifier.padding(end = 8.dp)) {
            Text(text = stringResource(R.string.action_knock).uppercase())
        }
    }
}

@Composable
fun KnockIconButton(onKnock: () -> Unit) {
    FilledIconButton(onClick = { onKnock() }) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
    }
}

