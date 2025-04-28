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

package me.impa.knockonports.screen.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.impa.knockonports.constants.KEEP_LAST_LOG_ENTRY_COUNT
import me.impa.knockonports.constants.REVIEW_KNOCKS_REQUIRED
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.data.type.TextResource
import me.impa.knockonports.knock.KnockHelper
import me.impa.knockonports.screen.viewmodel.state.main.UiEvent
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay.Automate
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay.ConfirmDelete
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay.Review
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay.UpdateToV2
import me.impa.knockonports.screen.viewmodel.state.main.UiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: KnocksRepository,
    private val knockHelper: KnockHelper
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private val _overlay: MutableStateFlow<UiOverlay?> = MutableStateFlow(null)
    val overlay: StateFlow<UiOverlay?> = _overlay

    @Suppress("ComplexMethod")
    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.Edit -> _state.update { it.copy(editMode = true, editSequenceId = event.sequenceId) }
            is UiEvent.ResetEditMode -> _state.update { it.copy(editMode = false, editSequenceId = null) }
            is UiEvent.Duplicate -> cloneSequence(event.sequenceId)
            is UiEvent.Move -> moveSequence(event.from, event.to)
            is UiEvent.ConfirmReorder -> confirmReorder()
            is UiEvent.ClearOverlay -> _overlay.update { null }
            is UiEvent.ConfirmDelete -> confirmDelete()
            is UiEvent.Delete -> _state.value.sequences.find { it.id == event.sequenceId }?.let { sequence ->
                _overlay.update { ConfirmDelete(requireNotNull(sequence.id), sequence.name ?: "") }
            }

            is UiEvent.Focus -> _state.update { it.copy(focusedSequenceId = event.sequenceId) }
            is UiEvent.Automate -> _overlay.update { Automate(event.sequenceId) }
            is UiEvent.DoNotAskForReview -> _overlay.update { null }.also { repository.doNotAskForReview() }
            is UiEvent.PostponeReviewRequest -> _overlay.update { null }
                .also { repository.postponeReviewRequest(event.interval) }
            is UiEvent.ShowMessage -> sendMessageEvent(event.resourceId)
            is UiEvent.ShowError -> sendErrorEvent(event.message)
            is UiEvent.Knock -> knockHelper.start(event.sequenceId)
            is UiEvent.DisableNotificationRequest -> repository.setDoNotAskAboutNotificationsFlag()
            is UiEvent.Export -> exportSequences(event.uri)
            is UiEvent.Import -> importSequences(event.uri)
        }
    }

    private fun importSequences(uri: Uri) {
        viewModelScope.launch {
            Timber.d("Importing sequences from $uri")
            try {
                val sequences = repository.readSequencesFromFile(uri)
                val maxOrder = repository.getMaxOrder() ?: 0
                val orderedSequences = sequences.mapIndexed { index, sequence ->
                    sequence.copy(order = maxOrder.plus(index + 1))
                }
                repository.saveSequences(orderedSequences)
                repository.saveLogEntry(
                    LogEntry(
                        event = EventType.IMPORT,
                        data = listOf(uri.toString(), orderedSequences.size.toString())
                    )
                )
                repository.sendEvent(AppEvent.SequenceListImported(orderedSequences.size))
                Timber.d("Imported ${orderedSequences.size} sequences")
            } catch (e: Exception) {
                Timber.e(e)
                repository.saveLogEntry(
                    LogEntry(
                        event = EventType.ERROR_IMPORT,
                        data = listOf(uri.toString(), e.message ?: "")
                    )
                )
            }
        }
    }

    private fun exportSequences(uri: Uri) {
        viewModelScope.launch {
            Timber.d("Exporting sequences to $uri")
            try {
                repository.writeSequencesToFile(uri, _state.value.sequences)
                repository.saveLogEntry(
                    LogEntry(
                        event = EventType.EXPORT,
                        data = listOf(uri.toString(), _state.value.sequences.size.toString())
                    )
                )
                repository.sendEvent(AppEvent.SequenceListExported(_state.value.sequences.size))
                Timber.d("Exported ${_state.value.sequences.size} sequences")
            } catch (e: Exception) {
                Timber.e(e)
                repository.saveLogEntry(
                    LogEntry(
                        event = EventType.ERROR_EXPORT,
                        data = listOf(uri.toString(), e.message ?: "")
                    )
                )
            }
        }
    }

    private fun confirmDelete() {
        (_overlay.getAndUpdate { null } as? ConfirmDelete)?.let {
            viewModelScope.launch {
                repository.deleteSequenceById(it.id)
                repository.saveLogEntry(LogEntry(event = EventType.SEQUENCE_DELETED, data = listOf(it.name)))
                repository.sendEvent(AppEvent.SequenceRemoved(it.name))
            }
        }
    }

    private fun moveSequence(from: Int, to: Int) {
        _state.update {
            it.copy(
                sequences = it.sequences.toMutableList().apply { add(to, removeAt(from)) }.toImmutableList()
            )
        }
    }

    /**
     * Confirms the reordering of sequences by updating their order in the repository.
     * The order of each sequence is set to its index in the current list of sequences.
     * The update operation is performed asynchronously using a coroutine launched in the viewModelScope.
     */
    private fun confirmReorder() {
        viewModelScope.launch {
            repository.updateSequences(
                _state.value.sequences.mapIndexed { index, sequence -> sequence.copy(order = index) })
        }
    }

    private fun cloneSequence(sequenceId: Long) {
        val sequence = _state.value.sequences.find { it.id == sequenceId } ?: return
        // Extract base name, removing trailing [number]
        val baseName = sequence.name?.replace(Regex("""\[\d+]$"""), "") ?: ""
        var counter = 1
        var newName: String
        // Generate names until a unique one is found
        do {
            newName = "$baseName[${counter++}]"
        } while (_state.value.sequences.any { it.name == newName })
        viewModelScope.launch {
            repository.saveSequence(
                sequence.copy(
                    name = newName,
                    order = _state.value.sequences.maxOfOrNull { it.order ?: 0 }?.plus(1) ?: 0,
                    id = null
                )
            ).also { newSequenceId ->
                _state.update { it.copy(focusedSequenceId = newSequenceId) }
            }
        }
    }

    private fun sendErrorEvent(errorText: String) =
        repository.sendEvent(AppEvent.GeneralError(TextResource.PlainText(errorText)))

    private fun sendMessageEvent(resourceId: Int) =
        repository.sendEvent(AppEvent.GeneralMessage(TextResource.DynamicText(resourceId)))

    private fun checkReviewRequest() {
        val appState = repository.getAppState().value
        @Suppress("ComplexCondition")
        if (appState.isPlayStoreInstallation
            && !appState.reviewRequestDisabled
            && appState.knockCount >= REVIEW_KNOCKS_REQUIRED
            && appState.reviewRequestTimestamp < System.currentTimeMillis()
        ) {
            _overlay.update { Review }
        }
    }

    init {
        viewModelScope.launch {
            Timber.d("Start collecting sequences")

            combine(
                repository.getSequences().distinctUntilChanged(),
                repository.getAppSettings().distinctUntilChangedBy { it.detailedListView },
                repository.getAppState().distinctUntilChanged { old, new ->
                    old.areShortcutsAvailable == new.areShortcutsAvailable &&
                            old.notificationPermissionRequestDisabled == new.notificationPermissionRequestDisabled
                }) { sequences, appSettings, appState ->
                _state.update {
                    it.copy(
                        sequences = sequences.toImmutableList(),
                        detailedList = appSettings.detailedListView,
                        areShortcutsAvailable = appState.areShortcutsAvailable,
                        disableNotificationRequest = appState.notificationPermissionRequestDisabled
                    )
                }
                Timber.d("Collected ${sequences.size} sequences")
            }.collect()
        }
        viewModelScope.launch {
            Timber.d("Clear old log entries")
            repository.cleanupLogEntries(KEEP_LAST_LOG_ENTRY_COUNT)
        }
        if (repository.getAppState().value.isFirstLaunchV2) {
            repository.clearFirstLaunchV2()
            _overlay.update { UpdateToV2 }
        } else checkReviewRequest()
    }

}

