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

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType
import me.impa.knockonports.screen.viewmodel.state.AdvancedSequenceSettings
import me.impa.knockonports.screen.viewmodel.state.BasicSequenceSettings
import timber.log.Timber

@Suppress("TooManyFunctions")
@HiltViewModel(assistedFactory = SequenceViewModel.SequenceViewModelFactory::class)
class SequenceViewModel @AssistedInject constructor(
    @Assisted private var sequenceId: Long?,
    val repository: KnocksRepository
) : ViewModel() {

    private val _basicConfig = MutableStateFlow(BasicSequenceSettings())
    val basicConfig: StateFlow<BasicSequenceSettings> = _basicConfig

    private val _advancedConfig = MutableStateFlow(AdvancedSequenceSettings())
    val advancedConfig: StateFlow<AdvancedSequenceSettings> = _advancedConfig

    private val _sequenceSteps = MutableStateFlow(persistentListOf<SequenceStep>())
    val sequenceSteps: StateFlow<ImmutableList<SequenceStep>> = _sequenceSteps

    fun updateName(name: String) {
        _basicConfig.value = _basicConfig.value.copy(name = name)
    }

    fun updateHost(host: String) {
        _basicConfig.value = _basicConfig.value.copy(host = host)
    }

    fun updateAdvancedConfig(update: AdvancedSequenceSettings.() -> AdvancedSequenceSettings) {
        _advancedConfig.value = _advancedConfig.value.update()
    }

    fun addSequenceStep() {
        _sequenceSteps.value = _sequenceSteps.value.add(
            SequenceStep(
                type = _sequenceSteps.value.lastOrNull()?.type ?: SequenceStepType.UDP
            )
        )
    }

    fun deleteSequenceStep(stepId: String) {
        _sequenceSteps.value = _sequenceSteps.value.filter { it.id != stepId }.toPersistentList()
    }

    fun moveSequenceStep(from: Int, to: Int) {
        _sequenceSteps.value = _sequenceSteps.value.toMutableList().apply { add(to, removeAt(from)) }.toPersistentList()
    }

    private fun updateSequenceStep(stepId: String, onUpdate: (SequenceStep) -> SequenceStep) {
        _sequenceSteps.value = _sequenceSteps.value.map {
            if (it.id == stepId) onUpdate(it) else it
        }.toPersistentList()
    }

    fun updateStepPort(stepId: String, port: Int?) {
        updateSequenceStep(stepId) { it.copy(port = port) }
    }

    fun updateStepType(stepId: String, type: SequenceStepType) {
        updateSequenceStep(stepId) { it.copy(type = type) }
    }

    fun updateStepContent(stepId: String, content: String) {
        updateSequenceStep(stepId) { it.copy(content = content) }
    }

    fun updateStepIcmpSize(stepId: String, size: Int?) {
        updateSequenceStep(stepId) { it.copy(icmpSize = size) }
    }

    fun updateStepIcmpCount(stepId: String, count: Int?) {
        updateSequenceStep(stepId) { it.copy(icmpCount = count) }
    }

    fun updateStepEncoding(stepId: String, encoding: ContentEncodingType) {
        updateSequenceStep(stepId) { it.copy(encoding = encoding) }
    }

    private var order: Int? = null

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var _isSaving = mutableStateOf(false)
    val isSaving: Boolean
        get() = _isSaving.value

    private var _savedSequenceId = MutableStateFlow<Long?>(null)
    val savedSequenceId: StateFlow<Long?> = _savedSequenceId

    private var _error = mutableStateOf<String?>(null)
    val error: String?
        get() = _error.value

    @Suppress("TooGenericExceptionCaught")
    // Loads sequence data from the repository based on the provided ID
    private fun loadSequence(id: Long) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                repository.findSequence(id)?.let { sequence ->
                    _basicConfig.value = BasicSequenceSettings(
                        name = sequence.name ?: "",
                        host = sequence.host ?: ""
                    )

                    _advancedConfig.value = AdvancedSequenceSettings(
                        delay = sequence.delay,
                        localPort = sequence.localPort,
                        protocolVersion = sequence.ipv ?: ProtocolVersionType.PREFER_IPV4,
                        icmpSizeType = sequence.icmpType ?: IcmpType.WITHOUT_HEADERS,
                        appPackage = sequence.application,
                        ttl = sequence.ttl,
                        appName = sequence.applicationName,
                        uri = sequence.uri
                    )
                    _sequenceSteps.value = sequence.steps?.toPersistentList() ?: persistentListOf()
                    order = sequence.order
                } ?: { sequenceId = null }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    // Saves the current sequence configuration to the repository
    fun saveSequence() {
        _isSaving.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val currentBasicConfig = _basicConfig.value
                val advancedConfig = _advancedConfig.value
                _savedSequenceId.value = repository.saveSequence(
                    Sequence(
                        id = sequenceId,
                        name = currentBasicConfig.name,
                        host = currentBasicConfig.host,
                        order = order ?: ((repository.getMaxOrder() ?: 0) + 1),
                        delay = advancedConfig.delay,
                        application = advancedConfig.appPackage,
                        applicationName = advancedConfig.appName,
                        icmpType = advancedConfig.icmpSizeType,
                        steps = _sequenceSteps.value,
                        descriptionType = null, // TODO this will be deprecated
                        pin = null, // TODO this will be deprecated
                        ipv = advancedConfig.protocolVersion,
                        ttl = advancedConfig.ttl,
                        localPort = advancedConfig.localPort,
                        uri = advancedConfig.uri
                    )
                )
                repository.saveLogEntry(LogEntry(event = EventType.SEQUENCE_SAVED,
                    data = listOf(currentBasicConfig.name)))
                repository.sendEvent(AppEvent.SequenceSaved(sequenceName = _basicConfig.value.name))
            } catch (e: Exception) {
                Timber.e(e)
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isSaving.value = false
            }
        }
    }

    init {
        Timber.d("Initializing SequenceViewModel ($sequenceId)")
        sequenceId?.let { loadSequence(id = it) }
    }

    @AssistedFactory
    interface SequenceViewModelFactory {
        fun create(sequenceId: Long?): SequenceViewModel
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("SequenceViewModel ($sequenceId) has been cleared")
    }
}
