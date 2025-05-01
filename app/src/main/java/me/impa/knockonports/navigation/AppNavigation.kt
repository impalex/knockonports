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

package me.impa.knockonports.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.screen.LogScreen
import me.impa.knockonports.screen.MainScreen
import me.impa.knockonports.screen.SequenceScreen
import me.impa.knockonports.screen.SettingsScreen
import me.impa.knockonports.screen.viewmodel.SequenceViewModel

private const val ENTER_ANIMATION_DURATION = 300

@Composable
fun AppNavigation(
    startDestination: Any = AppNavGraph.MainRoute,
    onComposing: (AppBarState) -> Unit,
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController, startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ENTER_ANIMATION_DURATION, easing = EaseIn)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ENTER_ANIMATION_DURATION, easing = EaseIn)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ENTER_ANIMATION_DURATION, easing = EaseIn)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ENTER_ANIMATION_DURATION, easing = EaseIn)
            )
        }) {
        composable<AppNavGraph.MainRoute>(
            deepLinks = listOf(navDeepLink { uriPattern = "${BuildConfig.APP_SCHEME}://${BuildConfig.APP_HOST}/list" })
        ) {
            MainScreen(onComposing, navController, modifier = modifier)
        }
        composable<AppNavGraph.SequenceRoute>(
            deepLinks = listOf(navDeepLink {
                uriPattern =
                    "${BuildConfig.APP_SCHEME}://${BuildConfig.APP_HOST}/sequence/{sequenceId}"
            })
        ) {
            var args = it.toRoute<AppNavGraph.SequenceRoute>()
            val viewModel = hiltViewModel<SequenceViewModel, SequenceViewModel.SequenceViewModelFactory> {
                it.create(args.sequenceId)
            }
            SequenceScreen(onComposing, navController, viewModel, modifier = modifier)
        }
        composable<AppNavGraph.SettingsRoute> {
            SettingsScreen(onComposing, navController, modifier = modifier)
        }
        composable<AppNavGraph.LogRoute> {
            LogScreen(onComposing, navController, modifier = modifier)
        }
    }

}
