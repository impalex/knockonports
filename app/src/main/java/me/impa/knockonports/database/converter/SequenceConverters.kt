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

import android.arch.persistence.room.TypeConverter
import android.util.Base64
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.data.KnockType
import me.impa.knockonports.data.PortType
import me.impa.knockonports.json.IcmpData
import me.impa.knockonports.json.PortData

@Suppress("unused")
class SequenceConverters {

    @TypeConverter
    fun stringToPortDataList(data: String?): List<PortData> =
            data?.split(ENTRY_SEPARATOR)?.map {
                val p = it.split(VALUE_SEPARATOR)
                PortData(if (p.isNotEmpty()) {
                    p[0].toIntOrNull()
                } else {
                    null
                },
                        if (p.size > 1) {
                            PortType.fromOrdinal(p[1].toIntOrNull() ?: PortType.UDP.ordinal)
                        } else {
                            PortType.UDP
                        })
            }?.toList() ?: listOf()

    @TypeConverter
    fun portDataListToString(data: List<PortData>?): String? =
            data?.joinToString(ENTRY_SEPARATOR.toString()) {
                (it.value?.toString() ?: "") + VALUE_SEPARATOR + it.type.ordinal
            }

    @TypeConverter
    fun intToKnockType(data: Int?): KnockType = KnockType.fromOrdinal(data ?: 0)

    @TypeConverter
    fun knockTypeToInt(data: KnockType): Int = data.ordinal

    @TypeConverter
    fun stringToIcmpDataList(data: String?): List<IcmpData> {
        return data?.split(ENTRY_SEPARATOR)?.map {
            val p = it.split(VALUE_SEPARATOR)
            IcmpData(if (p.isNotEmpty()) {
                p[0].toIntOrNull()
            } else {
                null
            },
                    if (p.size > 1) {
                        p[1].toIntOrNull()
                    } else {
                        null
                    },
                    if (p.size > 2) {
                        ContentEncoding.fromOrdinal(p[2].toIntOrNull()
                                ?: ContentEncoding.RAW.ordinal)
                    } else {
                        ContentEncoding.RAW
                    },
                    if (p.size > 3) {
                        String(Base64.decode(p[3], Base64.NO_WRAP or Base64.NO_PADDING), Charsets.UTF_8)
                    } else {
                        null
                    })
        }?.toList() ?: listOf()
    }

    @TypeConverter
    fun icmpDataListToString(data: List<IcmpData>?): String? =
            data?.joinToString(ENTRY_SEPARATOR.toString()) {
                (it.size?.toString() ?: "") + VALUE_SEPARATOR +
                        (it.count?.toString() ?: "") + VALUE_SEPARATOR +
                        it.encoding.ordinal + VALUE_SEPARATOR +
                        Base64.encodeToString(it.content?.toByteArray()
                                ?: byteArrayOf(), Base64.NO_PADDING or Base64.NO_WRAP)
            }

    companion object {
        const val VALUE_SEPARATOR = ':'
        const val ENTRY_SEPARATOR = '|'
    }
}