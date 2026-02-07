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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.impa.knockonports.screen.viewmodel.state.wear.UiEvent
import me.impa.knockonports.screen.viewmodel.state.wear.UiState
import me.impa.knockonports.service.wear.WearConnectionManager
import javax.inject.Inject

@HiltViewModel
class WearViewModel @Inject constructor(
    private val wearConnectionManager: WearConnectionManager
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.RemoteInstall -> viewModelScope.launch { wearConnectionManager.openPlayStore() }
        }
    }

    init {
        viewModelScope.launch {
            _state.update { it.copy(wearStatus = wearConnectionManager.getStatus()) }
        }
    }

}