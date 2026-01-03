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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.impa.knockonports.data.event.AppEventBus
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.service.biometric.BiometricHelper
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    val settings: SettingsDataStore,
    val eventBus: AppEventBus
) : ViewModel() {

    private val _isLocked = MutableStateFlow(false)

    val isLocked: StateFlow<Boolean> = _isLocked

    fun unlock() {
        _isLocked.value = false
    }

    init {
        viewModelScope.launch {
            val biometricState = biometricHelper.state.first()
            val appLockState = settings.isAppLockEnabled.first()
            _isLocked.value = biometricState && appLockState
        }
    }

}