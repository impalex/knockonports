/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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

package me.impa.knockonports.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.materialkolor.Contrast
import com.materialkolor.dynamicColorScheme
import me.impa.knockonports.ui.config.ThemeConfig
import me.impa.knockonports.ui.config.ThemeContrast
import me.impa.knockonports.ui.config.isDark

val LocalThemeConfig = compositionLocalOf { ThemeConfig() }

@Composable
fun KnockOnPortsTheme(
    config: ThemeConfig = ThemeConfig(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalThemeConfig provides config) {
        val view = LocalView.current
        val useDarkTheme = config.useDarkTheme.isDark()

        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightNavigationBars = !useDarkTheme
                    isAppearanceLightStatusBars = !useDarkTheme
                }
            }
        }
        val context = LocalContext.current

        val scheme = remember(config) {
            if (config.useDynamicColors && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)) {
                if (useDarkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            } else {
                dynamicColorScheme(
                    seedColor = Color.fromColorLong(config.themeSeed),
                    isDark = useDarkTheme,
                    isAmoled = config.amoledMode,
                    contrastLevel = when (config.contrast) {
                        ThemeContrast.STANDARD -> Contrast.Default.value
                        ThemeContrast.MEDIUM -> Contrast.Medium.value
                        ThemeContrast.HIGH -> Contrast.High.value
                    }
                )
            }
        }

        MaterialTheme(colorScheme = scheme, content = content)

    }

}
