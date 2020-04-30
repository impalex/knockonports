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
import android.util.EventLog
import kotlinx.coroutines.*
import me.impa.knockonports.R
import me.impa.knockonports.data.EventType
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.database.KnocksDatabase
import me.impa.knockonports.database.entity.LogEntry
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.util.*
import java.net.*
import java.util.regex.Pattern

class Knocker(val context: Context, private val sequence: Sequence): Logging {

    private val _maxSleep = 15000
    private val _maxIcmpSize = 65515

    private val ipPattern by lazy { Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$") }

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

        val address = try {
            InetAddress.getAllByName(sequence.host).first { it is Inet4Address }
        } catch (e: UnknownHostException) {
            CoroutineScope(Dispatchers.Main).launch {
                context.toast(context.getString(R.string.error_resolve, sequence.host))
            }
            runBlocking { log?.insertLogEntry(LogEntry(event = EventType.ERROR_RESOLVE_HOST, data = listOf(sequence.host, e.message))) }
            return
        } catch (e: Exception) {
            warn("Resolve error", e)
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

        val externalIpTask = GlobalScope.async {
            getExternalIp()
        }

        val icmpSizeOffset = sequence.icmpType?.offset ?: 0

        info { "Knocking to '${sequence.name}'" }
        AppPrefs.incKnockCount(context)

        val knockLogEvent = LogEntry(event = EventType.KNOCK)

        CoroutineScope(Dispatchers.Main).launch {
            context.toast(context.getString(R.string.start_knocking, sequence.name))
        }
        debug { "Remote address $address" }

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
                    when (it.type) {
                        SequenceStepType.UDP -> {
                            debug { "Knock UDP ${it.port}" }
                            udpSocket?.send(DatagramPacket(packet, packet.size, address, it.port!!))
                            if (delay > 0)
                                Thread.sleep(delay)
                        }
                        SequenceStepType.TCP -> {
                            debug { "Knock TCP ${it.port}" }
                            sendtcp(address.hostAddress, it.port!!)
                            if (delay > 0)
                                Thread.sleep(delay)
                        }
                        SequenceStepType.ICMP -> {
                            debug { "Knock ICMP" }
                            ping(address.hostAddress,
                                    ((it.icmpSize
                                            ?: 0) + icmpSizeOffset).coerceAtLeast(0).coerceAtMost(_maxIcmpSize),
                                    (it.icmpCount ?: 1).coerceAtLeast(1), packet, delay.toInt())
                        }
                    }
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
        val externalIp = runBlocking {
            externalIpTask.await()
        }
        runBlocking { log?.insertLogEntry(knockLogEvent.apply { data = listOf(sequence.name, sequence.host, externalIp, address.hostAddress.toString()) }) }
    }

    private fun isNetworkAvailable() = try {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true
    } catch (_: Exception) {
        false
    }

    private fun getExternalIp(): String? {
        val serviceList = listOf("http://whatismyip.akamai.com/", "https://ipecho.net/plain", "http://wtfismyip.com/text", "https://api.ipify.org", "https://icanhazip.com/")
        for (service in serviceList.shuffled()) {
            val ip = getExternalIp(service)
            if (ip != null)
                return ip
        }
        return null
    }

    private fun getExternalIp(serviceUrl: String): String? {
        info { "Trying to get my IP from $serviceUrl" }
        try {
            val response = URL(serviceUrl).openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
            }.getInputStream().bufferedReader().use { it.readText() }

            return if (ipPattern.matcher(response).matches()) response else null
        }
        catch (e: Exception) {
            error("Unable to get IP from $serviceUrl", e)
            return null
        }
    }

    private external fun ping(address: String, size: Int, count: Int, pattern: ByteArray, sleep: Int): Int
    private external fun sendtcp(host: String, port: Int): Int

    companion object {
        init {
            System.loadLibrary("netutil")
        }
    }

}