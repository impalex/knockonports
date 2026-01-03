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

package me.impa.knockonports.screen.component.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import me.impa.knockonports.data.event.AppEventBus

object LocalAppEventBus {
    private val LocalEventBus: ProvidableCompositionLocal<AppEventBus?> =
        staticCompositionLocalOf { null }

    val current: AppEventBus
        @Composable
        get() = if (LocalView.current.isInEditMode) AppEventBus() else
            LocalEventBus.current ?: error("LocalEventBus not provided")

    infix fun provides(bus: AppEventBus): ProvidedValue<AppEventBus?> = LocalEventBus.provides(bus)
}