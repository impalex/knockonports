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

@file:OptIn(ExperimentalSerializationApi::class)

package me.impa.knockonports.data.json

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import me.impa.knockonports.data.json.legacy.LegacySequenceEntryData

@Serializable(with = SequencesSerializer::class)
sealed class SequencesData {
    @EncodeDefault
    abstract val version: Int
}

@Serializable
data class LegacySequencesData(
    @EncodeDefault
    override val version: Int = 0,
    val sequences: List<LegacySequenceEntryData> = emptyList()
) : SequencesData()

@Serializable
data class SequencesDataV1(
    @EncodeDefault
    override val version: Int = 1,
    val sequences: List<SequenceEntryDataV1> = emptyList()
) : SequencesData()

