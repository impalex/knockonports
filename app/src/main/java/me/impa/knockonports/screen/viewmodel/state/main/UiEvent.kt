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

import android.net.Uri

sealed interface UiEvent {
    data class Edit(val sequenceId: Long?) : UiEvent
    data object ResetEditMode : UiEvent
    data class Duplicate(val sequenceId: Long) : UiEvent
    data class Move(val from: Int, val to: Int) : UiEvent
    data object ConfirmReorder : UiEvent
    data class Delete(val sequenceId: Long) : UiEvent
    data object ConfirmDelete : UiEvent
    data object ClearOverlay : UiEvent
    data class Focus(val sequenceId: Long?) : UiEvent
    data class Automate(val sequenceId: Long) : UiEvent
    data class PostponeReviewRequest(val interval: Long) : UiEvent
    data object DoNotAskForReview : UiEvent
    data class ShowMessage(val resourceId: Int) : UiEvent
    data class ShowError(val message: String) : UiEvent
    data class Knock(val sequenceId: Long) : UiEvent
    data object DisableNotificationRequest : UiEvent
    data class Export(val uri: Uri) : UiEvent
    data class Import(val uri: Uri) : UiEvent
    data object ConfirmBetaMessage : UiEvent
    data object ToggleListMode : UiEvent
}

