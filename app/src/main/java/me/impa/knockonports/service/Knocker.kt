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
import me.impa.knockonports.R
import me.impa.knockonports.data.IcmpType
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.fragment.RateAppFragment
import org.jetbrains.anko.*
import java.net.*

class Knocker(val context: Context, private val sequence: Sequence): AnkoLogger {

    private val _maxSleep = 15000

    fun execute() {
        if (sequence.host.isNullOrBlank()) {
            warn { "Empty host '${sequence.name}'" }
            context.runOnUiThread {
                toast(R.string.host_not_set)
            }
            return
        }

        val address = try {
            InetAddress.getByName(sequence.host)
        } catch (e: UnknownHostException) {
            context.runOnUiThread {
                toast(getString(R.string.error_resolve, sequence.host))
            }
            return
        } catch (e: Exception) {
            warn("Resolve error", e)
            return
        }

        val steps = sequence.steps?.filter {
            ((it.type == SequenceStepType.UDP || it.type == SequenceStepType.TCP) && it.port in 1..65535) || it.type == SequenceStepType.ICMP
        }

        if (steps == null || steps.isEmpty()) {
            warn { "Empty sequence '${sequence.name}'" }
            context.runOnUiThread {
                toast(getString(R.string.empty_sequence_warning, sequence.name))
            }
            return
        }

        val icmpSizeOffset = when (sequence.icmpType) {
            IcmpType.WITH_IP_AND_ICMP_HEADERS -> -20
            IcmpType.WITHOUT_HEADERS -> 8
            else -> 0
        }

        info { "Knocking to '${sequence.name}'" }
        RateAppFragment.incKnockCount(context)

        context.runOnUiThread {
            toast(getString(R.string.start_knocking, sequence.name))
        }
        debug { "Remote address $address" }

        val udpSocket = if (steps.any{it.type == SequenceStepType.UDP}) DatagramSocket() else null
        val delay = Math.min(Math.max(sequence.delay ?: 0, 0), _maxSleep).toLong()

        try {
            var cnt = 0
            steps.forEach {
                info { "Knock #${++cnt}" }
                val packet = if (it.type == SequenceStepType.TCP || it.content.isNullOrBlank()) {
                    ByteArray(0)
                } else  {
                    it.encoding?.decode(it.content) ?: ByteArray(0)
                }
                try {
                    when (it.type) {
                        SequenceStepType.UDP -> {
                            debug("Knock UDP ${it.port}")
                            udpSocket?.send(DatagramPacket(packet, packet.size, address, it.port!!))
                            if (delay > 0)
                                Thread.sleep(delay)
                        }
                        SequenceStepType.TCP -> {
                            debug("Knock TCP ${it.port}")
                            sendtcp(address.hostAddress, it.port!!)
                            if (delay > 0)
                                Thread.sleep(delay)
                        }
                        SequenceStepType.ICMP -> {
                            debug("Knock ICMP")
                            ping(address.hostAddress, Math.max((it.icmpSize
                                    ?: 0) + icmpSizeOffset, 0), Math.max(it.icmpCount
                                    ?: 1, 1), packet, delay.toInt())
                        }
                    }
                } catch (e: Exception) {
                    warn("Error while sending knock", e)
                }
            }
        } catch (e: Exception) {
            warn("Knocking error", e)
        }
        finally {
            udpSocket?.close()
        }

        info("Knocking complete")
        context.runOnUiThread {
            toast(R.string.end_knocking)
        }

        val app = sequence.application
        if (!app.isNullOrEmpty()) {
            info { "Starting $app..." }
            val launchIntent = context.packageManager.getLaunchIntentForPackage(app)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            } else {
                warn { "Could not find launch intent" }
                context.runOnUiThread {
                    toast(context.getString(R.string.error_app_launch, sequence.applicationName))
                }
            }
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