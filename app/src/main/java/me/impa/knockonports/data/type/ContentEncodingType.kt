/*
 * Copyright (c) 2018-2025 Alexander Yaburov
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

package me.impa.knockonports.data.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.impa.knockonports.extension.EnumCompanion

@Serializable
enum class ContentEncodingType {
    @SerialName("raw")
    RAW,
    @SerialName("base64")
    BASE64,
    @SerialName("hex")
    HEX;

    companion object : EnumCompanion<ContentEncodingType>(ContentEncodingType.entries.toTypedArray())
}
