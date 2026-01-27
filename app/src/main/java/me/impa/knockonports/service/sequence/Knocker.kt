/*
 * Copyright (c) 2018-2025 Alexander Yaburov
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

package me.impa.knockonports.service.sequence

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.R
import me.impa.knockonports.constants.MAX_CHECK_RETRIES
import me.impa.knockonports.constants.MAX_IP4_HEADER_SIZE
import me.impa.knockonports.constants.MAX_PACKET_SIZE
import me.impa.knockonports.constants.MAX_PORT
import me.impa.knockonports.constants.MAX_SLEEP
import me.impa.knockonports.constants.MAX_TTL
import me.impa.knockonports.constants.MIN_CHECK_RETRIES
import me.impa.knockonports.constants.MIN_PORT
import me.impa.knockonports.constants.MIN_SLEEP
import me.impa.knockonports.constants.MIN_TTL
import me.impa.knockonports.constants.UDP_HEADER_SIZE
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.settings.PROVIDER_CUSTOM
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.di.MainDispatcher
import me.impa.knockonports.extension.decode
import me.impa.knockonports.extension.getMaxIcmpPacketSize
import me.impa.knockonports.extension.getOffset
import me.impa.knockonports.helper.IpAddressType
import me.impa.knockonports.helper.getPublicIp
import me.impa.knockonports.helper.isNetworkAvailable
import me.impa.knockonports.service.resource.AccessWatcher
import me.impa.knockonports.service.resource.ResourceState
import timber.log.Timber
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.ranges.contains

@Singleton
class Knocker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: KnocksRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val accessWatcher: AccessWatcher,
    private val settingsDataStore: SettingsDataStore
) {

    /**
     * Executes a port knocking sequence.
     *
     * @param sequenceId The ID of the knocking sequence to execute.
     */
    suspend fun knock(sequenceId: Long) {
        if (!isNetworkAvailable(context)) {
            Timber.w("Network not available")
            showToast(R.string.message_error_network_not_avail)
            log(EventType.ERROR_NETWORK)
            return
        }
        val sequence = prepareSequence(sequenceId)
        val steps = prepareSteps(sequence)
        val addresses = getAddresses(sequence) ?: return

        val ipDetectionTasks = launchPublicIpDetection(addresses)

        val maxTries = if (sequence.checkPostKnock)
            sequence.checkRetries.coerceAtLeast(MIN_CHECK_RETRIES).coerceAtMost(MAX_CHECK_RETRIES) else 0

        for (i in 0 until maxTries + 1) {
            Timber.d("Attempt $i of $maxTries")
            launchSequence(sequence, steps, addresses)

            Timber.i("Knocking to \"${sequence.name}\" complete")
            showToast(R.string.message_knocking_complete)
            val resourceState = if (sequence.checkAccess) accessWatcher.checkResource(sequenceId) else null
            if (!sequence.checkPostKnock || resourceState is ResourceState.Available)
                break
        }

        launchApp(sequence)

        launchUri(sequence)

        val publicIps = ipDetectionTasks.awaitAll().filterNotNull().joinToString()
        Timber.d("Public IPs: $publicIps")
        log(EventType.KNOCK, listOf(sequence.name, sequence.host, publicIps, addresses.joinToString()))

        settingsDataStore.incrementKnockCount()
    }

    /**
     * Retrieves a sequence from the repository and performs validation.
     *
     * @param sequenceId The ID of the sequence to retrieve.
     * @return The retrieved [me.impa.knockonports.data.db.entity.Sequence] object.
     * @throws IllegalArgumentException if the sequence is not found or has an empty host.
     */
    private suspend fun prepareSequence(sequenceId: Long): Sequence {
        val sequence = withContext(ioDispatcher) {
            requireNotNull(repository.findSequence(sequenceId)) {
                "Sequence $sequenceId not found"
            }
        }

        require(sequence.host?.isNotBlank() == true) {
            Timber.w("Sequence $sequenceId has empty host")
            showToast(R.string.message_error_empty_host)
            log(EventType.ERROR_INVALID_HOST, listOf(sequence.name))
            "Sequence $sequenceId has empty host"
        }

        return sequence
    }

    /**
     * Prepares a list of valid sequence steps from a given [Sequence].
     *
     * @param sequence The [Sequence] object containing the steps to be prepared.
     * @return A list of [me.impa.knockonports.data.model.SequenceStep] objects representing the
     *         valid steps in the sequence.
     * @throws IllegalStateException if the sequence contains no valid steps. The exception message
     *  will indicate the name of the empty sequence.
     */
    private suspend fun prepareSteps(sequence: Sequence): List<SequenceStep> = requireNotNull(
        sequence.steps?.filter {
            ((it.type == SequenceStepType.UDP || it.type == SequenceStepType.TCP) &&
                    it.port in MIN_PORT..MAX_PORT) || it.type == SequenceStepType.ICMP
        }?.ifEmpty { null }) {
        Timber.w("Empty sequence ${sequence.name}")
        showToast(R.string.message_error_empty_sequence, sequence.name)
        log(EventType.ERROR_EMPTY_SEQUENCE, listOf(sequence.name))
        "Empty sequence ${sequence.name}"
    }

    private suspend fun launchSequence(sequence: Sequence, steps: List<SequenceStep>, addresses: List<InetAddress>) {

        showToast(R.string.message_start_knocking, sequence.name)

        val delay = (sequence.delay ?: 0).coerceAtLeast(MIN_SLEEP).coerceAtMost(MAX_SLEEP).toLong()

        try {
            var cnt = 0
            steps.forEach {
                cnt++
                Timber.d("Step $cnt")
                withContext(ioDispatcher) {
                    sendPackets(
                        addresses, sequence, it, (sequence.ttl ?: 0).coerceAtLeast(MIN_TTL).coerceAtMost(MAX_TTL),
                        delay, sequence.localPort ?: 0
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error while knocking \"${sequence.name}\"")
            showToast(R.string.message_error_unknown, e.message)
            log(EventType.ERROR_UNKNOWN, listOf(e.message))
        }
    }

    @Suppress("LongParameterList")
    private suspend fun sendPackets(
        addresses: List<InetAddress>,
        sequence: Sequence, step: SequenceStep, ttl: Int, delay: Long, localPort: Int
    ) {
        val packet = getPacket(step.type, step)
        val icmpTasks = mutableListOf<Deferred<Int>>()
        for (address in addresses) {
            try {
                when (step.type) {
                    SequenceStepType.TCP -> sendTcpPacket(address, ttl, step.port!!)
                    SequenceStepType.UDP -> sendUdpPacket(address, ttl, step.port!!, localPort, packet)
                    SequenceStepType.ICMP -> {
                        icmpTasks.add(
                            sendIcmpPacket(
                                address, ttl, step.icmpSize, step.icmpCount,
                                sequence.icmpType, delay.toInt(), packet
                            )
                        )
                    }

                    else -> continue
                }
            } catch (e: Exception) {
                Timber.e(e, "Error while sending packet")
                showToast(R.string.message_error_unknown, e.message)
                log(EventType.ERROR_UNKNOWN, listOf(e.message))
            }
        }
        icmpTasks.takeIf { it.isNotEmpty() }?.awaitAll()
        if (delay > 0 && step.type != SequenceStepType.ICMP) delay(delay)
    }

    private fun sendTcpPacket(address: InetAddress, ttl: Int, port: Int) {
        Timber.d("Sending TCP packet to $address:${port}]")
        if (address is Inet4Address) sendtcp(address.hostAddress!!, ttl, port)
        else sendtcp6(address.hostAddress!!, ttl, port)
    }

    @Suppress("LongParameterList")
    private suspend fun sendIcmpPacket(
        address: InetAddress, ttl: Int, size: Int?, count: Int?, type: IcmpType?,
        delay: Int, content: ByteArray
    ): Deferred<Int> {
        val customIp4Header = settingsDataStore.customIp4Header.first()
        val ip4HeaderSize = settingsDataStore.ip4HeaderSize.first()

        val packetSize = ((size ?: 0) + (type?.getOffset(address, customIp4Header, ip4HeaderSize) ?: 0))
            .coerceAtLeast(0)
            .coerceAtMost(getMaxIcmpPacketSize(address, customIp4Header, ip4HeaderSize))
        val packetCount = (count ?: 1).coerceAtLeast(1)
        Timber.d("Sending ICMP packet to $address: [${packetSize}]x[${packetCount}]")

        return if (address is Inet4Address) withContext(ioDispatcher) {
            async { ping(address.hostAddress!!, ttl, packetSize, packetCount, content, delay) }
        } else withContext(ioDispatcher) {
            async { ping6(address.hostAddress!!, ttl, packetSize, packetCount, content, delay) }
        }
    }

    private fun sendUdpPacket(address: InetAddress, ttl: Int, port: Int, localPort: Int, content: ByteArray) {
        Timber.d("Sending UDP packet to $address:${port} [${content.size}]")
        if (address is Inet4Address) sendudp(address.hostAddress!!, ttl, port, localPort, content)
        else sendudp6(address.hostAddress!!, ttl, port, localPort, content)
    }

    /**
     * Retrieves the packet data for a given sequence step.
     *
     * @param type The type of the sequence step, or null if not applicable.
     * @param step The sequence step object containing the content and encoding information.
     * @return A byte array representing the packet data, which may be empty.
     */
    @Suppress("NestedBlockDepth")
    private fun getPacket(type: SequenceStepType?, step: SequenceStep) =
        if (type == SequenceStepType.TCP) {
            ByteArray(0)
        } else {
            (step.encoding?.decode(step.content) ?: ByteArray(0)).let { packet ->
                step.udpPayloadSize?.takeIf { step.type == SequenceStepType.UDP }?.let {
                    // Resize UDP packet to payload size
                    val size = it.coerceAtMost(MAX_PACKET_SIZE - UDP_HEADER_SIZE - MAX_IP4_HEADER_SIZE)
                    if (size >= 0) packet.copyOf(size)
                    else packet
                } ?: packet
            }
        }

    /**
     * Retrieves a list of IP addresses for a given host based on the specified protocol version preference.
     *
     * @param sequence The [Sequence] object containing the host to resolve and the preferred IP protocol version.
     * @return A list of [InetAddress] objects representing the resolved IP addresses, or null if no addresses
     *         could be resolved or an error occurred.
     */
    private suspend fun getAddresses(sequence: Sequence) = withContext(ioDispatcher) {
        try {
            InetAddress.getAllByName(sequence.host)
                .asSequence()
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
                        else -> Int.MAX_VALUE
                    }
                }
                .take(if (sequence.ipv == ProtocolVersionType.BOTH) 2 else 1)
                .toList()
                .ifEmpty {
                    Timber.w("No addresses found for host ${sequence.host}")
                    showToast(R.string.message_error_resolve, sequence.host)
                    log(EventType.ERROR_RESOLVE_HOST, listOf(sequence.host))
                    null
                }
        } catch (e: UnknownHostException) {
            Timber.w("Unknown host ${sequence.host}")
            showToast(R.string.message_error_unknown_host, sequence.host)
            log(EventType.ERROR_RESOLVE_HOST, listOf(sequence.host, e.message))
            null
        }
    }

    /**
     * Launches asynchronous tasks to detect public IP addresses for the given list of InetAddresses.
     *
     * This function checks if public IP detection is enabled in the app settings.
     * If disabled, it returns an empty list.
     * Otherwise, it iterates through the provided list of InetAddresses, creating an asynchronous task for
     * each address to determine public IP using the configured services.
     * It respects custom service URLs if configured.
     *
     * @param addresses A list of [InetAddress] objects representing network addresses.
     *        Only [Inet4Address] and [Inet6Address] are processed; other types are ignored.
     * @return A list of [Deferred] objects, each representing an asynchronous task that will
     *         eventually yield a nullable [String] representing the detected public IP address.
     *         If no tasks are created, an empty list is returned.  The returned list corresponds
     *         to the input `addresses` list, but may contain fewer elements due to filtering of
     *         unsupported address types.
     */
    private suspend fun launchPublicIpDetection(addresses: List<InetAddress>): List<Deferred<String?>> {
        if (!settingsDataStore.detectPublicIP.first())
            return emptyList()

        val ipv4Service = with(settingsDataStore) {
            ipv4Service.first().takeIf { it != PROVIDER_CUSTOM } ?: customIpv4Service.first()
        }
        val ipv6Service = with(settingsDataStore) {
            ipv6Service.first().takeIf { it != PROVIDER_CUSTOM } ?: customIpv6Service.first()
        }
        val publicIpDetectionTasks = addresses.mapNotNull {
            when (it) {
                is Inet4Address -> withContext(ioDispatcher) { async { getPublicIp(ipv4Service, IpAddressType.IPV4) } }
                is Inet6Address -> withContext(ioDispatcher) { async { getPublicIp(ipv6Service, IpAddressType.IPV6) } }
                else -> null
            }
        }.toList()

        return publicIpDetectionTasks
    }

    /**
     * Launches the application associated with the given sequence.
     *
     * @param sequence The sequence containing the application package name.
     *
     * This function attempts to launch the application specified by the `sequence.application` package name.
     * If the package name is valid and a launch intent is found, the application is launched.
     * Otherwise, a warning is logged, and an error toast message is displayed.
     */
    private suspend fun launchApp(sequence: Sequence) = sequence.application?.takeIf { it.isNotBlank() }?.let {
        Timber.i("Starting $it...")
        val launchIntent = context.packageManager.getLaunchIntentForPackage(it)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Timber.w("Could not find launch intent")
            showToast(R.string.message_error_app_launch, sequence.applicationName)
        }
    }

    /**
     * Launches a URI associated with a [Sequence].
     *
     * @param sequence The [Sequence] object potentially containing a URI to launch.
     */
    private fun launchUri(sequence: Sequence) = sequence.uri?.let {
        try {
            it.toUri()
        } catch (e: Exception) {
            Timber.w(e, "Invalid URI: $it")
            null
        }
    }?.takeIf {
        // Don't open URI with our scheme to prevent infinite loops
        it.scheme != BuildConfig.APP_SCHEME
    }?.let {
        Timber.i("Opening $it...")
        context.startActivity(Intent(Intent.ACTION_VIEW, it).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
    }


    /**
     * Shows a short toast message on the UI thread.
     *
     * @param resourceId The resource ID of the string to display in the toast.
     */
    private suspend fun showToast(resourceId: Int) {
        withContext(mainDispatcher) {
            Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Displays a short toast message on the screen.
     *
     * The message is retrieved from the application's resources using the provided resource ID and formatted
     * with the given arguments.
     * The toast is displayed on the main thread.
     *
     * @param resourceId The resource ID of the string to display in the toast.
     * @param args Optional arguments to format the string.  These arguments will be used to replace
     *        placeholders in the string defined by [resourceId].  See [android.content.res.Resources.getString]
     *        for formatting details.
     */
    private suspend fun showToast(resourceId: Int, vararg args: Any?) {
        withContext(mainDispatcher) {
            Toast.makeText(context, context.getString(resourceId, *args), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Logs an event of the specified [type] with optional associated [data].
     *
     * This function saves a log entry to the underlying repository, encapsulating the process within
     * the IO dispatcher for non-blocking operation.
     *
     * @param type The type of event being logged (e.g., SCREEN_VIEW, BUTTON_CLICK).
     * @param data An optional list of strings representing data associated with the event.
     *             This can be null or contain null values, which will be handled by the repository.
     */
    private suspend fun log(type: EventType, data: List<String?>? = null) {
        withContext(ioDispatcher) {
            repository.saveLogEntry(LogEntry(event = type, data = data))
        }
    }

    @Suppress("Unused", "LongParameterList")
    private external fun ping(address: String, ttl: Int, size: Int, count: Int, pattern: ByteArray, sleep: Int): Int

    @Suppress("Unused", "LongParameterList")
    private external fun ping6(address: String, ttl: Int, size: Int, count: Int, pattern: ByteArray, sleep: Int): Int

    @Suppress("Unused")
    private external fun sendtcp(host: String, ttl: Int, port: Int): Int

    @Suppress("Unused")
    private external fun sendtcp6(host: String, ttl: Int, port: Int): Int

    @Suppress("Unused")
    private external fun sendudp(host: String, ttl: Int, port: Int, localPort: Int, data: ByteArray): Int

    @Suppress("Unused")
    private external fun sendudp6(host: String, ttl: Int, port: Int, localPort: Int, data: ByteArray): Int

}