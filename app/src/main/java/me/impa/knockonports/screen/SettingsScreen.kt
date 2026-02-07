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

package me.impa.knockonports.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.impa.knockonports.R
import me.impa.knockonports.helper.safeBottomContentPadding
import me.impa.knockonports.navigation.SettingsRoute
import me.impa.knockonports.screen.component.common.RegisterAppBar
import me.impa.knockonports.screen.component.settings.DetectIPAlert
import me.impa.knockonports.screen.component.settings.IPHeaderSizeAlert
import me.impa.knockonports.screen.component.settings.aboutSection
import me.impa.knockonports.screen.component.settings.generalSection
import me.impa.knockonports.screen.component.settings.listSection
import me.impa.knockonports.screen.component.settings.themeSection
import me.impa.knockonports.screen.viewmodel.SettingsViewModel
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent
import me.impa.knockonports.screen.viewmodel.state.settings.UiOverlay
import me.impa.knockonports.service.biometric.BiometricHelper

@Composable
@Suppress("ReturnCount")
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.appSettingsState.collectAsStateWithLifecycle(initialValue = null)
    val theme by viewModel.themeState.collectAsStateWithLifecycle(initialValue = null)
    val listSettings by viewModel.listState.collectAsStateWithLifecycle(initialValue = null)
    val isPlayStoreInstallation = viewModel.isPlayStoreInstallation
    val biometricHelper = viewModel.biometricHelper
    val overlay by viewModel.overlay.collectAsState()
    val title = stringResource(R.string.title_screen_settings)
    val lazyListState = rememberLazyListState()

    RegisterAppBar<SettingsRoute>(title = title, showBackButton = true)

    overlay?.let { ShowOverlay(LocalContext.current, biometricHelper, it, viewModel::onEvent) }

    // Wait for data
    settings ?: return
    theme ?: return
    listSettings ?: return

    LazyColumn(
        state = lazyListState,
        contentPadding = safeBottomContentPadding(),
        modifier = modifier.fillMaxSize()
    ) {
        settings?.let {
            generalSection(
                config = it,
                onEvent = viewModel::onEvent
            )
        }
        listSettings?.let {
            listSection(
                config = it,
                onEvent = viewModel::onEvent
            )
        }
        theme?.let {
            themeSection(
                theme = it,
                onEvent = viewModel::onEvent
            )
        }
        aboutSection(isPlayStoreInstallation)
    }
}

@Composable
fun ShowOverlay(context: Context, biometricHelper: BiometricHelper, overlay: UiOverlay, onEvent: (UiEvent) -> Unit) {
    when (overlay) {
        UiOverlay.ConfirmIPDetection -> DetectIPAlert(onEvent)
        UiOverlay.CustomIPHeaderAlert -> IPHeaderSizeAlert(onEvent)
        UiOverlay.OpenSecuritySettings ->
            onEvent(UiEvent.ClearOverlay).also { context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS)) }

        is  UiOverlay.PerformAuth ->
            onEvent(UiEvent.ClearOverlay).also {
                biometricHelper.launchBiometricPrompt(context,
                    title = context.getString(R.string.title_lock_config),
                    subtitle = context.getString(R.string.text_lock_config),
                    onSuccess = { onEvent(UiEvent.SetAuthState(overlay.enableAuth)) }
                    )
            }

    }
}