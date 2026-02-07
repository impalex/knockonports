/*
 * Copyright (c) 2026 Alexander Yaburov
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
 * See the License for the apecific language governing permissions and
 * limitations under the License.
 */

package me.impa.knockonports.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.impa.knockonports.R
import me.impa.knockonports.navigation.WearRoute
import me.impa.knockonports.screen.component.common.RegisterAppBar
import me.impa.knockonports.screen.viewmodel.WearViewModel
import me.impa.knockonports.screen.viewmodel.state.wear.UiEvent
import me.impa.knockonports.screen.viewmodel.state.wear.UiState
import me.impa.knockonports.service.wear.WearConnectionStatus
import me.impa.knockonports.ui.theme.KnockOnPortsTheme

@Composable
fun WearScreen(modifier: Modifier = Modifier, viewModel: WearViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterAppBar<WearRoute>(title = stringResource(R.string.title_screen_wear),
        showBackButton = true)

    WearScreenContent(state, viewModel::onEvent, modifier)
}

@Composable
private fun WearScreenContent(state: UiState, onEvent: (UiEvent) -> Unit, modifier: Modifier = Modifier) {
    when(state.wearStatus) {
        WearConnectionStatus.AppNotInstalled -> WearAppNotInstalled(onInstall = { onEvent(UiEvent.RemoteInstall) },
            modifier)
        WearConnectionStatus.Checking -> WearChecking(modifier)
        WearConnectionStatus.NotAvailable -> WearNotAvailable(modifier)
        WearConnectionStatus.Ready -> WearReady(modifier)
    }
}

@Composable
private fun WearTitleAndText(title: String, text: String) {
    Icon(painter = painterResource(R.drawable.watch_icon), contentDescription = null,
        modifier = Modifier.size(96.dp))
    Text(text = title,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge)
    Text(text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyLarge)

}

@Composable
private fun WearAppNotInstalled(onInstall: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.then(Modifier.safeContentPadding().padding(16.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        WearTitleAndText(title = stringResource(R.string.title_companion_not_installed),
            text = stringResource(R.string.text_companion_not_installed))
        Button(onClick = onInstall) {
            Text(text = stringResource(R.string.action_companion_open_market))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WearChecking(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.then(Modifier.safeContentPadding().padding(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(96.dp))
    }
}

@Composable
private fun WearNotAvailable(modifier: Modifier = Modifier) {
    Column(modifier = modifier.then(Modifier.safeContentPadding().padding(16.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        WearTitleAndText(title = stringResource(R.string.title_companion_lost),
            text = stringResource(R.string.text_companion_lost))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WearReady(modifier: Modifier = Modifier) {
    Column(modifier = modifier.then(Modifier.safeContentPadding().padding(16.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        WearTitleAndText(title = stringResource(R.string.title_companion_connected),
            text = stringResource(R.string.text_companion_connected))
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun WearAppNotInstalledPreview() {
    KnockOnPortsTheme {
        WearAppNotInstalled(onInstall = {}, modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun WearCheckingPreview() {
    KnockOnPortsTheme {
        WearChecking(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun WearNotAvailablePreview() {
    KnockOnPortsTheme {
        WearNotAvailable(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun WearReadyPreview() {
    KnockOnPortsTheme {
        WearReady(modifier = Modifier.fillMaxSize())
    }
}