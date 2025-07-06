/*
 * Copyright (c) 2025 Alexander Yaburov
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

package me.impa.knockonports.screen.component.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import me.impa.knockonports.R
import me.impa.knockonports.extension.OnDestination
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.navigation.AppNavGraph
import me.impa.knockonports.screen.viewmodel.state.main.MainBarEvent
import me.impa.knockonports.screen.viewmodel.state.main.UiEvent

@Composable
fun NavController.UpdateAppBar(
    isRuLangAvailable: Boolean,
    onComposing: (AppBarState) -> Unit, onEvent: (UiEvent) -> Unit
) {
    val title = stringResource(R.string.app_name)
    OnDestination<AppNavGraph.MainRoute> {
        LaunchedEffect(true) {
            onComposing(
                AppBarState(
                    title = title,
                    backAvailable = false,
                    actions = {
                        MainScreenActions(isRuLangAvailable) { action ->
                            when (action) {
                                is MainBarEvent.AddSequence -> this@UpdateAppBar.navigate(AppNavGraph.SequenceRoute())
                                is MainBarEvent.Import -> onEvent(UiEvent.Import(action.uri))
                                is MainBarEvent.Export -> onEvent(UiEvent.Export(action.uri))
                                is MainBarEvent.ShowLogs -> this@UpdateAppBar.navigate(AppNavGraph.LogRoute)
                                is MainBarEvent.Settings -> this@UpdateAppBar.navigate(AppNavGraph.SettingsRoute)
                            }
                        }
                    }
                )
            )
        }
    }

}