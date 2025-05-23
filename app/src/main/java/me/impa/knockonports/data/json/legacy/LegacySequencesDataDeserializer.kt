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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import me.impa.knockonports.data.json.LegacySequencesData

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
object LegacySequencesDataDeserializer : KSerializer<LegacySequencesData> {

    private val delegateSerializer =
        ListSerializer<LegacySequenceEntryData>(LegacySequenceEntryData::class.serializer())

    override val descriptor: SerialDescriptor
        get() = SerialDescriptor(
            "me.impa.knockonports.data.json.legacy.LegacySequencesDataSerializer",
            delegateSerializer.descriptor
        )

    override fun serialize(encoder: Encoder, value: LegacySequencesData) {
        encoder.encodeSerializableValue(delegateSerializer, value.sequences)
    }

    override fun deserialize(decoder: Decoder): LegacySequencesData {
        return LegacySequencesData(sequences = decoder.decodeSerializableValue(delegateSerializer))
    }

}