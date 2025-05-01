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

@file:Suppress("MatchingDeclarationName")

package me.impa.knockonports.data.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.SequenceStepType

@Serializable
data class SequenceStepDataV1(
    val type: SequenceStepType? = null,
    val port: Int? = null,
    @SerialName("icmp_size")
    val icmpSize: Int? = null,
    @SerialName("icmp_count")
    val icmpCount: Int? = null,
    val content: String? = null,
    val encoding: ContentEncodingType? = null
)
