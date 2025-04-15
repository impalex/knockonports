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

import android.content.pm.ShortcutManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.impa.knockonports.constants.KEEP_LAST_LOG_ENTRY_COUNT
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.data.settings.AppSettings
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.util.ShortcutManagerWrapper
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: KnocksRepository,
    shortcutManager: ShortcutManagerWrapper
) : ViewModel() {

    private val _sequences = MutableStateFlow<ImmutableList<Sequence>>(persistentListOf())
    val sequences: StateFlow<ImmutableList<Sequence>> = _sequences.asStateFlow()

    val appSettings: StateFlow<AppSettings> = repository.getAppSettings()

    private val _shortcutsAvailable = MutableStateFlow(false)
    val shortcutsAvailable: StateFlow<Boolean> = _shortcutsAvailable

    private val _focusedSequenceId = MutableStateFlow<Long?>(null)
    val focusedSequenceId: StateFlow<Long?> = _focusedSequenceId

    private val _shortcutManager = MutableStateFlow<ShortcutManager?>(null)
    val shortcutManager: StateFlow<ShortcutManager?> = _shortcutManager

    val doNotAskAboutNotifications = repository.doNotAskAboutNotifications()

    fun setDoNotAskAboutNotificationsFlag() = repository.setDoNotAskAboutNotificationsFlag()

    fun clearFirstLaunchV2() = repository.clearFirstLaunchV2()

    val knockCount = repository.getKnockCount()
    val firstLaunchV2 = repository.getFirstLaunchV2()
    val askReviewTime = repository.getAskReviewTime()
    val doNotAskForReview = repository.getDoNotAskForReview()
    val isInstalledFromPlayStore = repository.isInstalledFromPlayStore()

    fun postponeReviewRequest(interval: Long) = repository.postponeReviewRequest(interval)
    fun doNotAskForReview() = repository.doNotAskForReview()

    fun setFocusedSequence(sequenceId: Long?) {
        _focusedSequenceId.value = sequenceId
    }

    fun importSequences(uri: Uri) {
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

    fun exportSequences(uri: Uri) {
        viewModelScope.launch {
            Timber.d("Exporting sequences to $uri")
            try {
                repository.writeSequencesToFile(uri, _sequences.value)
                repository.saveLogEntry(
                    LogEntry(
                        event = EventType.EXPORT,
                        data = listOf(uri.toString(), _sequences.value.size.toString())
                    )
                )
                repository.sendEvent(AppEvent.SequenceListExported(_sequences.value.size))
                Timber.d("Exported ${_sequences.value.size} sequences")
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

    fun deleteSequence(sequence: Sequence) {
        viewModelScope.launch {
            Timber.d("Deleting sequence $sequence")
            repository.deleteSequence(sequence)
            repository.saveLogEntry(LogEntry(event = EventType.SEQUENCE_DELETED, data = listOf(sequence.name)))
            repository.sendEvent(AppEvent.SequenceRemoved(sequence.name))
        }
    }

    fun moveSequence(from: Int, to: Int) {
        Timber.d("Moving sequence from $from to $to")
        _sequences.value = _sequences.value.toMutableList().apply { add(to, removeAt(from)) }.toImmutableList()
    }

    /**
     * Confirms the reordering of sequences by updating their order in the repository.
     * The order of each sequence is set to its index in the current list of sequences.
     * The update operation is performed asynchronously using a coroutine launched in the viewModelScope.
     */
    fun confirmReorder() {
        viewModelScope.launch {
            Timber.d("Confirming reorder")
            repository.updateSequences(
                _sequences.value.mapIndexed { index, sequence -> sequence.copy(order = index) })
        }
    }

    fun cloneSequence(sequenceId: Long) {
        val sequence = _sequences.value.find { it.id == sequenceId } ?: return
        // Extract base name, removing trailing [number]
        val baseName = sequence.name?.replace(Regex("""\[\d+]$"""), "") ?: ""
        var counter = 1
        var newName: String
        // Generate names until a unique one is found
        do {
            newName = "$baseName[${counter++}]"
        } while (_sequences.value.any { it.name == newName })
        viewModelScope.launch {
            _focusedSequenceId.value = repository.saveSequence(
                sequence.copy(
                    name = newName,
                    order = _sequences.value.maxOfOrNull { it.order ?: 0 }?.plus(1) ?: 0,
                    id = null
                )
            )
        }
    }

    fun sendErrorEvent(errorText: String) = repository.sendEvent(AppEvent.GeneralError(errorText))

    fun sendMessageEvent(resourceId: Int) = repository.sendEvent(AppEvent.GeneralMessage(resourceId))

    init {
        viewModelScope.launch {
            Timber.d("Start collecting sequences")
            repository.getSequences().collect { data ->
                _sequences.value = data.toImmutableList()
                Timber.d("Collected ${data.size} sequences")
            }
            repository.cleanupLogEntries(KEEP_LAST_LOG_ENTRY_COUNT)
        }
        _shortcutsAvailable.value = (shortcutManager as? ShortcutManagerWrapper.Available)?.let {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && it.instance.isRequestPinShortcutSupported
        } == true

        _shortcutManager.value = (shortcutManager as? ShortcutManagerWrapper.Available)?.instance

    }

}

