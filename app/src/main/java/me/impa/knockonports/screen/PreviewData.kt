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

package me.impa.knockonports.screen

import kotlinx.collections.immutable.persistentListOf
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.data.type.SequenceStepType

object PreviewData {
    val mockSequences = persistentListOf(
        Sequence(
            id = 1, name = "Sequence 1", host = "127.0.0.1", order = 0, delay = null, application = null,
            applicationName = null, icmpType = null, steps = listOf(
                SequenceStep(
                    type = SequenceStepType.UDP,
                    port = 10301
                ),
                SequenceStep(
                    type = SequenceStepType.TCP,
                    port = 32001
                )
            ), descriptionType = null, pin = null,
            ipv = null, ttl = null, localPort = null, uri = null
        ),
        Sequence(
            id = 2, name = "Sequence 2", host = "10.5.0.55", order = 1, delay = null, application = null,
            applicationName = null, icmpType = null, steps = listOf(
                SequenceStep(
                    type = SequenceStepType.ICMP,
                    icmpSize = 1337,
                    icmpCount = 5
                ),
                SequenceStep(
                    type = SequenceStepType.ICMP,
                    icmpSize = 666,
                    icmpCount = 2
                )
            ), descriptionType = null, pin = null,
            ipv = null, ttl = null, localPort = null, uri = null
        ),
        Sequence(
            id = 3, name = "Very long sequence name", host = "10.5.0.12", order = 2, delay = null, application = null,
            applicationName = null, icmpType = null, steps = listOf(
                SequenceStep(
                    type = SequenceStepType.ICMP,
                    icmpSize = 1234,
                    icmpCount = 1
                ),
                SequenceStep(
                    type = SequenceStepType.ICMP,
                    icmpSize = 955,
                    icmpCount = 4
                )
            ), descriptionType = null, pin = null,
            ipv = null, ttl = null, localPort = null, uri = null
        )
    )
    val previewLogEntries = listOf(
        LogEntry(
            id = 1,
            event = EventType.SEQUENCE_SAVED,
            data = listOf("Sequence 1")
        ),
        LogEntry(
            id = 2,
            event = EventType.SEQUENCE_DELETED,
            data = listOf("Sequence 2")
        ),
        LogEntry(
            id = 3,
            event = EventType.ERROR_IMPORT,
            data = listOf("/here/something/not/interesting/file.ext", "Some error")
        ),
        LogEntry(
            id = 4,
            event = EventType.KNOCK,
            data = listOf("My Sequence", "host.url", "10.2.0.23", "10.2.0.93")
        ),
    )

}