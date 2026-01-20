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

package me.impa.knockonports.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.impa.knockonports.R
import me.impa.knockonports.extension.navigate
import me.impa.knockonports.navigation.NavigateUp
import me.impa.knockonports.screen.component.common.LocalAppEventBus
import me.impa.knockonports.screen.component.common.ValueTextField
import me.impa.knockonports.screen.viewmodel.ImportKnockdConfViewModel
import me.impa.knockonports.screen.viewmodel.state.importknockd.SeqUiState
import me.impa.knockonports.screen.viewmodel.state.importknockd.UiEvent
import me.impa.knockonports.screen.viewmodel.state.importknockd.UiEvent.ChangeHost
import me.impa.knockonports.screen.viewmodel.state.importknockd.UiEvent.SelectSequence
import me.impa.knockonports.screen.viewmodel.state.importknockd.UiState
import me.impa.knockonports.ui.theme.KnockOnPortsTheme

@Composable
fun ImportKnockdConfScreen(viewModel: ImportKnockdConfViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ImportKnockdConfViewModelContent(
        state, modifier = modifier.then(Modifier.fillMaxWidth()),
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImportKnockdConfViewModelContent(
    state: UiState,
    modifier: Modifier = Modifier,
    onEvent: (UiEvent) -> Unit = {}
) {
    Surface(
        modifier = modifier,
        color = AlertDialogDefaults.containerColor,
        shape = AlertDialogDefaults.shape
    ) {
        CompositionLocalProvider(LocalContentColor provides AlertDialogDefaults.textContentColor) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.title_import_from_knockd_conf),
                    style = MaterialTheme.typography.headlineMedium,
                    color = AlertDialogDefaults.titleContentColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                when (state) {
                    UiState.Loading -> LoadingData(modifier = Modifier.fillMaxWidth())

                    UiState.NothingToImport -> {
                        EmptyData(modifier = Modifier.padding(bottom = 24.dp))
                        CloseButton(modifier = Modifier.fillMaxWidth())
                    }

                    is UiState.MultiChoice -> {
                        val importEnabled = state.seqList.any { it.checked }
                        MultiImportList(
                            state, modifier = Modifier.padding(bottom = 8.dp),
                            onSelect = { key, state -> onEvent(SelectSequence(key, state)) })
                        ValueTextField(
                            value = state.host,
                            label = stringResource(R.string.field_host),
                            onValueChange = { onEvent(ChangeHost(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        )
                        ImportButton(
                            enabled = importEnabled,
                            onClick = { onEvent(UiEvent.ImportMultiple) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    is UiState.UnableToImport -> {
                        ErrorData(state, modifier = Modifier.padding(bottom = 24.dp))
                        CloseButton(modifier = Modifier.fillMaxWidth())
                    }

                    is UiState.SingleChoice -> SingleImportList(state, onSelect = { onEvent(UiEvent.ImportSingle(it)) })
                }
            }

        }
    }
}

@Composable
private fun MultiImportList(
    state: UiState.MultiChoice, modifier: Modifier = Modifier,
    onSelect: (Long, Boolean) -> Unit = { _, _ -> }
) {
    val listState = rememberLazyListState()
    LazyColumn(modifier = modifier, state = listState) {
        stickyHeader(key = "header") {
            Text(
                text = stringResource(R.string.text_sequences_to_import),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(state.seqList, key = { "seq${it.sequence.id}" }) { seqState ->
            val interactionSource = remember { MutableInteractionSource() }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .toggleable(
                        value = seqState.checked,
                        onValueChange = { checked -> onSelect(requireNotNull(seqState.sequence.id), checked) },
                        role = Role.Checkbox,
                        indication = null,
                        interactionSource = interactionSource
                    )
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(checked = seqState.checked, onCheckedChange = null, interactionSource = interactionSource)
                Text(text = seqState.sequence.name ?: "", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun SingleImportList(state: UiState.SingleChoice, onSelect: (Long) -> Unit, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    LazyColumn(modifier = modifier, state = listState) {
        itemsIndexed(items = state.seqList, key = { _, item -> "seq${item.sequence.id}" }) { index, seqState ->
            if (index > 0) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { onSelect(requireNotNull(seqState.sequence.id)) }
                    )) {
                Text(text = seqState.sequence.name ?: "", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun LoadingData(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        Text(text = stringResource(R.string.text_loading_data))
    }

}

@Composable
private fun EmptyData(modifier: Modifier = Modifier) {
    Text(
        text = "No sequences found to import.",
        modifier = modifier.then(Modifier.fillMaxWidth()),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun ErrorData(state: UiState.UnableToImport, modifier: Modifier) {
    Text(
        text = state.error,
        modifier = modifier.then(Modifier.fillMaxWidth()),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun CloseButton(modifier: Modifier = Modifier) {
    val eventBus = LocalAppEventBus.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Button(onClick = { eventBus.navigate(NavigateUp) }) {
            Text(text = stringResource(R.string.action_close))
        }
    }
}

@Composable
private fun ImportButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Button(enabled = enabled, onClick = onClick) {
            Text(text = stringResource(R.string.action_import))
        }
    }
}


@Preview
@Composable
private fun PreviewEmptySeqDialog() {
    KnockOnPortsTheme {
        ImportKnockdConfViewModelContent(
            state = UiState.NothingToImport
        )
    }
}

@Preview
@Composable
private fun PreviewErrSeqDialog() {
    KnockOnPortsTheme {
        ImportKnockdConfViewModelContent(
            state = UiState.UnableToImport("uri", "Unable to import sequence")
        )
    }
}

@Preview
@Composable
private fun PreviewMultiChoiceSeqDialog() {
    KnockOnPortsTheme {
        ImportKnockdConfViewModelContent(
            state = UiState.MultiChoice(
                "host",
                listOf(
                    SeqUiState(
                        true, PreviewData.mockSequences[0]
                    ),
                    SeqUiState(
                        false, PreviewData.mockSequences[1]
                    ),
                )
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSingleChoiceSeqDialog() {
    KnockOnPortsTheme {
        ImportKnockdConfViewModelContent(
            state = UiState.SingleChoice(
                listOf(
                    SeqUiState(
                        true, PreviewData.mockSequences[0]
                    ),
                    SeqUiState(
                        false, PreviewData.mockSequences[1]
                    ),
                )
            )
        )
    }
}


@Preview
@Composable
private fun PreviewLoadingSeqDialog() {
    KnockOnPortsTheme {
        ImportKnockdConfViewModelContent(
            state = UiState.Loading
        )
    }
}
