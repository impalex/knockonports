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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.impa.knockonports.database.entity.Sequence

@Serializable
data class SequenceData(var name: String?, var host: String?, var timeout: Int?, var delay: Int?,
                        @SerialName("udp_content") var udpContent: String?,
                        var application: String?, var base64: Int?, var ports: List<PortData>) {

    fun toEntity(): Sequence = Sequence(null, name, host, timeout, null, delay, udpContent, application, base64,
            ports.filter { it.value != null }.joinToString(", ") { it.toString() })

    companion object {
        fun fromEntity(sequence: Sequence): SequenceData =
                SequenceData(sequence.name, sequence.host, sequence.timeout, sequence.delay, sequence.udpContent,
                        sequence.application, sequence.base64, sequence.getPortList())
    }
}