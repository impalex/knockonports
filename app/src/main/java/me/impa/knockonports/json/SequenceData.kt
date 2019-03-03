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
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.data.IcmpType
import me.impa.knockonports.data.KnockType
import me.impa.knockonports.data.PortType
import me.impa.knockonports.database.entity.Sequence
import java.io.StringReader
import java.io.StringWriter

data class SequenceData(var name: String?, var host: String?, var delay: Int?,
                        var udpContent: String?, var application: String?, var base64: Int?,
                        var appName: String?, var ports: List<PortData>, var type: KnockType?,
                        var icmp: List<IcmpData>, var icmpType: IcmpType?) {

    fun toEntity(): Sequence = Sequence(null, name, host,null, delay, udpContent, application, base64,
            ports.asSequence().filter { it.value != null }.map { PortData(it.value, it.type) }.toList(), appName,
            type, icmp, icmpType)

    companion object {

        fun fromEntity(sequence: Sequence): SequenceData =
                SequenceData(sequence.name, sequence.host, sequence.delay, sequence.udpContent,
                        sequence.application, sequence.base64, sequence.applicationName, sequence.ports ?: listOf(),
                        sequence.type, sequence.icmp ?: listOf(), sequence.icmpType)

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

                    writeValue(writer, "type", it.type?.ordinal)
                    writeValue(writer, "name", it.name)
                    writeValue(writer, "host", it.host)
                    writeValue(writer, "delay", it.delay)
                    writeValue(writer, "udp_content", it.udpContent)
                    writeValue(writer, "application", it.application)
                    writeValue(writer, "app_name", it.appName)
                    writeValue(writer, "base64", it.base64)

                    writer.name("ports")
                    writer.beginArray()
                    it.ports.forEach { p ->
                        writer.beginObject()
                        writeValue(writer, "value", p.value)
                        writeValue(writer, "type", p.type.ordinal)
                        writer.endObject()
                    }
                    writer.endArray()

                    writeValue(writer, "icmp_type", it.icmpType?.ordinal)

                    writer.name("icmp")
                    writer.beginArray()
                    it.icmp.forEach { i ->
                        writer.beginObject()
                        writeValue(writer, "size", i.size)
                        writeValue(writer, "count", i.count)
                        writeValue(writer, "encoding", i.encoding.ordinal)
                        writeValue(writer, "content", i.content)
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
                    val seq = SequenceData(null, null,null, null, null, null, null, listOf(), KnockType.PORT, listOf(), IcmpType.WITH_ICMP_HEADER)
                    val ports = mutableListOf<PortData>()
                    val icmp = mutableListOf<IcmpData>()
                    while (reader.hasNext()) {
                        val key = reader.nextName()
                        when(key) {
                            "name" -> seq.name = readString(reader)
                            "host" -> seq.host = readString(reader)
                            "timeout" -> readInt(reader) // Deprecated. Just read and ignore.
                            "delay" -> seq.delay = readInt(reader)
                            "udp_content" -> seq.udpContent = readString(reader)
                            "application" -> seq.application = readString(reader)
                            "base64" -> seq.base64 = readInt(reader)
                            "app_name" -> seq.appName = readString(reader)
                            "type" -> seq.type = KnockType.fromOrdinal(readInt(reader) ?: 0)
                            "icmp_type" -> seq.icmpType = IcmpType.fromOrdinal(readInt(reader) ?: 1)
                            "ports" -> {
                                reader.beginArray()
                                while (reader.hasNext()) {
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        val port = PortData(null, PortType.UDP)
                                        while (reader.hasNext()) {
                                            val portKey = reader.nextName()
                                            when(portKey) {
                                                "value" -> port.value = readInt(reader)
                                                "type" -> port.type = PortType.fromOrdinal(readInt(reader) ?: 0)
                                            }
                                        }
                                        ports.add(port)
                                    }
                                    reader.endObject()
                                }
                                reader.endArray()
                            }
                            "icmp" -> {
                                reader.beginArray()
                                while (reader.hasNext()) {
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        val icmpData = IcmpData(null, null, ContentEncoding.RAW, null)
                                        while (reader.hasNext()) {
                                            val icmpKey = reader.nextName()
                                            when(icmpKey) {
                                                "size" -> icmpData.size = readInt(reader)
                                                "encoding" -> icmpData.encoding = ContentEncoding.fromOrdinal(readInt(reader) ?: 0)
                                                "content" -> icmpData.content = readString(reader)
                                                "count" -> icmpData.count = readInt(reader)
                                            }
                                        }
                                        icmp.add(icmpData)
                                    }
                                    reader.endObject()
                                }
                                reader.endArray()
                            }
                        }
                    }
                    seq.ports = ports
                    seq.icmp = icmp
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