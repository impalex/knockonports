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

package me.impa.knockonports.data.settings

import me.impa.knockonports.R

const val PROVIDER_CUSTOM = "custom"

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

