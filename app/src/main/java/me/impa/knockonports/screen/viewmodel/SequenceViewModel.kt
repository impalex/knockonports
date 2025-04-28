/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.impa.knockonports.constants.MAX_PORT
import me.impa.knockonports.constants.MAX_SLEEP
import me.impa.knockonports.constants.MAX_TTL
import me.impa.knockonports.constants.MIN_IP4_HEADER_SIZE
import me.impa.knockonports.constants.MIN_PORT
import me.impa.knockonports.constants.MIN_SLEEP
import me.impa.knockonports.constants.MIN_TTL
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType
import me.impa.knockonports.screen.validate.NotEmptyStringValidator
import me.impa.knockonports.screen.validate.RangeValidator
import me.impa.knockonports.screen.viewmodel.state.sequence.StepUiState
import me.impa.knockonports.screen.viewmodel.state.sequence.UiEvent
import me.impa.knockonports.screen.viewmodel.state.sequence.UiState
import me.impa.knockonports.screen.viewmodel.state.sequence.toSequenceStep
import me.impa.knockonports.screen.viewmodel.state.sequence.toStepUiState
import timber.log.Timber

@Suppress("TooManyFunctions")
@HiltViewModel(assistedFactory = SequenceViewModel.SequenceViewModelFactory::class)
class SequenceViewModel @AssistedInject constructor(
    @Assisted private var sequenceId: Long?,
    val repository: KnocksRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private val _notEmptyStringValidator = NotEmptyStringValidator()
    private val _portValidator = RangeValidator(MIN_PORT, MAX_PORT)
    private val _ttlValidator = RangeValidator(MIN_TTL, MAX_TTL)
    private val _sleepValidator = RangeValidator(MIN_SLEEP, MAX_SLEEP)

    private fun updateSequenceStep(stepId: Int, onUpdate: (StepUiState) -> StepUiState) {
        _state.update {
            it.copy(steps = it.steps.map {
                if (it.id == stepId) onUpdate(it) else it
            }.toPersistentList())
        }
    }

    @Suppress("CyclomaticComplexMethod")
    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.AddStep -> _state.update {
                val newStepId = it.steps.maxOfOrNull { it.id }?.plus(1) ?: 0
                val stepType = it.steps.lastOrNull()?.type ?: SequenceStepType.UDP
                it.copy(
                    newStepId = newStepId,
                    steps = it.steps.toMutableList().apply { add(StepUiState(id = newStepId, type = stepType)) }
                        .toPersistentList()
                )
            }

            is UiEvent.DeleteStep -> _state.update {
                it.copy(steps = it.steps.filter { it.id != event.id }
                    .toPersistentList())
            }

            is UiEvent.MoveStep -> _state.update {
                it.copy(steps = it.steps.toMutableList().apply {
                    add(event.to, removeAt(event.from))
                }.toPersistentList())
            }

            is UiEvent.ResetNewStepId -> _state.update { it.copy(newStepId = null) }
            is UiEvent.UpdateHost -> _state.update {
                it.copy(
                    host = event.host,
                    hostValidation = _notEmptyStringValidator.validate(event.host)
                )
            }

            is UiEvent.UpdateStepContent -> updateSequenceStep(event.id) { it.copy(content = event.content) }
            is UiEvent.UpdateStepEncoding -> updateSequenceStep(event.id) { it.copy(encoding = event.encoding) }
            is UiEvent.UpdateStepIcmpCount -> updateSequenceStep(event.id) { it.copy(icmpCount = event.count) }
            is UiEvent.UpdateStepIcmpSize -> updateSequenceStep(event.id) { it.copy(icmpSize = event.size) }
            is UiEvent.UpdateStepPort -> updateSequenceStep(event.id) {
                it.copy(
                    port = event.port,
                    portValidation = _portValidator.validate(event.port)
                )
            }

            is UiEvent.UpdateStepType -> updateSequenceStep(event.id) { it.copy(type = event.type) }
            is UiEvent.UpdateTitle -> _state.update {
                it.copy(
                    title = event.title,
                    titleValidation = _notEmptyStringValidator.validate(event.title)
                )
            }

            is UiEvent.UpdateApp -> _state.update { it.copy(appName = event.appName, appPackage = event.appPackage) }
            is UiEvent.UpdateDelay -> _state.update {
                it.copy(
                    delay = event.delay,
                    delayValidation = _sleepValidator.validate(event.delay)
                )
            }

            is UiEvent.UpdateIcmpType -> _state.update { it.copy(icmpSizeType = event.icmpType) }
            is UiEvent.UpdateLocalPort -> _state.update {
                it.copy(
                    localPort = event.port,
                    localPortValidation = _portValidator.validate(event.port)
                )
            }

            is UiEvent.UpdateProtocol -> _state.update { it.copy(protocolVersion = event.protocol) }
            is UiEvent.UpdateTtl -> _state.update {
                it.copy(
                    ttl = event.ttl,
                    ttlValidation = _ttlValidator.validate(event.ttl)
                )
            }

            is UiEvent.UpdateUri -> _state.update { it.copy(uri = event.uri) }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    // Loads sequence data from the repository based on the provided ID
    private fun loadSequence(id: Long) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                repository.findSequence(id)?.let { sequence ->
                    _state.update {
                        it.copy(
                            title = sequence.name ?: "",
                            host = sequence.host ?: "",
                            steps = sequence.steps?.mapIndexed { index, step ->
                                step.toStepUiState(index)
                            }?.toPersistentList() ?: persistentListOf(),
                            delay = sequence.delay,
                            localPort = sequence.localPort,
                            protocolVersion = sequence.ipv ?: ProtocolVersionType.PREFER_IPV4,
                            icmpSizeType = sequence.icmpType ?: IcmpType.WITHOUT_HEADERS,
                            appPackage = sequence.application,
                            ttl = sequence.ttl,
                            appName = sequence.applicationName,
                            uri = sequence.uri,
                            order = sequence.order
                        )
                    }
                } ?: { sequenceId = null }
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    // Saves the current sequence configuration to the repository
    fun saveSequence() {
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {

                repository.saveSequence(
                    _state.value.let {
                        Sequence(
                            id = sequenceId,
                            name = it.title,
                            host = it.host,
                            order = it.order ?: ((repository.getMaxOrder() ?: 0) + 1),
                            delay = it.delay,
                            application = it.appPackage,
                            applicationName = it.appName,
                            icmpType = it.icmpSizeType,
                            steps = it.steps.map { it.toSequenceStep() }.toPersistentList(),
                            descriptionType = null, // TODO this will be deprecated
                            pin = null, // TODO this will be deprecated
                            ipv = it.protocolVersion,
                            ttl = it.ttl,
                            localPort = it.localPort,
                            uri = it.uri
                        )
                    }
                ).also { newId ->
                    _state.update { it.copy(savedSequenceId = newId) }
                }
                repository.saveLogEntry(
                    LogEntry(
                        event = EventType.SEQUENCE_SAVED,
                        data = listOf(_state.value.title)
                    )
                )
                repository.sendEvent(AppEvent.SequenceSaved(sequenceName = _state.value.title))
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    init {
        sequenceId?.let { loadSequence(id = it) }
        with(repository.getAppSettings().value) {
            _state.update { it.copy(ip4HeaderSize = ip4HeaderSize.takeIf { customIp4Header } ?: MIN_IP4_HEADER_SIZE) }
        }
    }

    @AssistedFactory
    interface SequenceViewModelFactory {
        fun create(sequenceId: Long?): SequenceViewModel
    }
}
