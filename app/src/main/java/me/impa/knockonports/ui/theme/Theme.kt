/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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

package me.impa.knockonports.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.collections.immutable.persistentMapOf
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeConfig
import me.impa.knockonports.ui.theme.variant.SkySteelScheme
import me.impa.knockonports.ui.theme.variant.GoldenDuskScheme
import me.impa.knockonports.ui.theme.variant.BlushStone
import me.impa.knockonports.ui.theme.variant.NeonMossScheme
import timber.log.Timber

val themeMap = persistentMapOf(
    "SKY_STEEL" to SkySteelScheme,
    "BLUSH_STONE" to BlushStone,
    "GOLDEN_DUSK" to GoldenDuskScheme,
    "NEON_MOSS" to NeonMossScheme
)

val LocalThemeConfig = compositionLocalOf { ThemeConfig() }

private fun selectTheme(
    darkMode: DarkMode, systemDark: Boolean,
    lightScheme: ColorScheme, darkScheme: ColorScheme
): ColorScheme {
    return when (darkMode) {
        DarkMode.AUTO -> if (systemDark) darkScheme else lightScheme
        DarkMode.DARK -> darkScheme
        DarkMode.LIGHT -> lightScheme
    }
}

@Composable
fun KnockOnPortsTheme(
    config: ThemeConfig = ThemeConfig(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalThemeConfig provides config) {
        val currentConfig = LocalThemeConfig.current
        val systemDark = isSystemInDarkTheme()

        val lightScheme = themeMap.getOrElse(currentConfig.customTheme) { themeMap.values.first() }
            .theme[DarkMode.LIGHT]!![currentConfig.contrast]!!
        val darkScheme = themeMap.getOrElse(currentConfig.customTheme) { themeMap.values.first() }
            .theme[DarkMode.DARK]!![currentConfig.contrast]!!

        Timber.d("currentConfig: $currentConfig")

        val colorScheme = if (currentConfig.useDynamicColors && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)) {
            selectTheme(
                currentConfig.useDarkTheme, systemDark,
                dynamicLightColorScheme(LocalContext.current), dynamicDarkColorScheme(LocalContext.current)
            )
        } else {
            selectTheme(currentConfig.useDarkTheme, systemDark, lightScheme, darkScheme)
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}
