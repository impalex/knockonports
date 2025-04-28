/*
 * Copyright (c) 2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package me.impa.knockonports.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import me.impa.knockonports.navigation.AppNavGraph

/**
 * Executes a composable block only when the current navigation destination matches a specified route.
 *
 * This function leverages `NavController.currentBackStackEntryAsState()` to observe changes in the
 * current navigation stack.  It then checks if the current destination's route corresponds to the
 * provided `AppNavGraph` type. If a match is found, the composable block is executed; otherwise, it's skipped.
 *
 * @param T The type of `AppNavGraph` to check against the current navigation destination's route.
 *  Must be a reified type.
 * @param block The composable block to execute if the destination matches the specified route.
 */
@Composable
inline fun <reified T : AppNavGraph> NavController.OnDestination(crossinline block: @Composable () -> Unit) {
    val navBackStackEntry by currentBackStackEntryAsState()
    if (navBackStackEntry?.destination?.hasRoute(T::class) == true) {
        block()
    }
}