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

package me.impa.knockonports.data.event

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton class responsible for holding and managing application-wide events.
 *
 * This class utilizes `mutableStateOf` to allow for observing changes to the current event
 * from Composable functions.  It acts as a central hub for dispatching and handling
 * single-shot events that need to be communicated across different parts of the application,
 * such as displaying a snackbar, navigating to a different screen, or showing a dialog.
 *
 * Events are represented by the `AppEvent` sealed class, which should define the different
 * types of application events that can occur.
 */
@Singleton
class SharedEventHolder @Inject constructor() {

    private var _currentEventFlow = MutableStateFlow<AppEvent?>(null)
    val currentEventFlow: StateFlow<AppEvent?>
        get() = _currentEventFlow

    fun sendEvent(event: AppEvent) {
        Timber.d("Event: $event")
        _currentEventFlow.value = event
    }

    fun clearEvent() {
        _currentEventFlow.value = null
    }

}