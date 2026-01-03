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

package me.impa.knockonports.screen.viewmodel.state.main

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import me.impa.knockonports.constants.DEFAULT_TITLE_FONT_SCALE
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.type.TitleOverflowType
import me.impa.knockonports.service.resource.ResourceState

data class UiState(
    val sequences: ImmutableMap<String, ImmutableList<Sequence>> = persistentMapOf(),
    val resourceState: ImmutableMap<Long, ResourceState> = persistentMapOf(),
    val areShortcutsAvailable: Boolean = false,
    val isRuLangAvailable: Boolean = false,
    val focusedSequenceId: Long? = null,
    val disableNotificationRequest: Boolean = false,
    val detailedList: Boolean = true,
    val onlineColor: Color = Color.Unspecified,
    val offlineColor: Color = Color.Unspecified,
    val titleOverflowType: TitleOverflowType = TitleOverflowType.END,
    val titleMultiline: Boolean = false,
    val titleScale: Int = DEFAULT_TITLE_FONT_SCALE,
)