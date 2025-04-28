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

package me.impa.knockonports.screen.viewmodel.state.sequence

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import me.impa.knockonports.constants.MIN_IP4_HEADER_SIZE
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.screen.validate.ValidationResult

@Immutable
data class UiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val savedSequenceId: Long? = null,
    val newStepId: Int? = null,
    val title: String = "",
    val titleValidation: ValidationResult = ValidationResult.Valid,
    val host: String = "",
    val hostValidation: ValidationResult = ValidationResult.Valid,
    val steps: ImmutableList<StepUiState> = persistentListOf(),
    val delay: Int? = null,
    val delayValidation: ValidationResult = ValidationResult.Valid,
    val localPort: Int? = null,
    val localPortValidation: ValidationResult = ValidationResult.Valid,
    val ttl: Int? = null,
    val ttlValidation: ValidationResult = ValidationResult.Valid,
    val protocolVersion: ProtocolVersionType = ProtocolVersionType.PREFER_IPV4,
    val icmpSizeType: IcmpType = IcmpType.WITHOUT_HEADERS,
    val appPackage: String? = null,
    val appName: String? = null,
    val uri: String? = null,
    val order: Int? = null,
    val ip4HeaderSize: Int = MIN_IP4_HEADER_SIZE
)
