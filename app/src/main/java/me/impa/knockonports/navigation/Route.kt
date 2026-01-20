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

package me.impa.knockonports.navigation

import androidx.annotation.ColorLong
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import kotlinx.serialization.Serializable
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.screen.ColorPickerScreen
import me.impa.knockonports.screen.ImportKnockdConfScreen
import me.impa.knockonports.screen.LogScreen
import me.impa.knockonports.screen.MainScreen
import me.impa.knockonports.screen.SequenceScreen
import me.impa.knockonports.screen.SettingsScreen
import me.impa.knockonports.screen.viewmodel.ColorPickerViewModel
import me.impa.knockonports.screen.viewmodel.ImportKnockdConfViewModel
import me.impa.knockonports.screen.viewmodel.SequenceViewModel
import me.impa.knockonports.screen.viewmodel.state.importknockd.UiState

sealed interface NavDeepLinkKey {
    val parent: NavKey
}

@Serializable
data object MainRoute : NavKey

@Serializable
data object SettingsRoute : NavKey, NavDeepLinkKey {
    override val parent: NavKey = MainRoute
}

@Serializable
data class SequenceRoute(val sequenceId: Long? = null) : NavKey, NavDeepLinkKey {
    override val parent: NavKey = MainRoute
}

@Serializable
data object LogRoute : NavKey, NavDeepLinkKey {
    override val parent: NavKey = MainRoute
}

@Serializable
data class ColorPickerRoute(
    @ColorLong val initialColor: Long,
    @ColorLong val defaultColor: Long = Color.Unspecified.toColorLong(),
    val showAlpha: Boolean = true,
    val resultChannel: String = ""
) : NavKey

@Serializable
data class ImportFromKnockdRoute(
    val uri: String,
    val singleChoice: Boolean
) : NavKey

@Serializable
data object NavigateUp

val entryProvider = entryProvider {
    entry<MainRoute> {
        MainScreen()
    }
    entry<SequenceRoute> { key ->
        val viewModel = hiltViewModel<SequenceViewModel, SequenceViewModel.SequenceViewModelFactory> {
            it.create(key.sequenceId)
        }
        SequenceScreen(viewModel = viewModel)
    }
    entry<SettingsRoute> {
        SettingsScreen()
    }
    entry<LogRoute> {
        LogScreen()
    }
    entry<ColorPickerRoute>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties()
        )
    ) { key ->
        val viewModel = hiltViewModel<ColorPickerViewModel, ColorPickerViewModel.ColorPickerViewModelFactory> {
            it.create(key.initialColor, key.defaultColor)
        }
        ColorPickerScreen(resultChannel = key.resultChannel, showAlpha = key.showAlpha, viewModel = viewModel)
    }
    entry<ImportFromKnockdRoute>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties()
        )
    ) { key ->
        val viewModel =
            hiltViewModel<ImportKnockdConfViewModel, ImportKnockdConfViewModel.ImportKnockdConfViewModelFactory> {
                it.create(key.uri, key.singleChoice)
            }
        ImportKnockdConfScreen(viewModel = viewModel)
    }
}

val deepLinkPatterns: List<DeepLinkPattern<out NavKey>> = listOf(
    DeepLinkPattern(
        SequenceRoute.serializer(),
        "${BuildConfig.APP_SCHEME}://${BuildConfig.APP_HOST}/sequence/null".toUri()
    ),
    DeepLinkPattern(
        SequenceRoute.serializer(),
        "${BuildConfig.APP_SCHEME}://${BuildConfig.APP_HOST}/sequence/{sequenceId}".toUri()
    ),
    DeepLinkPattern(
        MainRoute.serializer(),
        "${BuildConfig.APP_SCHEME}://${BuildConfig.APP_HOST}/list".toUri()
    )

)

fun buildBackStack(startRoute: NavKey): List<NavKey> {
    return buildList {
        var node: NavKey? = startRoute
        while (node != null) {
            add(0, node)
            node = (node as? NavDeepLinkKey)?.parent
        }
    }
}

const val NAV_BUS = "NAVIGATION_EVENT_BUS"