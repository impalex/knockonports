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
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeContrast
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: KnocksRepository): ViewModel() {

    val appSettings = repository.getAppSettings()

    val themeSettings = repository.getThemeSettings()

    val isInstalledFromPlayStore = repository.isInstalledFromPlayStore()

    fun setDynamicMode(dynamic: Boolean) {
        repository.updateThemeSettings(themeSettings.value.copy(useDynamicColors = dynamic))
    }

    fun setDarkMode(darkMode: DarkMode) {
        repository.updateThemeSettings(themeSettings.value.copy(useDarkTheme = darkMode))
    }

    fun setContrast(contrast: ThemeContrast) {
        repository.updateThemeSettings(themeSettings.value.copy(contrast = contrast))
    }

    fun setCustomTheme(themeTag: String) {
        repository.updateThemeSettings(themeSettings.value.copy(customTheme = themeTag))
    }

    fun setWidgetConfirmation(confirmation: Boolean) {
        repository.updateAppSettings(appSettings.value.copy(widgetConfirmation = confirmation))
    }

    fun setIPDetection(detection: Boolean) {
        repository.updateAppSettings(appSettings.value.copy(detectPublicIP = detection))
    }

    fun setDetailedView(detailed: Boolean) {
        repository.updateAppSettings(appSettings.value.copy(detailedListView = detailed))
    }

    fun setIpv4Service(service: String) {
        repository.updateAppSettings(appSettings.value.copy(ipv4Service = service))
    }

    fun setIpv6Service(service: String) {
        repository.updateAppSettings(appSettings.value.copy(ipv6Service = service))
    }
}