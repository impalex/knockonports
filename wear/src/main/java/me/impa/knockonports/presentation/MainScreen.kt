/*
 * Copyright (c) 2026 Alexander Yaburov
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

package me.impa.knockonports.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CircularProgressIndicatorDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListSubHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SegmentedCircularProgressIndicator
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import me.impa.knockonports.R
import me.impa.knockonports.presentation.theme.KnockOnPortsTheme
import me.impa.knockonports.shared.data.KnockStatus
import me.impa.knockonports.shared.data.SequenceInfo
import me.impa.knockonports.shared.data.SequenceList

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: MainViewModel = hiltViewModel()) {
    val isCompanionReady by viewModel.isConnectionReady.collectAsStateWithLifecycle()
    val sequences by viewModel.sequences.collectAsStateWithLifecycle()
    val knockStatus by viewModel.knockStatus.collectAsStateWithLifecycle()

    MainScreenContent(
        isCompanionReady = isCompanionReady,
        sequences = sequences,
        knockStatus = knockStatus,
        onOpenMarket = viewModel::openRemoteMarket,
        onExecute = viewModel::startKnocking,
        modifier = modifier
    )
}

@Composable
fun MainScreenContent(
    isCompanionReady: Boolean,
    sequences: SequenceList,
    knockStatus: KnockStatus,
    onOpenMarket: () -> Unit,
    onExecute: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    KnockOnPortsTheme {
        AppScaffold(modifier = modifier) {
            val readyListState = rememberTransformingLazyColumnState()
            val knockingListState = rememberTransformingLazyColumnState()
            val noConnectionListState = rememberTransformingLazyColumnState()

            when {
                !isCompanionReady -> NoConnectionScaffold(noConnectionListState, onOpenMarket)
                knockStatus.is_active -> KnockingScaffold(knockingListState, knockStatus)
                else -> ReadyScaffold(readyListState, sequences, onExecute)
            }
        }
    }
}

@Composable
fun ReadyScaffold(listState: TransformingLazyColumnState, sequences: SequenceList, onExecute: (Long) -> Unit) {
    val transformationSpec = rememberTransformationSpec()

    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )
    ScreenScaffold(scrollState = listState,
        contentPadding = contentPadding) { contentPadding ->
        TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
            item(key = "ready_header") {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                ) { Text(text = stringResource(R.string.title_sequences)) }
            }
            sequenceList(sequences, transformationSpec, onExecute)
        }
    }
}

@Composable
fun KnockingScaffold(listState: TransformingLazyColumnState, knockStatus: KnockStatus) {
    val transformationSpec = rememberTransformationSpec()

    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.BodyText,
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (knockStatus.is_active && knockStatus.max_steps > 1) {
            val animatedProgress by animateFloatAsState(
                targetValue = knockStatus.step.toFloat() / knockStatus.max_steps.toFloat(),
                label = "Progress"
            )

            SegmentedCircularProgressIndicator(
                segmentCount = knockStatus.max_steps,
                progress = { animatedProgress },
                modifier = Modifier
                    .padding(CircularProgressIndicatorDefaults.FullScreenPadding)
                    .fillMaxSize(),
                strokeWidth = CircularProgressIndicatorDefaults.largeStrokeWidth,
                gapSize = CircularProgressIndicatorDefaults.calculateRecommendedGapSize(
                    CircularProgressIndicatorDefaults.largeStrokeWidth
                ),
                startAngle = 295.5f,
                endAngle = 244.5f,
            )
        }
        ScreenScaffold(scrollState = listState, contentPadding = contentPadding) { contentPadding ->
            TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                item(key = "knocking_header") {
                    ListHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                    ) { Text(text = stringResource(R.string.title_knocking)) }
                }
                knockStatus(knockStatus, transformationSpec)
            }
        }
    }
}

@Composable
fun NoConnectionScaffold(listState: TransformingLazyColumnState, onOpenMarket: () -> Unit) {

    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(scrollState = listState, contentPadding = contentPadding) { contentPadding ->
        TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
            item(key = "no_connection_header") {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    transformation = SurfaceTransformation(transformationSpec),
                ) { Text(text = stringResource(R.string.title_install_app)) }
            }
            item(key = "no_connection_subheader") {
                Text(
                    text = stringResource(R.string.text_install_app),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            noConnection(transformationSpec, onOpenMarket)
        }
    }
}

fun TransformingLazyColumnScope.knockStatus(
    knockStatus: KnockStatus,
    transformationSpec: TransformationSpec
) {
    item(key = "knock_status") {
        ListSubHeader(
            modifier = Modifier
                .transformedHeight(this, transformationSpec),
            transformation = SurfaceTransformation(transformationSpec)
        ) {
            Text(
                text = knockStatus.sequence_name.ifEmpty { stringResource(R.string.title_no_name_sequence) },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
    if (knockStatus.max_attempts > 1) {
        item(key = "knock_attempts") {
            Text(
                text = stringResource(R.string.text_attempts, knockStatus.attempt, knockStatus.max_attempts),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .transformedHeight(this, transformationSpec)
            )
        }
    }
}

fun TransformingLazyColumnScope.noConnection(transformationSpec: TransformationSpec, onOpenMarket: () -> Unit) {
    item(key = "open_market") {
        Button(
            onClick = onOpenMarket, modifier = Modifier
                .padding(top = 16.dp)
                .transformedHeight(this, transformationSpec)
        ) {
            Text(stringResource(R.string.action_open_market), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

fun TransformingLazyColumnScope.sequenceList(
    list: SequenceList, transformationSpec: TransformationSpec,
    onExecute: (Long) -> Unit
) {
    if (list.items.isNotEmpty()) {
        items(items = list.items, key = { "sequence_${it.id}" }) { item ->
            Button(
                onClick = { onExecute(item.id) }, modifier = Modifier
                    .fillMaxWidth()
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec)
            ) {
                Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    } else {
        item(key = "sequence_empty") {
            ListSubHeader(
                modifier = Modifier
                    .transformedHeight(this, transformationSpec),
                transformation = SurfaceTransformation(transformationSpec)
            ) {
                Text(
                    text = stringResource(R.string.text_empty_list),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun PreviewMainScreenContent() {
    MainScreenContent(
        isCompanionReady = true,
        sequences = SequenceList(
            listOf(
                SequenceInfo(1, "Sequence 1"),
                SequenceInfo(2, "Sequence 2"),
            )
        ),
        knockStatus = KnockStatus(),
        onOpenMarket = {},
        onExecute = {}
    )
}

@WearPreviewDevices
@Composable
private fun PreviewMainScreenContentNoConnection() {
    MainScreenContent(
        isCompanionReady = false,
        sequences = SequenceList(
            listOf(
                SequenceInfo(1, "Sequence 1"),
                SequenceInfo(2, "Sequence 2"),
            )
        ),
        knockStatus = KnockStatus(),
        onOpenMarket = {},
        onExecute = {}
    )
}

@WearPreviewDevices
@Composable
private fun PreviewMainScreenContentKnockProgreess() {
    MainScreenContent(
        isCompanionReady = true,
        sequences = SequenceList(
            listOf(
                SequenceInfo(1, "Sequence 1"),
                SequenceInfo(2, "Sequence 2"),
            )
        ),
        knockStatus = KnockStatus(
            is_active = true,
            step = 2,
            max_steps = 5,
            attempt = 2,
            max_attempts = 3,
            sequence_name = "Sequence 1 — This is very long title — very-very long title — no, really, it's very long"
        ),
        onOpenMarket = {},
        onExecute = {}
    )
}