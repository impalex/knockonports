/*
 * Copyright (c) 2018 Alexander Yaburov
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

package me.impa.knockonports.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.webkit.URLUtil
import kotlinx.coroutines.*
import me.impa.knockonports.R
import me.impa.knockonports.data.EventType
import me.impa.knockonports.data.ProtocolVersionType
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.database.KnocksDatabase
import me.impa.knockonports.database.entity.LogEntry
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.util.*
import java.net.*
import java.util.regex.Pattern

class Knocker(val context: Context, private val sequence: Sequence): Logging {

    private val _maxSleep = 15000
    private val _maxIcmpSize4 = 65515
    private val _maxIcmpSize6 = 65495

    private val disableTag by lazy { context.getString(R.string.const_disable) }
    private val customTag by lazy { context.getString(R.string.const_custom) }

    private val ipv4Pattern by lazy { Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$") }
    private val ipv6Pattern by lazy { Pattern.compile("^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|" +
            "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}" +
            "(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
            "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
            "::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|" +
            "([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$")}

    fun execute() {
        val log = runBlocking { KnocksDatabase.getInstance(context)?.logEntryDao() }

        if (!isNetworkAvailable()) {
            warn { "Network not available" }
            CoroutineScope(Dispatchers.Main).launch {
                context.toast(R.string.error_network_not_avail)
            }
            runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_NETWORK)) }
            return
        }

        if (sequence.host.isNullOrBlank()) {
            warn { "Empty host '${sequence.name}'" }
            CoroutineScope(Dispatchers.Main).launch {
                context.toast(R.string.host_not_set)
            }
            runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_INVALID_HOST, data = listOf(sequence.name))) }
            return
        }

        val addresses = try {
            InetAddress.getAllByName(sequence.host)
                    .distinctBy { it is Inet4Address }
                    .filter {
                        when (sequence.ipv) {
                            ProtocolVersionType.ONLY_IPV4 -> it is Inet4Address
                            ProtocolVersionType.ONLY_IPV6 -> it is Inet6Address
                            else -> true
                        }
                    }
                    .sortedBy {
                        when (it) {
                            is Inet4Address -> if (sequence.ipv == ProtocolVersionType.PREFER_IPV4) 0 else 1
                            is Inet6Address -> if (sequence.ipv == ProtocolVersionType.PREFER_IPV6) 0 else 1
                            else -> 100
                        }
                    }
                    .take(if (sequence.ipv == ProtocolVersionType.BOTH) 2 else 1)
                    .toList()
                    .takeIf { it.isNotEmpty() } ?: throw java.lang.Exception()
        } catch (e: UnknownHostException) {
            CoroutineScope(Dispatchers.Main).launch {
                context.toast(context.getString(R.string.error_resolve, sequence.host))
            }
            runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_RESOLVE_HOST, data = listOf(sequence.host, e.message))) }
            return
        } catch (e: Exception) {
            warn("Resolve error", e)
            CoroutineScope(Dispatchers.Main).launch {
                context.toast(context.getString(R.string.error_resolve, sequence.host))
            }
            runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_RESOLVE_HOST, data = listOf(sequence.host, e.message))) }
            return
        }

        val steps = sequence.steps?.filter {
            ((it.type == SequenceStepType.UDP || it.type == SequenceStepType.TCP) && it.port in 1..65535) || it.type == SequenceStepType.ICMP
        }

        if (steps == null || steps.isEmpty()) {
            warn { "Empty sequence '${sequence.name}'" }
            CoroutineScope(Dispatchers.Main).launch {
                context.toast(context.getString(R.string.empty_sequence_warning, sequence.name))
            }
            runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_EMPTY_SEQUENCE, data = listOf(sequence.name))) }
            return
        }

        var ipv4Service = AppPrefs.getIP4Service(context)
        if (ipv4Service == customTag)
            ipv4Service = AppPrefs.getCustomIP4Service(context)
        var ipv6Service = AppPrefs.getIP6Service(context)
        if (ipv6Service == customTag)
            ipv6Service = AppPrefs.getCustomIP6Service(context)

        val externalIpTasks = addresses.mapNotNull {
            when {
                it is Inet4Address && ipv4Service != disableTag -> GlobalScope.async { getExternalIp(ipv4Service, ipv4Pattern) }
                it is Inet6Address && ipv6Service != disableTag -> GlobalScope.async { getExternalIp(ipv6Service, ipv6Pattern) }
                else -> null
            }
        }

        info { "Knocking to '${sequence.name}'" }
        AppPrefs.incKnockCount(context)

        val knockLogEvent = LogEntry(event = EventType.KNOCK)

        CoroutineScope(Dispatchers.Main).launch {
            context.toast(context.getString(R.string.start_knocking, sequence.name))
        }

        debug { "Remote address ${addresses.joinToString()}" }

        val udpSocket = if (steps.any { it.type == SequenceStepType.UDP }) DatagramSocket() else null
        val delay = (sequence.delay ?: 0).coerceAtLeast(0).coerceAtMost(_maxSleep).toLong()

        try {
            var cnt = 0
            steps.forEach {
                info { "Knock #${++cnt}" }
                val packet = if (it.type == SequenceStepType.TCP || it.content.isNullOrBlank()) {
                    ByteArray(0)
                } else {
                    it.encoding?.decode(it.content) ?: ByteArray(0)
                }
                try {
                    val icmpTasks = mutableListOf<Deferred<Int>>()
                    for (address in addresses) {
                        when (it.type) {
                            SequenceStepType.UDP -> {
                                debug { "Knock UDP ${it.port}" }
                                udpSocket?.send(DatagramPacket(packet, packet.size, address, it.port!!))
                            }
                            SequenceStepType.TCP -> {
                                debug { "Knock TCP ${it.port}" }
                                address.run {
                                    if (this is Inet4Address)
                                        sendtcp(this.hostAddress, it.port!!)
                                    else
                                        sendtcp6(this.hostAddress, it.port!!)
                                }
                            }
                            SequenceStepType.ICMP -> {
                                debug { "Knock ICMP" }
                                val packetSize = ((it.icmpSize ?: 0) + (sequence.icmpType?.getOffset(address) ?: 0))
                                    .coerceAtLeast(0)
                                    .coerceAtMost(if (address is Inet4Address) _maxIcmpSize4 else _maxIcmpSize6)
                                val packetCount = (it.icmpCount ?: 1).coerceAtLeast(1)
                                address.run {
                                    icmpTasks.add(GlobalScope.async {
                                        if (this@run is Inet4Address)
                                            ping(this@run.hostAddress, packetSize, packetCount, packet, delay.toInt())
                                        else
                                            ping6(this@run.hostAddress, packetSize, packetCount, packet, delay.toInt())
                                    })
                                }
                            }
                        }
                    }
                    if (icmpTasks.size>0)
                        runBlocking { icmpTasks.awaitAll() }
                    if (delay > 0 && it.type != SequenceStepType.ICMP)
                        Thread.sleep(delay)
                } catch (e: Exception) {
                    warn("Error while sending knock", e)
                    runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_UNKNOWN, data = listOf(e.message))) }
                }
            }
        } catch (e: Exception) {
            warn("Knocking error", e)
            runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_UNKNOWN, data = listOf(e.message))) }
        } finally {
            udpSocket?.close()
        }

        info { "Knocking complete" }
        CoroutineScope(Dispatchers.Main).launch {
            context.toast(R.string.end_knocking)
        }

        val app = sequence.application
        if (!app.isNullOrEmpty()) {
            info { "Starting $app..." }
            val launchIntent = context.packageManager.getLaunchIntentForPackage(app)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            } else {
                warn { "Could not find launch intent" }
                CoroutineScope(Dispatchers.Main).launch {
                    context.toast(context.getString(R.string.error_app_launch, sequence.applicationName))
                }
            }
        }
        val externalIp = if (externalIpTasks.isEmpty()) null else runBlocking {
            externalIpTasks.awaitAll()
        }.filterNotNull().joinToString()

        runBlocking { log?.insertLogEntry(knockLogEvent.apply { data = listOf(sequence.name, sequence.host, externalIp, addresses.joinToString()) }) }
    }

    private fun isNetworkAvailable() = try {
        //(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(networkCapabilities)!!
            when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
    } catch (_: Exception) {
        false
    }

    private fun getExternalIp(serviceUrl: String?, pattern: Pattern): String? {
        if (!URLUtil.isValidUrl(serviceUrl)) {
            error("Invalid URL: $serviceUrl")
            return null
        }
        info { "Trying to get my IP from $serviceUrl" }
        return try {
            val response = URL(serviceUrl).openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
            }.getInputStream().bufferedReader().use { it.readText() }.trim()
            info { "Response from $serviceUrl: $response"}
            if (pattern.matcher(response).matches()) response else null
        } catch (e: Exception) {
            error("Unable to get IP from $serviceUrl", e)
            null
        }
    }

    private external fun ping(address: String, size: Int, count: Int, pattern: ByteArray, sleep: Int): Int
    private external fun ping6(address: String, size: Int, count: Int, pattern: ByteArray, sleep: Int): Int
    private external fun sendtcp(host: String, port: Int): Int
    private external fun sendtcp6(host: String, port: Int): Int

    companion object {
        init {
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR2)
                System.loadLibrary("c++_shared")
            System.loadLibrary("netutil")
        }
    }

}