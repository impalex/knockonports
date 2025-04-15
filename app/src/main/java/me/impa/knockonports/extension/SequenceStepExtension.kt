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

package me.impa.knockonports.extension

import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.SequenceStepType

/**
 * Checks if a [SequenceStep] is valid based on its [SequenceStepType].
 *
 * A [SequenceStep] is considered valid if its required fields are populated based on its type.
 * Specifically:
 * - For [SequenceStepType.ICMP], the [icmpSize] must not be null.
 * - For [SequenceStepType.TCP] and [SequenceStepType.UDP], the [port] must not be null.
 * - For all other [SequenceStepType] values, the step is considered invalid.
 *
 * @return `true` if the [SequenceStep] is valid, `false` otherwise.
 */
fun SequenceStep.isValid() = when (type) {
    SequenceStepType.ICMP -> icmpSize != null
    SequenceStepType.TCP, SequenceStepType.UDP -> port != null
    else -> false
}

/**
 *  Provides a string description of a [SequenceStep] based on its type and properties.
 *
 *  The description format varies depending on the [SequenceStepType]:
 *  - UDP: "port:UDP" (e.g., "53:UDP")
 *  - TCP: "port:TCP" (e.g., "80:TCP")
 *  - ICMP: "icmpSize x icmpCount:ICMP" (e.g., "64x1:ICMP")
 *  - Other types: An empty string ""
 *
 * @return A string describing the [SequenceStep].
 */
fun SequenceStep.description() = when (type) {
    SequenceStepType.UDP -> "$port:UDP"
    SequenceStepType.TCP -> "$port:TCP"
    SequenceStepType.ICMP -> "${icmpSize}x${icmpCount}:ICMP"
    else -> ""
}