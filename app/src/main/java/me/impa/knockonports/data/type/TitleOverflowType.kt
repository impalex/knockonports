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

package me.impa.knockonports.data.type

import androidx.compose.ui.text.style.TextOverflow
import me.impa.knockonports.extension.EnumCompanion

enum class TitleOverflowType {
    START, MIDDLE, END;

    companion object : EnumCompanion<TitleOverflowType>(
        TitleOverflowType.entries.toTypedArray(),
        END
    )
}

fun TitleOverflowType.toTextOverflow() = when (this) {
    TitleOverflowType.START -> TextOverflow.StartEllipsis
    TitleOverflowType.MIDDLE -> TextOverflow.MiddleEllipsis
    TitleOverflowType.END -> TextOverflow.Ellipsis
}