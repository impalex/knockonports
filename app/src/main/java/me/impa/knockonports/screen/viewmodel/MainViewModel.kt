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

package me.impa.knockonports.screen.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.constants.CURRENT_BETA_TEST_MESSAGE
import me.impa.knockonports.constants.KEEP_LAST_LOG_ENTRY_COUNT
import me.impa.knockonports.constants.REVIEW_KNOCKS_REQUIRED
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.data.settings.DeviceState
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.helper.TextResource
import me.impa.knockonports.screen.viewmodel.state.main.UiEvent
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay.Automate
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay.ConfirmDelete
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay.Review
import me.impa.knockonports.screen.viewmodel.state.main.UiState
import me.impa.knockonports.service.resource.AccessWatcher
import me.impa.knockonports.service.sequence.KnockHelper
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: KnocksRepository,
    private val knockHelper: KnockHelper,
    private val resourceWatcher: AccessWatcher,
    private val settingsDataStore: SettingsDataStore,
    private val deviceState: DeviceState,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
            is UiEvent.Delete -> _state.value.sequences.values.flatten()
                .find { it.id == event.sequenceId }?.let { sequence ->
                    _overlay.update { ConfirmDelete(requireNotNull(sequence.id), sequence.name ?: "") }
                }

            is UiEvent.Focus -> _state.update { it.copy(focusedSequenceId = event.sequenceId) }
            is UiEvent.Automate -> _overlay.update { Automate(event.sequenceId) }
            is UiEvent.DoNotAskForReview -> _overlay.update { null }.also {
                viewModelScope.launch { settingsDataStore.setDoNotAskForReviewFlag() }
            }

            is UiEvent.PostponeReviewRequest -> _overlay.update { null }.also {
                viewModelScope.launch { settingsDataStore.postponeReviewRequest(event.interval) }
            }

            is UiEvent.ShowMessage -> sendMessageEvent(event.resourceId)
            is UiEvent.ShowError -> sendErrorEvent(event.message)
            is UiEvent.Knock -> knockHelper.start(event.sequenceId)
            is UiEvent.DisableNotificationRequest -> viewModelScope.launch {
                settingsDataStore.setDoNotAskForNotificationsFlag()
            }

            is UiEvent.Export -> exportSequences(event.uri)
            is UiEvent.Import -> importSequences(event.uri)
            is UiEvent.ConfirmBetaMessage -> viewModelScope.launch { confirmBetaMessage() }
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
                val flatSequences = _state.value.sequences.values.flatten()
                repository.writeSequencesToFile(uri, flatSequences)
                repository.saveLogEntry(
                    LogEntry(
                        event = EventType.EXPORT,
                        data = listOf(uri.toString(), flatSequences.size.toString())
                    )
                )
                repository.sendEvent(AppEvent.SequenceListExported(flatSequences.size))
                Timber.d("Exported ${flatSequences.size} sequences")
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

        fun findListItemIndices(position: Int, sequences: Map<String, List<Sequence>>): Pair<Int, Int> {
            var index = position - 1
            var listKeyIndex = 0
            while (index >= sequences.values.elementAt(listKeyIndex).size) {
                index -= sequences.values.elementAt(listKeyIndex++).size + 1
            }
            return index to listKeyIndex
        }

        Timber.d("Move from $from to $to")
        // Find the list that contains the 'from' item
        val (fromIndex, fromListKeyIndex) = findListItemIndices(from, _state.value.sequences)
        // Find the list that contains the 'to' item
        var (toIndex, toListKeyIndex) = findListItemIndices(to, _state.value.sequences)
        Timber.d("Move from $fromIndex to $toIndex (list: $fromListKeyIndex to $toListKeyIndex)")
        if (toIndex < 0) {
            // Adjust the 'to' index if it's negative (we are moving between lists)
            if (toListKeyIndex == fromListKeyIndex) {
                // In this case we are moving to the previous list, skip header and add to the end
                toListKeyIndex--
                toIndex = _state.value.sequences.values.elementAt(toListKeyIndex).size
            } else {
                // In this case we are moving to the next list, add to the beginning
                toIndex = 0
            }
        }
        if (toListKeyIndex == fromListKeyIndex) {
            // We are moving item withing one list, just swap items
            _state.update {
                it.copy(
                    sequences = it.sequences.toMutableMap().apply {
                        this[it.sequences.keys.elementAt(toListKeyIndex)] =
                            it.sequences.values.elementAt(toListKeyIndex).toMutableList()
                                .apply { add(toIndex, removeAt(fromIndex)) }.toImmutableList()
                    }.toPersistentMap()
                )
            }
        } else {
            // We are moving item between lists
            val copiedElement = _state.value.sequences.values.elementAt(fromListKeyIndex)[fromIndex].copy(
                group = _state.value.sequences.keys.elementAt(toListKeyIndex)
            )
            val newToList = _state.value.sequences.values.elementAt(toListKeyIndex).toMutableList().apply {
                if (fromListKeyIndex > toListKeyIndex)
                    add(copiedElement)
                else
                    add(0, copiedElement)
            }
            val newFromList = _state.value.sequences.values.elementAt(fromListKeyIndex).toMutableList().apply {
                removeAt(fromIndex)
            }
            _state.update {
                it.copy(
                    sequences = it.sequences.toMutableMap().apply {
                        this[it.sequences.keys.elementAt(toListKeyIndex)] = newToList.toImmutableList()
                        this[it.sequences.keys.elementAt(fromListKeyIndex)] = newFromList.toImmutableList()
                    }.toPersistentMap()
                )
            }
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
                _state.value.sequences.values.flatten()
                    .mapIndexed { index, sequence -> sequence.copy(order = index) })
        }
    }

    private fun cloneSequence(sequenceId: Long) {
        val sequence = _state.value.sequences.values.flatten().find { it.id == sequenceId } ?: return
        // Extract base name, removing trailing [number]
        val baseName = sequence.name?.replace(Regex("""\[\d+]$"""), "") ?: ""
        var counter = 1
        var newName: String
        val flattenSequences = _state.value.sequences.values.flatten()
        // Generate names until a unique one is found
        do {
            newName = "$baseName[${counter++}]"
        } while (flattenSequences.any { it.name == newName })
        viewModelScope.launch {
            repository.saveSequence(
                sequence.copy(
                    name = newName,
                    order = flattenSequences.maxOfOrNull { it.order ?: 0 }?.plus(1) ?: 0,
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

    private suspend fun checkReviewRequest() {
        @Suppress("ComplexCondition")
        if (deviceState.isPlayStoreInstallation
            && !settingsDataStore.doNotAskReview.first()
            && settingsDataStore.knockCount.first() >= REVIEW_KNOCKS_REQUIRED
            && settingsDataStore.doNotAskBefore.first() < System.currentTimeMillis()
        ) {
            _overlay.update { Review }
        }
    }

    private suspend fun confirmBetaMessage() {
        settingsDataStore.setCurrentBetaMessageRead()
        _overlay.update { null }
    }

    private fun startSequenceCollection() {
        Timber.d("Start collecting sequences")
        viewModelScope.launch {
            repository.getSequences()
                .distinctUntilChanged()
                .flowOn(ioDispatcher)
                .map { list ->
                    list.groupBy { (it.group ?: "").trim() }
                        .mapValues { it.value.toImmutableList() }
                        .toSortedMap()
                        .toPersistentMap()
                }
                .collect { sequences ->
                    _state.update { it.copy(sequences = sequences) }
                }
        }
    }

    private fun startConfigCollection() {
        Timber.d("Start collecting app settings")

        viewModelScope.launch {
            _state.update {
                it.copy(
                    areShortcutsAvailable = deviceState.areShortcutsAvailable,
                    isRuLangAvailable = deviceState.isRuLangAvailable
                )
            }

            combine(
                settingsDataStore.doNotAskNotification,
                settingsDataStore.detailedListView,
                settingsDataStore.titleOverflow,
                settingsDataStore.titleScale,
                settingsDataStore.titleMultiline,
            ) { doNotAskNotification, detailedView, titleOverflow, titleScale, titleMultiline ->
                _state.value.copy(
                    detailedList = detailedView,
                    titleOverflowType = titleOverflow,
                    titleScale = titleScale,
                    titleMultiline = titleMultiline,
                    disableNotificationRequest = doNotAskNotification
                )
            }.collect { newState ->
                _state.update { newState }
            }
        }
    }

    private fun startResourceStateCollection() {
        Timber.d("Start collecting resource state")
        viewModelScope.launch {
            resourceWatcher.resourceState.distinctUntilChanged().flowOn(ioDispatcher).map { state ->
                state.mapKeys { requireNotNull(it.key.id) }
            }.collect { resources ->
                _state.update {
                    it.copy(resourceState = resources.toPersistentMap())
                }
            }
        }
    }

    init {
        startSequenceCollection()
        startConfigCollection()
        startResourceStateCollection()
        viewModelScope.launch {
            Timber.d("Clear old log entries")
            repository.cleanupLogEntries(KEEP_LAST_LOG_ENTRY_COUNT)
        }
        viewModelScope.launch {
            when {
                BuildConfig.VERSION_NAME.contains("beta") && CURRENT_BETA_TEST_MESSAGE.isNotEmpty()
                        && settingsDataStore.betaMessageState.first() != CURRENT_BETA_TEST_MESSAGE ->
                    _overlay.update { UiOverlay.Beta }

                else -> checkReviewRequest()
            }
        }
    }

}

