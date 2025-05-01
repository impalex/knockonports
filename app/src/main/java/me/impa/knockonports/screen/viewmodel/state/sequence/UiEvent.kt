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

package me.impa.knockonports.screen.viewmodel.state.sequence

import me.impa.knockonports.data.type.CheckAccessType
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType

sealed interface UiEvent {
    data class UpdateTitle(val title: String) : UiEvent
    data class UpdateGroup(val group: String) : UiEvent
    data class UpdateHost(val host: String) : UiEvent
    data object AddStep : UiEvent
    data object ResetNewStepId : UiEvent
    data class DeleteStep(val id: Int) : UiEvent
    data class MoveStep(val from: Int, val to: Int) : UiEvent
    data class UpdateStepPort(val id: Int, val port: Int?) : UiEvent
    data class UpdateStepType(val id: Int, val type: SequenceStepType) : UiEvent
    data class UpdateStepContent(val id: Int, val content: String) : UiEvent
    data class UpdateStepIcmpSize(val id: Int, val size: Int?) : UiEvent
    data class UpdateStepIcmpCount(val id: Int, val count: Int?) : UiEvent
    data class UpdateStepEncoding(val id: Int, val encoding: ContentEncodingType) : UiEvent
    data class UpdateDelay(val delay: Int?) : UiEvent
    data class UpdateApp(val appPackage: String?, val appName: String?) : UiEvent
    data class UpdateLocalPort(val port: Int?) : UiEvent
    data class UpdateProtocol(val protocol: ProtocolVersionType) : UiEvent
    data class UpdateIcmpType(val icmpType: IcmpType) : UiEvent
    data class UpdateTtl(val ttl: Int?) : UiEvent
    data class UpdateUri(val uri: String) : UiEvent
    data object ToggleCheckAccess : UiEvent
    data class UpdateCheckAccessType(val checkAccessType: CheckAccessType) : UiEvent
    data class UpdateCheckAccessHost(val host: String) : UiEvent
    data class UpdateCheckAccessPort(val port: Int?) : UiEvent
    data class UpdateCheckAccessTimeout(val timeout: Int) : UiEvent
    data object ToggleCheckAccessPostKnock : UiEvent
    data class UpdateCheckAccessMaxRetries(val retries: Int) : UiEvent
}