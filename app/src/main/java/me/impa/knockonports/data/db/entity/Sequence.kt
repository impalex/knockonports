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

package me.impa.knockonports.data.db.entity

import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.DescriptionType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType

@Stable
@Entity(tableName = "tbSequence")
data class Sequence(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long?,
    @ColumnInfo(name = "_name")
    val name: String?,
    @ColumnInfo(name = "_host")
    val host: String?,
    @ColumnInfo(name = "_order")
    val order: Int?,
    @ColumnInfo(name = "_delay")
    val delay: Int?,
    @ColumnInfo(name = "_application")
    val application: String?,
    @ColumnInfo(name = "_application_name")
    val applicationName: String?,
    @ColumnInfo(name = "_icmp_type")
    val icmpType: IcmpType?,
    @ColumnInfo(name = "_steps")
    val steps: List<SequenceStep>?,
    @ColumnInfo(name = "_description_type")
    val descriptionType: DescriptionType?,
    @ColumnInfo(name = "_pin")
    val pin: String?,
    @ColumnInfo(name = "_ipv")
    val ipv: ProtocolVersionType?,
    @ColumnInfo(name = "_local_port")
    val localPort: Int?,
    @ColumnInfo(name = "_ttl")
    val ttl: Int?,
    @ColumnInfo(name = "_uri")
    val uri: String?
)
