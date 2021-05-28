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

import me.impa.knockonports.ext.EnumCompanion
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.security.InvalidParameterException

enum class IcmpType {
    WITHOUT_HEADERS {
        override fun getOffset(address: InetAddress?): Int = 8
    },
    WITH_ICMP_HEADER {
        override fun getOffset(address: InetAddress?): Int = 0
    },
    WITH_IP_AND_ICMP_HEADERS {
        override fun getOffset(address: InetAddress?): Int = when(address) {
            is Inet4Address, null -> -20
            is Inet6Address -> -40
            else -> throw InvalidParameterException()
        }
    };

    abstract fun getOffset(address: InetAddress? = null): Int

    companion object : EnumCompanion<IcmpType>(values(), WITH_ICMP_HEADER)
}