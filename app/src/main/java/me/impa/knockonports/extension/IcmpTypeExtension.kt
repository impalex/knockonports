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

package me.impa.knockonports.extension

import me.impa.knockonports.constants.ICMP_HEADER_SIZE
import me.impa.knockonports.constants.IP6_HEADER_SIZE
import me.impa.knockonports.constants.MAX_PACKET_SIZE
import me.impa.knockonports.constants.MIN_IP4_HEADER_SIZE
import me.impa.knockonports.data.type.IcmpType
import java.net.Inet6Address
import java.net.InetAddress

fun IcmpType.getOffset(type: AddressType, ipv4HeaderSize: Int) = when (this) {
    IcmpType.WITHOUT_HEADERS -> ICMP_HEADER_SIZE
    IcmpType.WITH_ICMP_HEADER -> 0
    IcmpType.WITH_IP_AND_ICMP_HEADERS -> -if (type == AddressType.IPV6) IP6_HEADER_SIZE else ipv4HeaderSize
}

fun IcmpType.getOffset(address: InetAddress, customIp4Header: Boolean, ip4HeaderSize: Int) = this.getOffset(
    if (address is Inet6Address) AddressType.IPV6 else AddressType.IPV4,
    if (customIp4Header) ip4HeaderSize else MIN_IP4_HEADER_SIZE)

fun getMaxIcmpPacketSize(address: InetAddress, customIp4Header: Boolean, ip4HeaderSize: Int) =
    getMaxIcmpPacketSize(
        if (address is Inet6Address) AddressType.IPV6 else AddressType.IPV4,
        if (customIp4Header) ip4HeaderSize else MIN_IP4_HEADER_SIZE)

fun getMaxIcmpPacketSize(type: AddressType, ipv4HeaderSize: Int) =
    if (type == AddressType.IPV6) MAX_PACKET_SIZE - IP6_HEADER_SIZE
    else MAX_PACKET_SIZE - ipv4HeaderSize

enum class AddressType {
    IPV4,
    IPV6
}
