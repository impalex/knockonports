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

package me.impa.knockonports.screen.viewmodel.state.settings

import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeContrast

sealed interface UiEvent {
    data class SetDynamicMode(val dynamic: Boolean) : UiEvent
    data class SetDarkMode(val darkMode: DarkMode) : UiEvent
    data class SetContrast(val contrast: ThemeContrast) : UiEvent
    data class SetCustomTheme(val theme: String) : UiEvent
    data class SetWidgetConfirmation(val confirmation: Boolean) : UiEvent
    data class SetIPDetection(val ipDetection: Boolean) : UiEvent
    data object ConfirmIPDetection : UiEvent
    data class SetDetailedView(val detailed: Boolean) : UiEvent
    data class SetIpv4Service(val service: String) : UiEvent
    data class SetIpv6Service(val service: String) : UiEvent
    data class SetCustomIpv4Service(val service: String) : UiEvent
    data class SetCustomIpv6Service(val service: String) : UiEvent
    data object ClearOverlay : UiEvent
    data class SetCustomIPHeaderSizeEnabled(val enabled: Boolean) : UiEvent
    data class SetCustomIPHeaderSize(val size: Int) : UiEvent
    data object ConfirmCustomIPHeaderSizeEnabled : UiEvent
}