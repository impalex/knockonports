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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import me.impa.knockonports.R
import me.impa.knockonports.extension.OnDestination
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.navigation.AppNavGraph
import me.impa.knockonports.screen.component.settings.DetectIPAlert
import me.impa.knockonports.screen.component.settings.IPHeaderSizeAlert
import me.impa.knockonports.screen.component.settings.aboutSection
import me.impa.knockonports.screen.component.settings.generalSection
import me.impa.knockonports.screen.component.settings.themeSection
import me.impa.knockonports.screen.viewmodel.SettingsViewModel
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent
import me.impa.knockonports.screen.viewmodel.state.settings.UiOverlay

@Composable
fun SettingsScreen(
    onComposing: (AppBarState) -> Unit,
    navController: NavController,
    innerPaddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.appSettingsState.collectAsStateWithLifecycle(initialValue = null)
    val theme by viewModel.themeState.collectAsStateWithLifecycle(initialValue = null)
    val isPlayStoreInstallation = viewModel.isPlayStoreInstallation
    val overlay by viewModel.overlay.collectAsState()
    val title = stringResource(R.string.title_screen_settings)
    val lazyListState = rememberLazyListState()
    navController.OnDestination<AppNavGraph.SettingsRoute> {
        LaunchedEffect(key1 = true) {
            onComposing(
                AppBarState(
                    title = title,
                    backAvailable = true,
                    actions = null
                )
            )
        }
    }

    overlay?.let { ShowOverlay(it, viewModel::onEvent) }

    LazyColumn(
        state = lazyListState,
        contentPadding = innerPaddingValues,
        modifier = modifier.fillMaxSize()
    ) {
        settings?.let {
            generalSection(
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
fun ShowOverlay(overlay: UiOverlay, onEvent: (UiEvent) -> Unit) {
    when (overlay) {
        UiOverlay.ConfirmIPDetection -> DetectIPAlert(onEvent)
        UiOverlay.CustomIPHeaderAlert -> IPHeaderSizeAlert(onEvent)
    }
}