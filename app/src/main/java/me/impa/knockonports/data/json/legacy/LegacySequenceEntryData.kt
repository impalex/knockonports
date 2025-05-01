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

package me.impa.knockonports.data.json.legacy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType

@Serializable
data class LegacySequenceEntryData(
    val name: String? = null,
    val host: String? = null,
    val delay: Int? = null,
    val application: String? = null,
    @SerialName("app_name")
    val appName: String? = null,
    @SerialName("icmp_type")
    @Serializable(with = LegacyIcmpTypeSerializer::class)
    val icmpType: IcmpType = IcmpType.WITH_ICMP_HEADER,
    @Serializable(with = LegacyProtocolVersionTypeSerializer::class)
    val ipv: ProtocolVersionType = ProtocolVersionType.PREFER_IPV4,
    val localPort: Int? = null,
    val steps: List<LegacySequenceStepData> = emptyList()
)

