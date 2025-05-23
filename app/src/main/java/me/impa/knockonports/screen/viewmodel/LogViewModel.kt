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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(val repository: KnocksRepository): ViewModel() {

    private val _logEntries = repository.getLogEntries()
    val logEntries: StateFlow<List<LogEntry>> = _logEntries.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(), emptyList())

    fun clearLog() {
        viewModelScope.launch {
            repository.clearLogEntries()
        }
    }
}