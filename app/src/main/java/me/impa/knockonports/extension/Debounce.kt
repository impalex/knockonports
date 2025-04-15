/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop

const val DEFAULT_DEBOUNCE_TIME = 500L

/**
 * Creates a debounced click handler for Compose UI elements.  This prevents rapid successive clicks from
 * triggering the provided [onClick] action multiple times within the specified [debounceTime].
 *
 * @param onClick The action to perform when a click event is triggered after the debounce period.
 * @param debounceTime The minimum time in milliseconds that must elapse between click events for the [onClick]
 *                     action to be executed.  Defaults to [DEFAULT_DEBOUNCE_TIME].
 * @return A lambda function that should be used as the click handler for a Compose UI element
 *         (e.g., in a Button's `onClick` parameter).
 *         This lambda manages the debounce logic and only invokes the provided [onClick] after the debounce period.
 *
 * Example usage:
 * ```kotlin
 * @Composable
 * fun MyComposable() {
 *     val debouncedClick = debounced(onClick = {
 *         // Action to perform on click after debounce period
 *         println("Button clicked!")
 *     })
 *
 *     Button(onClick = debouncedClick) {
 *         Text("Click Me")
 *     }
 * }
 * ```
 */
@Composable
inline fun debounced(crossinline onClick: () -> Unit, debounceTime: Long = DEFAULT_DEBOUNCE_TIME): () -> Unit {
    var lastClicked by remember { mutableLongStateOf(0L) }

    return {
        val now = SystemClock.uptimeMillis()
        if (now - lastClicked >= debounceTime) {
            onClick()
        }
        lastClicked = now
    }
}

/**
 * Applies a debounced click listener to the composable. This means that the [onClick]
 * function will only be invoked after a specified [debounceTime] has passed since the
 * last click event.  Subsequent clicks within the debounce time will reset the timer.
 *
 * @param debounceTime The time in milliseconds to debounce click events. Defaults to [DEFAULT_DEBOUNCE_TIME].
 * @param enabled Controls the enabled state of the clickable element. When `false`, clicks will be ignored.
 *                Defaults to `true`.
 * @param onClick The function to be invoked when the composable is clicked after the debounce time has elapsed.
 * @return A [Modifier] representing the modified composable with the debounced click listener.
 */
fun Modifier.debouncedClickable(
    debounceTime: Long = DEFAULT_DEBOUNCE_TIME,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    return this.composed {
        val clickable = debounced(onClick = { onClick() }, debounceTime = debounceTime)
        this.clickable(enabled = enabled) { clickable() }
    }
}

/**
 * A composable function that debounces changes to a [MutableState].
 *
 * This function observes a [MutableState] and emits its value after a specified debounce time.  This is useful for
 * scenarios like search bars where you want to avoid performing actions on every keystroke and instead wait for the
 * user to pause typing.
 *
 * @param T The type of the value held by the [MutableState].
 * @param debounceTime The debounce time in milliseconds. Defaults to [DEFAULT_DEBOUNCE_TIME].
 * @param onValueChange A callback function that is invoked with the debounced value of the state.
 *
 * **Note:** The `drop(1)` call ensures that the initial value of the state is not emitted immediately.
 */
@SuppressLint("ComposableNaming")
@OptIn(FlowPreview::class)
@Composable
fun <T> MutableState<T>.debounced(debounceTime: Long = DEFAULT_DEBOUNCE_TIME, onValueChange: (T) -> Unit) {
    LaunchedEffect(this) {
        snapshotFlow { this@debounced.value }
            .drop(1)
            .debounce(debounceTime)
            .collect {
                onValueChange(it)
            }
    }
}
