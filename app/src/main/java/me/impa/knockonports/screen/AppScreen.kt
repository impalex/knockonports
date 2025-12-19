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

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import me.impa.knockonports.R
import me.impa.knockonports.constants.TAG_APP_SCREEN
import me.impa.knockonports.constants.TAG_BACK_BUTTON
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.navigation.AppNavGraph
import me.impa.knockonports.navigation.AppNavigation
import me.impa.knockonports.screen.viewmodel.AppViewModel

@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    startDestination: AppNavGraph = AppNavGraph.MainRoute,
    viewModel: AppViewModel = hiltViewModel(),
) {
    val isLocked by viewModel.isLocked.collectAsState()
    val currentEvent by viewModel.eventFlow.collectAsState()

    if (isLocked) {
        val context = LocalContext.current
        viewModel.biometricHelper.launchBiometricPrompt(
            LocalContext.current, stringResource(R.string.title_lock_app),
            stringResource(R.string.text_lock_app), onSuccess = { viewModel.unlock() },
            onUnavailable = { viewModel.unlock() },
            onError = { (context as? Activity)?.finish() })
    } else {
        AppScreenContent(modifier, startDestination, currentEvent, viewModel::clearEvent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScreenContent(
    modifier: Modifier,
    startDestination: AppNavGraph,
    currentEvent: AppEvent?,
    onClearEvent: () -> Unit
) {
    val navController = rememberNavController()

    Surface(
        modifier = modifier.then(Modifier.testTag(TAG_APP_SCREEN)),
        color = MaterialTheme.colorScheme.background
    ) {
        var appBarState by remember { mutableStateOf(AppBarState()) }
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(),
                    navigationIcon = {
                        if (appBarState.backAvailable) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                },
                                modifier = Modifier.testTag(TAG_BACK_BUTTON)
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                            }
                        }
                    },
                    title = {
                        Text(text = appBarState.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    actions = {
                        appBarState.actions?.invoke(this)
                    }
                )
            }
        ) { paddingValues ->
            currentEvent?.let {
                EventHandler(
                    event = it,
                    snackbarHostState = snackbarHostState,
                    onEventHandled = onClearEvent
                )
            }
            AppNavigation(
                startDestination = startDestination, onComposing = {
                    @Suppress("AssignedValueIsNeverRead")
                    appBarState = it
                }, navController = navController, innerPaddingValues = paddingValues, modifier = Modifier
            )
        }
    }
}

@Composable
fun EventHandler(event: AppEvent, snackbarHostState: SnackbarHostState, onEventHandled: () -> Unit) {
    val message = when (event) {
        is AppEvent.SequenceSaved -> stringResource(
            R.string.message_sequence_saved,
            event.sequenceName?.takeIf { it.isNotBlank() } ?: stringResource(R.string.text_unnamed_sequence)
        )

        is AppEvent.SequenceRemoved -> stringResource(
            R.string.message_sequence_deleted,
            event.sequenceName?.takeIf { it.isNotBlank() } ?: stringResource(R.string.text_unnamed_sequence)
        )

        is AppEvent.SequenceListExported ->
            pluralStringResource(R.plurals.message_sequence_export, event.count, event.count)

        is AppEvent.SequenceListImported ->
            pluralStringResource(R.plurals.message_sequence_import, event.count, event.count)

        is AppEvent.GeneralError -> stringResource(R.string.text_error_general, event.error.asString())
        is AppEvent.GeneralMessage -> event.message.asString()
    }
    LaunchedEffect(message) {
        snackbarHostState.showSnackbar(message)
        onEventHandled()
    }
}
