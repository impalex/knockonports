/*
 * Copyright (c) 2018-2025 Alexander Yaburov
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

package me.impa.knockonports.data.db.converter

import android.util.Base64
import androidx.room.TypeConverter
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.DescriptionType
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType
import java.net.URLDecoder
import java.net.URLEncoder

@Suppress("unused", "TooManyFunctions")
class DataConverters {

    @TypeConverter
    fun sequenceStepToString(data: List<SequenceStep>?): String? =
        data?.takeIf { it.isNotEmpty() }?.joinToString(ENTRY_SEPARATOR.toString()) {
            (it.type?.ordinal?.toString() ?: "0") + VALUE_SEPARATOR +
                    (it.port?.toString() ?: "") + VALUE_SEPARATOR +
                    (it.icmpSize?.toString() ?: "") + VALUE_SEPARATOR +
                    (it.icmpCount?.toString() ?: "") + VALUE_SEPARATOR +
                    Base64.encodeToString(
                        it.content?.toByteArray()
                            ?: byteArrayOf(),
                        Base64.NO_PADDING or Base64.NO_WRAP
                    ) + VALUE_SEPARATOR +
                    (it.encoding?.ordinal?.toString() ?: "")
        }

    @TypeConverter
    @Suppress("MagicNumber")
    fun stringToSequenceStep(data: String?) =
        data?.split(ENTRY_SEPARATOR)?.map { step ->
            val s = step.split(VALUE_SEPARATOR)
            SequenceStep(
                SequenceStepType.fromOrdinal(s.getOrNull(0)?.toIntOrNull() ?: SequenceStepType.UDP.ordinal),
                s.getOrNull(1)?.toIntOrNull(),
                s.getOrNull(2)?.toIntOrNull(),
                s.getOrNull(3)?.toIntOrNull(),
                s.getOrNull(4)?.let {
                    String(Base64.decode(it, Base64.NO_WRAP or Base64.NO_PADDING), Charsets.UTF_8)
                },
                ContentEncodingType.fromOrdinal(s.getOrNull(5)?.toIntOrNull() ?: ContentEncodingType.RAW.ordinal)
            )
        }?.toList() ?: listOf()

    @TypeConverter
    fun listToString(data: List<String?>?): String? =
        data?.joinToString(DATA_SEPARATOR.toString()) { URLEncoder.encode(it ?: "", "utf-8") }

    @TypeConverter
    fun stringToList(data: String?): List<String?>? =
        data?.split(DATA_SEPARATOR)?.map { URLDecoder.decode(it, "utf-8") }?.toList()

    @TypeConverter
    fun intToIcmpType(data: Int?): IcmpType? = if (data == null) null else IcmpType.fromOrdinal(data)

    @TypeConverter
    fun icmpTypeToInt(data: IcmpType?): Int? = data?.ordinal

    @TypeConverter
    fun intToDescriptionType(
        data: Int?
    ): DescriptionType? = if (data == null) null else DescriptionType.fromOrdinal(data)

    @TypeConverter
    fun descriptionTypeToInt(data: DescriptionType?): Int? = data?.ordinal

    @TypeConverter
    fun intToEventType(data: Int?): EventType = EventType.fromOrdinal(data ?: 0)

    @TypeConverter
    fun eventTypeToInt(data: EventType?): Int? = data?.ordinal

    @TypeConverter
    fun intToProtocolVersionType(
        data: Int?
    ): ProtocolVersionType? = if (data == null) null else ProtocolVersionType.fromOrdinal(data)

    @TypeConverter
    fun protocolVersionTypeToInt(data: ProtocolVersionType?): Int? = data?.ordinal

    companion object {
        const val VALUE_SEPARATOR = ':'
        const val ENTRY_SEPARATOR = '|'
        const val DATA_SEPARATOR = '\n'
    }
}
