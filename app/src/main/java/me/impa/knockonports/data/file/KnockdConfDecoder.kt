/*
 * Copyright (c) 2026 Alexander Yaburov
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

package me.impa.knockonports.data.file

import me.impa.knockonports.constants.DEFAULT_CHECK_RETRIES
import me.impa.knockonports.constants.DEFAULT_CHECK_TIMEOUT
import me.impa.knockonports.constants.MAX_PORT
import me.impa.knockonports.constants.MIN_PORT
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.CheckAccessType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType

object KnockdConfDecoder {

    fun decode(data: String): List<Sequence> {

        var currentSequence: Sequence? = null
        val sequences = mutableListOf<Sequence>()
        var idx = 0L
        data.lines().forEach { line ->

            val trimmedLine = line
                .split('#', limit = 2)
                .first()
                .trim()
            if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
                val currentSection = trimmedLine.trim('[', ']', ' ', '\t')
                currentSequence?.takeIf { it.steps != null && it.steps.isNotEmpty() }?.let { sequences.add(it) }
                currentSequence = newSequence(idx++, currentSection)
            } else if (currentSequence != null) {
                trimmedLine.split('=', limit = 2).takeIf { it.size == 2 }?.also {
                    if (it[0].trim().lowercase() == "sequence") {
                        currentSequence = currentSequence?.copy(steps = parseSequence(it[1]))
                    }
                }
            }
        }
        currentSequence?.takeIf { it.steps != null }?.let { sequences.add(it) }
        return sequences
    }

    private fun parseSequence(sequence: String) = buildList {
        val parts = sequence.split(',')
        parts.forEach { step ->
            val (port, type) = step.split(':', limit = 2)
                .takeIf { it.size == 2 }
                ?.map { it.trim().lowercase() }
                ?: listOf(step.trim(), "tcp")
            port.toIntOrNull()?.takeIf { it in MIN_PORT..MAX_PORT }?.also {
                add(SequenceStep(port = it, type = if (type == "udp") SequenceStepType.UDP else SequenceStepType.TCP))
            }
        }
    }

    private fun newSequence(idx: Long, name: String): Sequence =
        Sequence(
            id = idx,
            name = name,
            host = "",
            order = null,
            delay = null,
            application = null,
            applicationName = null,
            icmpType = IcmpType.WITHOUT_HEADERS,
            steps = null,
            descriptionType = null,
            pin = null,
            ipv = ProtocolVersionType.PREFER_IPV4,
            localPort = null,
            ttl = null,
            uri = null,
            group = null,
            checkAccess = false,
            checkType = CheckAccessType.PORT,
            checkPort = null,
            checkHost = null,
            checkTimeout = DEFAULT_CHECK_TIMEOUT,
            checkPostKnock = false,
            checkRetries = DEFAULT_CHECK_RETRIES
        )

}