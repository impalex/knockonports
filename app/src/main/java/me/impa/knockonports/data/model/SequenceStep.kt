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

package me.impa.knockonports.data.model

import androidx.compose.runtime.Immutable
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.SequenceStepType
import java.util.UUID

@Immutable
data class SequenceStep(
    val type: SequenceStepType? = SequenceStepType.UDP,
    val port: Int? = null,
    val icmpSize: Int? = null,
    val icmpCount: Int? = null,
    val content: String? = null,
    val encoding: ContentEncodingType? = ContentEncodingType.RAW,
    val id: String = UUID.randomUUID().toString()
)