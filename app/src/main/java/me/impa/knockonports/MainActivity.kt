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

package me.impa.knockonports

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.screen.AppScreen
import me.impa.knockonports.service.resource.AccessWatcher
import me.impa.knockonports.service.shortcut.ShortcutWatcher
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeConfig
import me.impa.knockonports.ui.theme.KnockOnPortsTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var shortcutWatcher: ShortcutWatcher

    @Inject
    lateinit var resourceWatcher: AccessWatcher

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(resourceWatcher)
        lifecycle.addObserver(shortcutWatcher)
        setContent {
            val theme by settingsDataStore.themeSettings.collectAsStateWithLifecycle(initialValue = ThemeConfig())
            val view = LocalView.current
            val isSystemInDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(theme, isSystemInDarkTheme) {
                val window = (this@MainActivity).window
                val darkMode = when(theme.useDarkTheme) {
                    DarkMode.AUTO -> isSystemInDarkTheme
                    DarkMode.LIGHT -> false
                    DarkMode.DARK -> true
                }
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkMode
            }
            KnockOnPortsTheme (config = theme) {
                AppScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(resourceWatcher)
        lifecycle.removeObserver(shortcutWatcher)
        super.onDestroy()
    }
}

