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

package me.impa.knockonports.data.json.legacy

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType
import java.util.UUID

/**
 * Provides a [KSerializer] for enums that maintains compatibility with a legacy serialization format.
 *
 * This serializer encodes enum values as their ordinal index within the enum's `values()` array.  During
 * deserialization, it attempts to decode an integer representing the index.  Crucially, it handles
 * out-of-bounds or null values in a specific way to ensure backward compatibility with the legacy
 * format.  If the decoded integer is out of bounds or a null value is encountered, it defaults to
 * the *first* enum value.
 */
inline fun <reified T : Enum<T>> legacyEnumSerializer() = object : KSerializer<T> {

    val values = enumValues<T>()

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            "me.impa.knockonports.data.json.legacy.LegacyEnumSerializer",
            PrimitiveKind.INT
        )

    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeInt(values.indexOf(value))

    override fun deserialize(decoder: Decoder): T =
        // This is not an error!
        // Fallback to the first value is a behavior designed to support the old format.
        if (decoder.decodeNotNullMark()) values.getOrNull(decoder.decodeInt()) ?: values[0] else {
            decoder.decodeNull()
            values[0]
        }
}

/**
 * Creates a serializer for enum types that are serialized as integers in legacy format.
 *
 * @param enumSerializer The serializer for the enum type.
 * @param descriptorName Optional name for the serial descriptor. If null, a random UUID will be used.
 */
fun <T : Enum<T>> createEnumSerializer(enumSerializer: KSerializer<T>, descriptorName: String?): KSerializer<T> =
    object : KSerializer<T> {
        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor(descriptorName ?: UUID.randomUUID().toString()) {
                element("value", enumSerializer.descriptor)
            }

        override fun serialize(encoder: Encoder, value: T) =
            encoder.encodeSerializableValue(enumSerializer, value)

        override fun deserialize(decoder: Decoder): T =
            decoder.decodeSerializableValue(enumSerializer)
    }


/**
 * Serializer for [IcmpType] enums used in legacy data format.
 */
class LegacyIcmpTypeSerializer : KSerializer<IcmpType> by createEnumSerializer(
    legacyEnumSerializer<IcmpType>(),
    LegacyIcmpTypeSerializer::class.qualifiedName
)

/**
 * Serializer for [ProtocolVersionType] enums used in legacy data format.
 */
class LegacyProtocolVersionTypeSerializer : KSerializer<ProtocolVersionType> by createEnumSerializer(
    legacyEnumSerializer<ProtocolVersionType>(),
    LegacyProtocolVersionTypeSerializer::class.qualifiedName
)

/**
 * Serializer for [SequenceStepType] enums used in legacy data format.
 */
class LegacySequenceStepTypeSerializer : KSerializer<SequenceStepType> by createEnumSerializer(
    legacyEnumSerializer<SequenceStepType>(),
    LegacySequenceStepTypeSerializer::class.qualifiedName
)

/**
 * Serializer for [ContentEncodingType] enums used in legacy data format.
 */
class LegacyContentEncodingTypeSerializer : KSerializer<ContentEncodingType> by createEnumSerializer(
    legacyEnumSerializer<ContentEncodingType>(),
    LegacyContentEncodingTypeSerializer::class.qualifiedName
)