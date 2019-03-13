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

package me.impa.knockonports.database.converter

import androidx.room.TypeConverter
import android.util.Base64
import me.impa.knockonports.data.*
import me.impa.knockonports.json.SequenceStep

@Suppress("unused")
class SequenceConverters {

    @TypeConverter
    fun sequenceStepToString(data: List<SequenceStep>?): String? =
            data?.joinToString(ENTRY_SEPARATOR.toString()) {
                (it.type?.ordinal?.toString() ?: "0") + VALUE_SEPARATOR +
                        (it.port?.toString() ?: "") + VALUE_SEPARATOR +
                        (it.icmpSize?.toString() ?: "") + VALUE_SEPARATOR +
                        (it.icmpCount?.toString() ?: "") + VALUE_SEPARATOR +
                        Base64.encodeToString(it.content?.toByteArray()
                                ?: byteArrayOf(), Base64.NO_PADDING or Base64.NO_WRAP) + VALUE_SEPARATOR +
                        (it.encoding?.ordinal?.toString() ?: "")
            }

    @TypeConverter
    fun stringToSequenceStep(data: String?): List<SequenceStep>? =
            data?.split(ENTRY_SEPARATOR)?.map {
                val s = it.split(VALUE_SEPARATOR)
                SequenceStep(if (s.isNotEmpty()) SequenceStepType.fromOrdinal(s[0].toIntOrNull() ?: SequenceStepType.UDP.ordinal) else null,
                        if (s.size>1) s[1].toIntOrNull() else null,
                        if (s.size>2) s[2].toIntOrNull() else null,
                        if (s.size>3) s[3].toIntOrNull() else null,
                        if (s.size>4) String(Base64.decode(s[4], Base64.NO_WRAP or Base64.NO_PADDING), Charsets.UTF_8) else null,
                        if (s.size>5) ContentEncoding.fromOrdinal(s[5].toIntOrNull() ?: ContentEncoding.RAW.ordinal) else ContentEncoding.RAW)
            }?.toList() ?: listOf()

    @TypeConverter
    fun intToIcmpType(data: Int?): IcmpType = IcmpType.fromOrdinal(data ?: 1)

    @TypeConverter
    fun icmpTypeToInt(data: IcmpType): Int = data.ordinal

    companion object {
        const val VALUE_SEPARATOR = ':'
        const val ENTRY_SEPARATOR = '|'
    }
}