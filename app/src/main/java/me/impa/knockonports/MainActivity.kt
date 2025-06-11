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
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import android.graphics.Color
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
            //val theme by settingsDataStore.themeSettings.collectAsStateWithLifecycle(initialValue = ThemeConfig())
            val theme by settingsDataStore.themeSettings.collectAsState(initial = null)
            var themeCache by rememberSaveable { mutableStateOf(theme) }
            val isSystemInDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(theme) {
                theme?.let { themeCache = theme }
            }
            LaunchedEffect(themeCache, isSystemInDarkTheme) {
                themeCache?.let {
                    enableEdgeToEdge(
                        statusBarStyle = when (it.useDarkTheme) {
                            DarkMode.AUTO -> if (isSystemInDarkTheme) {
                                SystemBarStyle.dark(Color.TRANSPARENT)
                            } else {
                                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                            }

                            DarkMode.LIGHT -> SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                            DarkMode.DARK -> SystemBarStyle.dark(Color.TRANSPARENT)
                        }
                    )
                }
            }
            themeCache?.let {
                KnockOnPortsTheme(config = it) {
                    AppScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(resourceWatcher)
        lifecycle.removeObserver(shortcutWatcher)
        super.onDestroy()
    }
}

