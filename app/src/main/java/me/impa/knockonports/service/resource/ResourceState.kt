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

package me.impa.knockonports.service.resource

import me.impa.knockonports.helper.TextResource

sealed interface ResourceState {
    val date: Long

    data class Unknown(override val date: Long = Long.MIN_VALUE) : ResourceState
    data class Available(override val date: Long = System.currentTimeMillis()) : ResourceState
    data class Unavailable(
        val error: TextResource,
        override val date: Long = System.currentTimeMillis()
    ) : ResourceState
    data class Checking(override val date: Long = System.currentTimeMillis()) : ResourceState
}