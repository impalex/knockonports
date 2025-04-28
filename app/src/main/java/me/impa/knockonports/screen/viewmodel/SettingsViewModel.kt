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

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.settings.AppSettings
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent
import me.impa.knockonports.screen.viewmodel.state.settings.UiOverlay
import me.impa.knockonports.ui.config.ThemeConfig
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: KnocksRepository) : ViewModel() {

    val appSettings = repository.getAppSettings()

    val themeSettings = repository.getThemeSettings()

    val appState = repository.getAppState()

    private val _overlay = MutableStateFlow<UiOverlay?>(null)
    val overlay: StateFlow<UiOverlay?> = _overlay

    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.ClearOverlay -> _overlay.update { null }
            UiEvent.ConfirmIPDetection -> updateAppSettings { it.copy(detectPublicIP = true) }
                .also { _overlay.update { null } }

            is UiEvent.SetContrast -> updateThemeSettings { it.copy(contrast = event.contrast) }
            is UiEvent.SetCustomTheme -> updateThemeSettings { it.copy(customTheme = event.theme) }
            is UiEvent.SetDarkMode -> updateThemeSettings { it.copy(useDarkTheme = event.darkMode) }
            is UiEvent.SetDetailedView -> updateAppSettings { it.copy(detailedListView = event.detailed) }
            is UiEvent.SetDynamicMode -> updateThemeSettings { it.copy(useDynamicColors = event.dynamic) }
            is UiEvent.SetIPDetection ->
                if (event.ipDetection) _overlay.update { UiOverlay.ConfirmIPDetection }
                else updateAppSettings { it.copy(detectPublicIP = false) }

            is UiEvent.SetIpv4Service -> updateAppSettings { it.copy(ipv4Service = event.service) }
            is UiEvent.SetIpv6Service -> updateAppSettings { it.copy(ipv6Service = event.service) }
            is UiEvent.SetWidgetConfirmation -> updateAppSettings { it.copy(widgetConfirmation = event.confirmation) }
            is UiEvent.SetCustomIpv4Service -> updateAppSettings { it.copy(customIpv4Service = event.service) }
            is UiEvent.SetCustomIpv6Service -> updateAppSettings { it.copy(customIpv6Service = event.service) }
            is UiEvent.SetCustomIPHeaderSize -> updateAppSettings { it.copy(ip4HeaderSize = event.size) }
            is UiEvent.SetCustomIPHeaderSizeEnabled ->
                if (event.enabled) _overlay.update { UiOverlay.CustomIPHeaderAlert }
                else updateAppSettings { it.copy(customIp4Header = false) }

            is UiEvent.ConfirmCustomIPHeaderSizeEnabled -> updateAppSettings { it.copy(customIp4Header = true) }
                .also { _overlay.update { null } }
        }
    }

    private fun updateAppSettings(onUpdate: (AppSettings) -> AppSettings) {
        repository.updateAppSettings(onUpdate(appSettings.value))
    }

    private fun updateThemeSettings(onUpdate: (ThemeConfig) -> ThemeConfig) {
        repository.updateThemeSettings(onUpdate(themeSettings.value))
    }
}