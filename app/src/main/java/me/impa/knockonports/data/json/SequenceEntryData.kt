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

@file:Suppress("MatchingDeclarationName")

package me.impa.knockonports.data.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType

@Serializable
data class SequenceEntryDataV1(
    val name: String? = null,
    val host: String? = null,
    val delay: Int? = null,
    val application: String? = null,
    @SerialName("app_name")
    val applicationName: String? = null,
    @SerialName("icmp_type")
    val icmpType: IcmpType? = null,
    val ipv: ProtocolVersionType? = null,
    @SerialName("local_port")
    val localPort: Int? = null,
    val ttl: Int? = null,
    val uri: String? = null,
    val steps: List<SequenceStepDataV1> = listOf()
)
