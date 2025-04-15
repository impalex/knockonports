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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import me.impa.knockonports.R
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.extension.sequenceString
import me.impa.knockonports.screen.ANIMATION_DURATION
import me.impa.knockonports.screen.action.MainViewInterface
import me.impa.knockonports.screen.event.SequenceMenuEvent
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.SequenceCard(
    sequence: Sequence,
    state: ReorderableLazyListState,
    actions: MainViewInterface,
    showSequenceDetails: Boolean,
    isShortcutsAvailable: Boolean,
    modifier: Modifier = Modifier,
    isHighLighted: Boolean = false,
    onHighLightFinished: () -> Unit = {},
    onKnock: (Long) -> Unit = {}
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
            sequence,
            actions = actions,
            cardColor = animatedCardColor.value,
            textColor = animatedContentColor.value,
            showSequenceDetails = showSequenceDetails,
            isShortcutsAvailable = showShortcutMenu,
            modifier = modifier,
            onKnock = onKnock
        )
    }
}

@Composable
private fun ReorderableCollectionItemScope.SequenceCardContent(
    sequence: Sequence,
    cardColor: Color,
    textColor: Color,
    actions: MainViewInterface,
    modifier: Modifier = Modifier,
    showSequenceDetails: Boolean = true,
    isShortcutsAvailable: Boolean = false,
    onKnock: (Long) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    var automationId by rememberSaveable { mutableStateOf<Long?>(null) }
    automationId?.let { IntegrationAlert(it, onDismiss = { automationId = null }) }
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = textColor
        ),
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ),
        onClick = debounced({ sequence.id?.let { actions.onEdit(it) } }),
        interactionSource = interactionSource
    ) {
        // Display sequence information and controls within a row
        Row(verticalAlignment = Alignment.CenterVertically) {

            SequenceDragHandle(onDragEnded = actions.onDragEnded)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = if (showSequenceDetails) Alignment.Top else Alignment.CenterVertically
                ) {
                    SequenceCardInfo(sequence.name?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.text_unnamed_sequence),
                        sequence.host?.takeIf { it.isNotBlank() } ?: stringResource(R.string.text_host_not_set),
                        sequence.sequenceString() ?: stringResource(R.string.text_empty_sequence), showSequenceDetails)
                    if (!showSequenceDetails)
                        KnockIconButton()
                    SequenceMenu(
                        isShortcutsAvailable = isShortcutsAvailable,
                        onAction = { event ->
                            Timber.d("Sequence menu event: $event")
                            when (event) {
                                is SequenceMenuEvent.Edit -> sequence.id?.let { actions.onEdit(it) }
                                is SequenceMenuEvent.Delete -> actions.confirmDelete = sequence
                                is SequenceMenuEvent.CreateShortcut -> actions.onCreateShortcut(sequence)
                                is SequenceMenuEvent.Duplicate -> sequence.id?.let { actions.onDuplicate(it) }
                                is SequenceMenuEvent.Automation -> automationId = sequence.id
                            }
                        })
                }
                if (showSequenceDetails)
                    KnockButton(onKnock = { onKnock(sequence.id!!) })
            }
        }
    }
}

@Composable
fun ColumnScope.KnockButton(onKnock: () -> Unit = {}) {
    Button(
        onClick = { onKnock() }, modifier = Modifier
            .align(Alignment.End)
            .padding(end = 8.dp, bottom = 4.dp)
    ) {
        Text(text = stringResource(R.string.action_knock).uppercase())
    }
}

@Composable
fun KnockIconButton() {
    FilledIconButton(onClick = {}) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
    }
}

