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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

data class AppTopBar(
    val showBack: Boolean,
    val title: String,
    val actions: @Composable RowScope.() -> Unit = {}
)

interface AppTopBarRegistry {
    fun registerAppBar(key: String, topBar: AppTopBar)
    fun unregisterAppBar(key: String)
}

val LocalAppBarRegistry = staticCompositionLocalOf<AppTopBarRegistry?> { null }