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

package me.impa.knockonports.extension

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

/**
 * Traverses the context hierarchy to find the nearest [FragmentActivity].
 *
 * This extension function is useful for obtaining a `FragmentActivity` instance from a generic `Context`,
 * which might be a `ContextWrapper` wrapping the actual activity. It recursively unwraps the `baseContext`
 * until it finds a `FragmentActivity` or reaches the end of the chain.
 *
 * @return The found [FragmentActivity], or `null` if no `FragmentActivity` is found in the context chain.
 */
fun Context.findFragmentActivity(): FragmentActivity? = when(this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}