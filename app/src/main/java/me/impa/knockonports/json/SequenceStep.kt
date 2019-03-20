/*
 * Copyright (c) 2019 Alexander Yaburov
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

import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.data.SequenceStepType
import kotlin.properties.Delegates

data class SequenceStep(var type: SequenceStepType?, var port: Int?, var icmpSize: Int?, var icmpCount: Int?, var content: String?, var encoding: ContentEncoding?) {

    var onIcmpSizeOffsetChanged: ((Int, Int) -> Unit)? = null

    var isExpanded = !content.isNullOrBlank() && type != SequenceStepType.TCP

    private var _icmpSizeOffset: Int = 0

    var icmpSizeOffset: Int by Delegates.observable(_icmpSizeOffset) { _, old, new ->
        if (old != new)
            onIcmpSizeOffsetChanged?.invoke(old, new)
    }

    fun isValid() = when (type) {
        SequenceStepType.ICMP -> icmpSize != null
        SequenceStepType.TCP, SequenceStepType.UDP -> port != null
        else -> false
    }
}