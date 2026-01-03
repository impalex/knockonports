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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import me.impa.knockonports.R
import me.impa.knockonports.constants.TAG_APP_SCREEN
import me.impa.knockonports.constants.TAG_BACK_BUTTON
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.navigation.AppNavigation
import me.impa.knockonports.navigation.NAV_BUS
import me.impa.knockonports.navigation.NavigateUp
import me.impa.knockonports.navigation.Navigator
import me.impa.knockonports.navigation.rememberNavigationState
import me.impa.knockonports.screen.component.common.AppTopBar
import me.impa.knockonports.screen.component.common.AppTopBarRegistry
import me.impa.knockonports.screen.component.common.LocalAppBarRegistry
import me.impa.knockonports.screen.component.common.LocalAppEventBus
import me.impa.knockonports.screen.component.common.LocalInnerPaddingValues
import me.impa.knockonports.screen.viewmodel.AppViewModel

@Composable
fun AppScreen(
    backStack: List<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel(),
) {
    val isLocked by viewModel.isLocked.collectAsState()

    if (isLocked) {
        val context = LocalContext.current
        viewModel.biometricHelper.launchBiometricPrompt(
            LocalContext.current, stringResource(R.string.title_lock_app),
            stringResource(R.string.text_lock_app), onSuccess = { viewModel.unlock() },
            onUnavailable = { viewModel.unlock() },
            onError = { (context as? Activity)?.finish() })
    } else {
        CompositionLocalProvider(LocalAppEventBus provides viewModel.eventBus) {
            AppScreenContent(modifier, backStack)
        }
    }
}

@Composable
private fun AppScreenContent(
    modifier: Modifier,
    backStack: List<NavKey>
) {
    val navigationState = rememberNavigationState(backStack)
    val navigator = remember { Navigator(navigationState) }

    NavigationEffects(navigator)

    Surface(
        modifier = modifier.then(Modifier.testTag(TAG_APP_SCREEN)),
        color = MaterialTheme.colorScheme.background
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val appBarConfigs = remember { mutableStateMapOf<String, AppTopBar>() }
        val appBarRegistry = remember {
            object : AppTopBarRegistry {
                override fun registerAppBar(key: String, topBar: AppTopBar) {
                    appBarConfigs[key] = topBar
                }

                override fun unregisterAppBar(key: String) {
                    appBarConfigs.remove(key)
                }
            }
        }

        CompositionLocalProvider(LocalAppBarRegistry provides appBarRegistry) {
            Scaffold(
                modifier = modifier,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    AppScreenTopBar(
                        currentRoute = navigationState.backStack.lastOrNull(),
                        appBarConfigs = appBarConfigs,
                        onNavigateUp = { navigator.goBack() }
                    )
                }
            ) { paddingValues ->
                EventHandler(snackbarHostState = snackbarHostState)

                CompositionLocalProvider(LocalInnerPaddingValues provides paddingValues) {
                    AppNavigation(navigator, navigationState)
                }
            }
        }
    }
}

@Composable
private fun NavigationEffects(navigator: Navigator) {
    val eventBus = LocalAppEventBus.current
    LaunchedEffect(eventBus.channelMap[NAV_BUS]) {
        eventBus.getEventFlow<Any>(NAV_BUS).collect { event ->
            when (event) {
                is NavigateUp -> navigator.goBack()
                is NavKey -> navigator.navigate(event)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScreenTopBar(
    currentRoute: NavKey?,
    appBarConfigs: Map<String, AppTopBar>,
    onNavigateUp: () -> Unit,
) {
    var cachedBarConfig by remember { mutableStateOf<AppTopBar?>(null) }
    val currentBar = currentRoute?.let {
        appBarConfigs[it::class.toString()]
    } ?: cachedBarConfig

    @Suppress("AssignedValueIsNeverRead")
    cachedBarConfig = currentBar

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        navigationIcon = {
            if (currentBar?.showBack == true) {
                IconButton(
                    onClick = onNavigateUp,
                    modifier = Modifier.testTag(TAG_BACK_BUTTON)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        title = {
            currentBar?.let {
                Text(text = it.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        actions = {
            currentBar?.actions?.invoke(this)
        }
    )
}

@Composable
fun EventHandler(snackbarHostState: SnackbarHostState) {
    val eventBus = LocalAppEventBus.current

    val resources = LocalResources.current

    val context = LocalContext.current

    LaunchedEffect(eventBus) {
        eventBus.getEventFlow<AppEvent>().collect { currentEvent ->
            val message = when (val event = currentEvent as AppEvent) {
                is AppEvent.SequenceSaved -> resources.getString(
                    R.string.message_sequence_saved,
                    event.sequenceName?.takeIf { it.isNotBlank() }
                        ?: resources.getString(R.string.text_unnamed_sequence)
                )

                is AppEvent.SequenceRemoved -> resources.getString(
                    R.string.message_sequence_deleted,
                    event.sequenceName?.takeIf { it.isNotBlank() }
                        ?: resources.getString(R.string.text_unnamed_sequence)
                )

                is AppEvent.SequenceListExported ->
                    resources.getQuantityString(R.plurals.message_sequence_export, event.count, event.count)

                is AppEvent.SequenceListImported ->
                    resources.getQuantityString(R.plurals.message_sequence_import, event.count, event.count)

                is AppEvent.GeneralError -> resources.getString(
                    R.string.text_error_general,
                    event.error.asString(context)
                )

                is AppEvent.GeneralMessage -> event.message.asString(context)
            }
            snackbarHostState.showSnackbar(message)
        }
    }
}
