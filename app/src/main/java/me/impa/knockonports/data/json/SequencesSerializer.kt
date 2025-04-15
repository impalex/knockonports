/*
 * Copyright (c) 2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package me.impa.knockonports.data.json

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import me.impa.knockonports.data.json.legacy.LegacySequencesDataDeserializer

/**
 * Serializer for [SequencesData] that handles polymorphic deserialization based on the JSON structure.
 *
 * It supports two formats:
 * - Legacy format (represented by a JSON array), deserialized using [LegacySequencesDataDeserializer].
 * - Versioned format (represented by a JSON object), with the version indicated by the "version" field.
 *
 *  If the provided JSON element is not a JSON array or object, or if the version in a JSON object
 *  is not recognized, it throws an [IllegalArgumentException].
 */
object SequencesSerializer : JsonContentPolymorphicSerializer<SequencesData>(SequencesData::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<SequencesData> =
        when (element) {
            is JsonArray -> LegacySequencesDataDeserializer
            is JsonObject -> when (element["version"]?.jsonPrimitive?.int) {
                1 -> SequencesDataV1.serializer()
                else -> throw IllegalArgumentException("Unknown element type: $element")
            }

            else -> throw IllegalArgumentException("Unknown element type: $element")
        }
}
