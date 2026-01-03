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

package me.impa.knockonports.ui.config

import android.os.Parcelable
import androidx.annotation.ColorLong
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import kotlinx.parcelize.Parcelize

// Old themes
@Suppress("MagicNumber")
val defaultThemes = mapOf(
    "SKY_STEEL" to Color(0xFF5A93FF).toColorLong(),
    "BLUSH_STONE" to Color(0xFFFFBEC3).toColorLong(),
    "GOLDEN_DUSK" to Color(0xFFE3CD00).toColorLong(),
    "NEON_MOSS" to Color(0xFF00E24E).toColorLong()
)

@Immutable
@Parcelize
data class ThemeConfig(
    val useDynamicColors: Boolean = true,
    val useDarkTheme: DarkMode = DarkMode.AUTO,
    val contrast: ThemeContrast = ThemeContrast.STANDARD,
    val amoledMode: Boolean = false,
    @ColorLong val themeSeed: Long = requireNotNull(defaultThemes["SKY_STEEL"])
) : Parcelable
