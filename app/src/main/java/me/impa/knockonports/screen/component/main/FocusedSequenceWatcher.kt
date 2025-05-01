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
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import me.impa.knockonports.screen.FOCUSED_SEQUENCE_ID
import me.impa.knockonports.screen.viewmodel.state.main.UiEvent

@Composable
fun FocusedSequenceWatcher(
    stateHandle: SavedStateHandle?,
    onEvent: (UiEvent) -> Unit
) {
    LaunchedEffect(stateHandle) {
        stateHandle?.getStateFlow<Long?>(FOCUSED_SEQUENCE_ID, null)?.filterNotNull()?.collectLatest {
            onEvent(UiEvent.Focus(it))
            stateHandle[FOCUSED_SEQUENCE_ID] = null
        }
    }
}

