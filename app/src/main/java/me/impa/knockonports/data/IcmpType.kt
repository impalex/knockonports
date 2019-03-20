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

package me.impa.knockonports.data

enum class IcmpType {
    WITHOUT_HEADERS {
        override val offset: Int
            get() = 8
    },
    WITH_ICMP_HEADER {
        override val offset: Int
            get() = 0
    },
    WITH_IP_AND_ICMP_HEADERS {
        override val offset: Int
            get() = -20
    };

    abstract val offset: Int

    companion object {
        val values = IcmpType.values()

        fun fromOrdinal(ordinal: Int): IcmpType = if (ordinal in 0 until values.size) values[ordinal] else WITH_ICMP_HEADER
    }
}