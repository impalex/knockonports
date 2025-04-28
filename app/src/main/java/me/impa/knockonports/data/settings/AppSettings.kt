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

package me.impa.knockonports.data.settings

import androidx.compose.runtime.Immutable
import me.impa.knockonports.R
import me.impa.knockonports.constants.MIN_IP4_HEADER_SIZE
import me.impa.knockonports.ui.config.ThemeConfig

const val PROVIDER_CUSTOM = "custom"

@Immutable
data class AppSettings(
    val widgetConfirmation: Boolean = false,
    val detectPublicIP: Boolean = false,
    val ipv4Service: String = "",
    val ipv6Service: String = "",
    val customIpv4Service: String = "",
    val customIpv6Service: String = "",
    val detailedListView: Boolean = true,
    val customIp4Header: Boolean = false,
    val ip4HeaderSize: Int = MIN_IP4_HEADER_SIZE
)

val Ipv4ProviderMap = mapOf(
    "https://ipv4.wtfismyip.com/text" to R.string.type_provider_wtfismyip,
    "https://api.ipify.org" to R.string.type_provider_ipify,
    "https://icanhazip.com/" to R.string.type_provider_icanhazip,
    PROVIDER_CUSTOM to R.string.type_provider_custom
)

val Ipv6ProviderMap = mapOf(
    "https://ipv6.wtfismyip.com/text" to R.string.type_provider_wtfismyip,
    "https://api6.ipify.org" to R.string.type_provider_ipify,
    "https://ipv6.icanhazip.com/" to R.string.type_provider_icanhazip,
    PROVIDER_CUSTOM to R.string.type_provider_custom
)

