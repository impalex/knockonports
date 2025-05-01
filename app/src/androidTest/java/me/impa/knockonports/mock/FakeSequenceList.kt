/*
 * Copyright (c) 2025 Alexander Yaburov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.impa.knockonports.mock

import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.CheckAccessType
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.DescriptionType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType

val fakeSequenceList = arrayListOf(
    Sequence(
        1L, "Test Sequence 1", "192.168.1.100", 1, 1000, "ssh", "SSH", null,
        listOf(
            SequenceStep(port = 1234),
            SequenceStep(port = 5678, type = SequenceStepType.TCP)
        ),
        null, null, null, null, null, null, "",
        false, CheckAccessType.PORT, 80, null, 10, true, 3
    ),
    Sequence(
        2L, "Test Sequence 2", "192.168.1.101", 2, 500, "rdp", "RDP", null,
        listOf(
            SequenceStep(port = 3389, type = SequenceStepType.TCP),
            SequenceStep(port = 44687, type = SequenceStepType.UDP)
        ),
        DescriptionType.HIDE, "12345", ProtocolVersionType.PREFER_IPV4, 12345, 64, "http://test.com", "",
        false, CheckAccessType.PORT, 80, null, 10, true, 3
    ),
    Sequence(
        3L, "Test ICMP Sequence", "192.168.1.102", 3, 2000, null, null, IcmpType.WITH_ICMP_HEADER,
        listOf(
            SequenceStep(type = SequenceStepType.ICMP, icmpSize = 64, icmpCount = 3),
            SequenceStep(type = SequenceStepType.ICMP, icmpSize = 127, icmpCount = 8)
        ),
        DescriptionType.HIDE, null, null, null, null, null, "",
        false, CheckAccessType.PORT, 80, null, 10, true, 3
    ),
    Sequence(
        4L, "Test Sequence 3", "192.168.1.103", 4, 1500, null, null, null,
        listOf(
            SequenceStep(
                type = SequenceStepType.UDP, port = 80,
                content = "CONTENT",
                encoding = ContentEncodingType.RAW
            ),
            SequenceStep(
                type = SequenceStepType.UDP, port = 443,
                content = "TEXT DATA",
                encoding = ContentEncodingType.RAW
            )
        ),
        null, null, null, null, 42, "myapp://start/secret/service", "",
        false, CheckAccessType.PORT, 80, null, 10, true, 3
    ),
    Sequence(
        5L, "Test Sequence 4", "2001:0db8:85a3:0000:0000:8a2e:0370:7334", 5, 750, null, null, null,
        listOf(
            SequenceStep(port = 1111, type = SequenceStepType.UDP),
            SequenceStep(port = 2222, type = SequenceStepType.TCP)
        ),
        null, null, ProtocolVersionType.ONLY_IPV6, null, 128, null, "",
        false, CheckAccessType.PORT, 80, null, 10, true, 3
    )
)