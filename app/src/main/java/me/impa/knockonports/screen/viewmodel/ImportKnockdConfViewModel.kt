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

package me.impa.knockonports.screen.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.event.AppEventBus
import me.impa.knockonports.extension.navigate
import me.impa.knockonports.navigation.NavigateUp
import me.impa.knockonports.screen.viewmodel.state.importknockd.SeqUiState
import me.impa.knockonports.screen.viewmodel.state.importknockd.UiEvent
import me.impa.knockonports.screen.viewmodel.state.importknockd.UiState
import me.impa.knockonports.screen.viewmodel.state.main.KnockdImportResults

@HiltViewModel(assistedFactory = ImportKnockdConfViewModel.ImportKnockdConfViewModelFactory::class)
class ImportKnockdConfViewModel @AssistedInject constructor(
    @Assisted private var uri: String,
    @Assisted private var singleChoice: Boolean,
    private val repository: KnocksRepository,
    private val eventBus: AppEventBus
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)

    val state: StateFlow<UiState> = _state

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SelectSequence -> (_state.value as? UiState.MultiChoice)?.let { ready ->
                _state.value = ready.copy(
                    seqList = ready.seqList.map {
                        if (it.sequence.id == event.id)
                            it.copy(checked = event.state)
                        else
                            it
                    })
            }

            UiEvent.ImportMultiple -> (_state.value as? UiState.MultiChoice)?.let {
                viewModelScope.launch { saveSequences(it.host, it.seqList) }
            }

            is UiEvent.ChangeHost -> (_state.value as? UiState.MultiChoice)?.let {
                _state.value = it.copy(host = event.host)
            }

            is UiEvent.ImportSingle -> (_state.value as? UiState.SingleChoice)?.let {
                it.seqList.firstOrNull { seq -> seq.sequence.id == event.id }?.let { seq ->
                    eventBus.sendEvent(event = KnockdImportResults(
                        steps = seq.sequence.steps?.map { step ->
                            step.port!! to step.type!!
                        }?.toMap() ?: mapOf()
                    ))
                    eventBus.navigate(NavigateUp)
                }
            }
        }
    }

    private suspend fun saveSequences(host: String, sequences: List<SeqUiState>) {
        _state.value = UiState.Loading
        val maxOrder = repository.getMaxOrder()?.plus(1) ?: 0
        val seqList = sequences.filter { it.checked }.mapIndexed { index, state ->
            state.sequence.copy(
                id = null,
                host = host,
                order = maxOrder + index
            )
        }
        repository.saveSequences(seqList)
        eventBus.navigate(NavigateUp)
    }

    private suspend fun readConfig(uri: String) {
        val dataUri = uri.toUri()

        try {
            val sequences = repository.readSequencesFromKnockdConf(dataUri)
            _state.value = if (sequences.isEmpty()) {
                UiState.NothingToImport
            } else {
                val sequences = sequences.map {
                    SeqUiState(checked = true, it)
                }
                if (singleChoice)
                    UiState.SingleChoice(sequences)
                else
                    UiState.MultiChoice(host = "", seqList = sequences)
            }
        } catch (e: Exception) {
            _state.value = UiState.UnableToImport(uri, e.message ?: "")
        }
    }

    init {
        viewModelScope.launch {
            readConfig(uri)
        }
    }

    @AssistedFactory
    interface ImportKnockdConfViewModelFactory {
        fun create(uri: String, singleChoice: Boolean): ImportKnockdConfViewModel
    }

}