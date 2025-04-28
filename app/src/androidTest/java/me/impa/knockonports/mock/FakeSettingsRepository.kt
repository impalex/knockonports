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

package me.impa.knockonports.mock

import android.os.Build
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import me.impa.knockonports.data.settings.AppSettings
import me.impa.knockonports.data.settings.AppState
import me.impa.knockonports.data.settings.SettingsRepository
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeConfig

object FakeSettingsRepository : SettingsRepository {
    override val appSettings: StateFlow<AppSettings>
        get() = TODO("Not yet implemented")
    override val themeSettings: StateFlow<ThemeConfig>
        get() = MutableStateFlow(
            ThemeConfig(useDarkTheme = DarkMode.DARK)
        )
    override val appState: StateFlow<AppState>
        get() = MutableStateFlow(AppState())

    override fun updateAppSettings(newSettings: AppSettings) {
        TODO("Not yet implemented")
    }

    override fun updateThemeSettings(newSettings: ThemeConfig) {
        TODO("Not yet implemented")
    }

    override fun incrementKnockCount() {
        TODO("Not yet implemented")
    }

    override fun setDoNotAskAboutNotificationsFlag() {
        TODO("Not yet implemented")
    }

    override fun postponeReviewRequest(time: Long) {
        TODO("Not yet implemented")
    }

    override fun doNotAskForReview() {
        TODO("Not yet implemented")
    }

    override fun clearFirstLaunchV2() {
        TODO("Not yet implemented")
    }
}