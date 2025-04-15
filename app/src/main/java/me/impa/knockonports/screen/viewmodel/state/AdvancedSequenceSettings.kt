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

package me.impa.knockonports.screen.viewmodel.state

import androidx.compose.runtime.Immutable
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType

@Immutable
data class AdvancedSequenceSettings(
    val delay: Int? = null,
    val localPort: Int? = null,
    val ttl: Int? = null,
    val protocolVersion: ProtocolVersionType = ProtocolVersionType.PREFER_IPV4,
    val icmpSizeType: IcmpType = IcmpType.WITHOUT_HEADERS,
    val appPackage: String? = null,
    val appName: String? = null,
    val uri: String? = null
)