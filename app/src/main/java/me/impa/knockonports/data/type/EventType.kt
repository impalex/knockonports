/*
 * Copyright (c) 2018-2025 Alexander Yaburov
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

package me.impa.knockonports.data.type

import me.impa.knockonports.extension.EnumCompanion

enum class EventType {
    UNKNOWN,
    KNOCK,
    ERROR_NETWORK,
    ERROR_INVALID_HOST,
    ERROR_RESOLVE_HOST,
    ERROR_EMPTY_SEQUENCE,
    ERROR_UNKNOWN,
    SEQUENCE_SAVED,
    SEQUENCE_DELETED,
    EXPORT,
    IMPORT,
    ERROR_EXPORT,
    ERROR_IMPORT;

    companion object : EnumCompanion<EventType>(EventType.entries.toTypedArray())
}
