/*
 * Copyright (c) 2018 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.impa.knockonports.json

import android.util.JsonReader
import android.util.JsonToken
import android.util.JsonWriter
import me.impa.knockonports.data.*
import me.impa.knockonports.database.entity.Sequence
import java.io.StringReader
import java.io.StringWriter

data class SequenceData(var name: String?, var host: String?, var delay: Int?,
                        var application: String?, var appName: String?, var icmpType: IcmpType?,
                        var steps: List<SequenceStep>) {

    fun toEntity(): Sequence = Sequence(null, name, host,null, delay, application,
            appName, icmpType, steps.filter { it.isValid() })

    companion object {

        fun fromEntity(sequence: Sequence): SequenceData =
                SequenceData(sequence.name, sequence.host, sequence.delay,
                        sequence.application, sequence.applicationName, sequence.icmpType,
                        sequence.steps ?: listOf())

        private fun writeValue(writer: JsonWriter, name: String, value: String?) {
            if (value == null)
                writer.name(name).nullValue()
            else
                writer.name(name).value(value)
        }

        private fun writeValue(writer: JsonWriter, name: String, value: Int?) {
            if (value == null)
                writer.name(name).nullValue()
            else
                writer.name(name).value(value)
        }

        private fun readString(reader: JsonReader): String? {
            return if (reader.peek() == JsonToken.NULL) {
                reader.nextNull(); null
            } else {
                reader.nextString()
            }
        }

        private fun readInt(reader: JsonReader): Int? {
            return if (reader.peek() == JsonToken.NULL) {
                reader.nextNull(); null
            } else {
                reader.nextInt()
            }
        }

        fun toJson(sequenceList: List<SequenceData>): String {
            val sw = StringWriter()
            val writer = JsonWriter(sw)
            try {
                writer.beginArray()

                sequenceList.forEach {
                    writer.beginObject()

                    writeValue(writer, "name", it.name)
                    writeValue(writer, "host", it.host)
                    writeValue(writer, "delay", it.delay)
                    writeValue(writer, "application", it.application)
                    writeValue(writer, "app_name", it.appName)
                    writeValue(writer, "icmp_type", it.icmpType?.ordinal)

                    writer.name("steps")
                    writer.beginArray()
                    it.steps.forEach {s ->
                        writer.beginObject()
                        writeValue(writer, "type", s.type?.ordinal)
                        writeValue(writer, "port", s.port)
                        writeValue(writer, "icmp_size", s.icmpSize)
                        writeValue(writer, "icmp_count", s.icmpCount)
                        writeValue(writer, "content", s.content)
                        writeValue(writer, "encoding", s.encoding?.ordinal)
                        writer.endObject()
                    }
                    writer.endArray()

                    writer.endObject()
                }

                writer.endArray()
                return sw.toString()
            }
            finally {
                writer.close()
                sw.close()
            }
        }

        fun fromJson(input: String?): List<SequenceData> {
            input ?: return listOf()

            val result = mutableListOf<SequenceData>()

            val sr = StringReader(input)
            val reader = JsonReader(sr)

            try {
                reader.beginArray()
                while (reader.hasNext()) {
                    reader.beginObject()
                    val seq = SequenceData(null, null,null, null, null, null, listOf())
                    var oldUdpContent : String? = null
                    var oldBase64 : Int? = null
                    var oldType: Int? = null
                    val oldPorts = mutableListOf<SequenceStep>()
                    val oldIcmp = mutableListOf<SequenceStep>()
                    while (reader.hasNext()) {
                        val key = reader.nextName()
                        when(key) {
                            "name" -> seq.name = readString(reader)
                            "host" -> seq.host = readString(reader)
                            "timeout" -> readInt(reader) // Deprecated. Just read and ignore.
                            "delay" -> seq.delay = readInt(reader)
                            "udp_content" -> oldUdpContent = readString(reader) // Deprecated. Let's postprocess it
                            "application" -> seq.application = readString(reader)
                            "base64" -> oldBase64 = readInt(reader) // Deprecated
                            "app_name" -> seq.appName = readString(reader)
                            "type" -> oldType = readInt(reader) // Deprecated
                            "icmp_type" -> seq.icmpType = IcmpType.fromOrdinal(readInt(reader) ?: 1)
                            "ports" -> {
                                // Deprecated stuff
                                reader.beginArray()
                                while (reader.hasNext()) {
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        val port = SequenceStep(SequenceStepType.UDP, null, null, null, null, ContentEncoding.RAW)
                                        while (reader.hasNext()) {
                                            val portKey = reader.nextName()
                                            when(portKey) {
                                                "value" -> port.port = readInt(reader)
                                                "type" -> port.type = SequenceStepType.fromOrdinal(readInt(reader) ?: 0)
                                            }
                                        }
                                        oldPorts.add(port)
                                    }
                                    reader.endObject()
                                }
                                reader.endArray()
                            }
                            "icmp" -> {
                                // Deprecated stuff
                                reader.beginArray()
                                while (reader.hasNext()) {
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        val icmpData = SequenceStep(SequenceStepType.ICMP, null, null, null, null, ContentEncoding.RAW)
                                        while (reader.hasNext()) {
                                            val icmpKey = reader.nextName()
                                            when(icmpKey) {
                                                "size" -> icmpData.icmpSize = readInt(reader)
                                                "encoding" -> icmpData.encoding = ContentEncoding.fromOrdinal(readInt(reader) ?: 0)
                                                "content" -> icmpData.content = readString(reader)
                                                "count" -> icmpData.icmpCount = readInt(reader)
                                            }
                                        }
                                        oldIcmp.add(icmpData)
                                    }
                                    reader.endObject()
                                }
                                reader.endArray()
                            }
                            "steps" -> {
                                val steps = mutableListOf<SequenceStep>()
                                reader.beginArray()
                                while (reader.hasNext()) {
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        val step = SequenceStep(SequenceStepType.UDP, null, null, null, null, null)
                                        while (reader.hasNext()) {
                                            val stepKey = reader.nextName()
                                            when (stepKey) {
                                                "type" -> step.type = SequenceStepType.fromOrdinal(readInt(reader) ?: 0)
                                                "port" -> step.port = readInt(reader)
                                                "icmp_size" -> step.icmpSize = readInt(reader)
                                                "icmp_count" -> step.icmpCount = readInt(reader)
                                                "content" -> step.content = readString(reader)
                                                "encoding" -> step.encoding = ContentEncoding.fromOrdinal(readInt(reader) ?: 0)
                                            }
                                        }
                                        steps.add(step)
                                    }
                                    reader.endObject()
                                }
                                reader.endArray()
                                seq.steps = steps
                            }
                        }
                    }
                    if (seq.steps.isEmpty()) {
                        if (oldType ?: 0 == 0) {
                            seq.steps = oldPorts
                            if (!oldUdpContent.isNullOrEmpty()) {
                                seq.steps.filter { it.type == SequenceStepType.UDP }.forEach {
                                    it.content = oldUdpContent
                                    if (oldBase64 == 1)
                                        it.encoding = ContentEncoding.BASE64
                                }
                            }
                        } else {
                            seq.steps = oldIcmp
                        }
                    }
                    result.add(seq)
                    reader.endObject()
                }
                reader.endArray()
            }
            finally {
                reader.close()
                sr.close()
            }

            return result
        }
    }
}