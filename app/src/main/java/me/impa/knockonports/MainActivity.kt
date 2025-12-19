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

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import me.impa.knockonports.data.settings.DeviceState
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.screen.AppScreen
import me.impa.knockonports.service.biometric.BiometricHelper
import me.impa.knockonports.service.resource.AccessWatcher
import me.impa.knockonports.service.shortcut.ShortcutWatcher
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.theme.KnockOnPortsTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var shortcutWatcher: ShortcutWatcher

    @Inject
    lateinit var resourceWatcher: AccessWatcher

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var deviceState: DeviceState

    @Inject
    lateinit var biometricHelper: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        lifecycle.addObserver(resourceWatcher)
        lifecycle.addObserver(shortcutWatcher)
        lifecycle.addObserver(biometricHelper)
        intent?.data?.path?.let {
            intent = intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        setContent {
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

            themeCache?.let { theme ->
                KnockOnPortsTheme(config = theme) {
                    AppScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(resourceWatcher)
        lifecycle.removeObserver(shortcutWatcher)
        lifecycle.removeObserver(biometricHelper)
        super.onDestroy()
    }

}
