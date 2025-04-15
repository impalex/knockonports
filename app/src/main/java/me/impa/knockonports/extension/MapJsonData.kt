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

package me.impa.knockonports.extension

import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.json.LegacySequencesData
import me.impa.knockonports.data.json.SequenceEntryDataV1
import me.impa.knockonports.data.json.SequenceStepDataV1
import me.impa.knockonports.data.json.SequencesData
import me.impa.knockonports.data.json.SequencesDataV1
import me.impa.knockonports.data.model.SequenceStep

/**
 * Converts a [SequencesData] object to a list of [Sequence] objects.
 *
 * This function acts as a dispatcher, handling different versions of the
 * [SequencesData] format.  It uses a `when` expression to determine the
 * specific conversion logic based on the type of [SequencesData]:
 *
 * @return A [List] of [Sequence] objects representing the converted data.
 */
fun SequencesData.asSequenceList(): List<Sequence> =
    when (this) {
        is LegacySequencesData -> legacyToSequenceList(this)
        is SequencesDataV1 -> v1ToSequenceList(this)
    }

/**
 * Converts a list of [Sequence] objects into a [SequencesData] object suitable for JSON serialization.
 * This function transforms the internal representation of sequences into a standardized data structure
 * for outputting sequence information in a consistent format.
 *
 * @return A [SequencesData] object representing the input list of sequences, formatted according to the schema.
 */
fun List<Sequence>.asJsonData(): SequencesData = SequencesDataV1(
    sequences = this.map {
        SequenceEntryDataV1(
            name = it.name,
            host = it.host,
            delay = it.delay,
            application = it.application,
            applicationName = it.applicationName,
            icmpType = it.icmpType,
            ipv = it.ipv,
            localPort = it.localPort,
            steps = it.steps?.map { step ->
                SequenceStepDataV1(
                    type = step.type,
                    port = step.port,
                    icmpSize = step.icmpSize,
                    icmpCount = step.icmpCount,
                    content = step.content
                )
            } ?: listOf()
        )
    }
)

/**
 * Converts a [LegacySequencesData] object to a list of [Sequence] objects.  This function
 * handles the mapping between the old data structure and the new one, including
 * translating field names and restructuring nested data.  Fields that are not present
 * in the legacy data are initialized to null or appropriate defaults in the new structure.
 *
 * @param legacySequencesData The legacy sequence data to convert.
 * @return A list of [Sequence] objects representing the converted data.
 */
private fun legacyToSequenceList(legacySequencesData: LegacySequencesData): List<Sequence> =
    legacySequencesData.sequences.map { sequence ->
        Sequence(
            id = null,
            name = sequence.name,
            host = sequence.host,
            order = null,
            descriptionType = null,
            pin = null,
            application = sequence.application,
            applicationName = sequence.appName,
            delay = sequence.delay,
            ipv = sequence.ipv,
            icmpType = sequence.icmpType,
            localPort = sequence.localPort,
            ttl = null,
            uri = null,
            steps = sequence.steps.map { step ->
                SequenceStep(
                    type = step.type,
                    port = step.port,
                    icmpSize = step.icmpSize,
                    icmpCount = step.icmpCount,
                    content = step.content
                )
            }
        )
    }

/**
 * Converts a list of sequences from the V1 data format ([SequencesDataV1]) to the internal [Sequence] representation.
 *
 * This function iterates through each sequence in the provided V1 data and maps it to a new [Sequence] object.
 * It handles the conversion of individual fields, setting some fields like `id` and `order` to null (as they are
 * not present in V1 data)
 * and mapping nested structures like steps appropriately.
 *
 * @param v1SequencesData The V1 sequences data containing a list of sequences to be converted.
 * @return A list of [Sequence] objects representing the converted sequences.
 */
private fun v1ToSequenceList(v1SequencesData: SequencesDataV1): List<Sequence> =
    v1SequencesData.sequences.map { sequence ->
        Sequence(
            id = null,
            name = sequence.name,
            host = sequence.host,
            order = null,
            descriptionType = null,
            pin = null,
            application = sequence.application,
            applicationName = sequence.applicationName,
            delay = sequence.delay,
            ipv = sequence.ipv,
            icmpType = sequence.icmpType,
            localPort = sequence.localPort,
            ttl = sequence.ttl,
            uri = sequence.uri,
            steps = sequence.steps.map { step ->
                SequenceStep(
                    type = step.type,
                    port = step.port,
                    icmpSize = step.icmpSize,
                    icmpCount = step.icmpCount,
                    content = step.content
                )
            }
        )
    }