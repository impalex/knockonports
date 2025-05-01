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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.impa.knockonports.data.settings.DeviceState
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.screen.viewmodel.state.settings.SettingsUiState
import me.impa.knockonports.screen.viewmodel.state.settings.ThemeUiState
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent
import me.impa.knockonports.screen.viewmodel.state.settings.UiOverlay
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    deviceState: DeviceState,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val isPlayStoreInstallation = deviceState.isPlayStoreInstallation

    val themeState = settingsDataStore.themeSettings.map {
        ThemeUiState(
            darkMode = it.useDarkTheme,
            dynamicColors = it.useDynamicColors,
            customTheme = it.customTheme,
            contrast = it.contrast,
        )
    }

    val appSettingsState = combine<Any, SettingsUiState>(
        settingsDataStore.widgetConfirmation,
        settingsDataStore.detectPublicIP,
        settingsDataStore.ipv4Service,
        settingsDataStore.ipv6Service,
        settingsDataStore.customIpv4Service,
        settingsDataStore.customIpv6Service,
        settingsDataStore.detailedListView,
        settingsDataStore.customIp4Header,
        settingsDataStore.ip4HeaderSize,
        settingsDataStore.resourceCheckPeriod,
    ) {
        SettingsUiState(
            widgetConfirmation = it[0] as Boolean,
            detectPublicIP = it[1] as Boolean,
            ipv4Service = it[2] as String,
            ipv6Service = it[3] as String,
            customIpv4Service = it[4] as String,
            customIpv6Service = it[5] as String,
            detailedListView = it[6] as Boolean,
            customIp4Header = it[7] as Boolean,
            ip4HeaderSize = it[8] as Int,
            resourceCheckPeriod = it[9] as Int,
        )
    }

    private val _overlay = MutableStateFlow<UiOverlay?>(null)
    val overlay: StateFlow<UiOverlay?> = _overlay

    @Suppress("CyclomaticComplexMethod")
    fun onEvent(event: UiEvent) = viewModelScope.launch {
        when (event) {
            UiEvent.ClearOverlay -> _overlay.update { null }
            UiEvent.ConfirmIPDetection -> settingsDataStore.setDetectPublicIP(true).also { _overlay.update { null } }
            is UiEvent.SetContrast -> settingsDataStore.setContrast(event.contrast)
            is UiEvent.SetCustomTheme -> settingsDataStore.setCustomTheme(event.theme)
            is UiEvent.SetDarkMode -> settingsDataStore.setDarkMode(event.darkMode)
            is UiEvent.SetDetailedView -> settingsDataStore.setDetailedListView(event.detailed)
            is UiEvent.SetDynamicMode -> settingsDataStore.setDynamicColors(event.dynamic)
            is UiEvent.SetIPDetection ->
                if (event.ipDetection) _overlay.update { UiOverlay.ConfirmIPDetection }
                else settingsDataStore.setDetectPublicIP(false)

            is UiEvent.SetIpv4Service -> settingsDataStore.setIpv4Service(event.service)
            is UiEvent.SetIpv6Service -> settingsDataStore.setIpv6Service(event.service)
            is UiEvent.SetWidgetConfirmation -> settingsDataStore.setWidgetConfirmation(event.confirmation)
            is UiEvent.SetCustomIpv4Service -> settingsDataStore.setCustomIpv4Service(event.service)
            is UiEvent.SetCustomIpv6Service -> settingsDataStore.setCustomIpv6Service(event.service)
            is UiEvent.SetCustomIPHeaderSize -> settingsDataStore.setIp4HeaderSize(event.size)
            is UiEvent.SetCustomIPHeaderSizeEnabled ->
                if (event.enabled) _overlay.update { UiOverlay.CustomIPHeaderAlert }
                else settingsDataStore.setCustomIp4Header(false)

            is UiEvent.ConfirmCustomIPHeaderSizeEnabled -> settingsDataStore.setCustomIp4Header(true)
                .also { _overlay.update { null } }

            is UiEvent.SetResourceCheckPeriod -> settingsDataStore.setResourceCheckPeriod(event.period)
        }
    }

}