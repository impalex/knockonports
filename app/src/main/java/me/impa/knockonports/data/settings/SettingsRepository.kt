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

package me.impa.knockonports.data.settings

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import me.impa.knockonports.ui.config.ThemeConfig

interface SettingsRepository {
    val appSettings: StateFlow<AppSettings>
    val themeSettings: StateFlow<ThemeConfig>
    val knockCount: State<Long>
    val doNotAskAboutNotifications: State<Boolean>
    val firstLaunchV2: State<Boolean>
    val askReviewTime: State<Long>
    val doNotAskForReview: State<Boolean>
    val isInstalledFromPlayStore: State<Boolean>
    fun updateAppSettings(newSettings: AppSettings)
    fun updateThemeSettings(newSettings: ThemeConfig)
    fun incrementKnockCount()
    fun setDoNotAskAboutNotificationsFlag()
    fun postponeReviewRequest(time: Long)
    fun doNotAskForReview()
    fun clearFirstLaunchV2()
}
