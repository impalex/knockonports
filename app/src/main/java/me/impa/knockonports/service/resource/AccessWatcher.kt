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

package me.impa.knockonports.service.resource

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import me.impa.knockonports.R
import me.impa.knockonports.constants.CHECK_RESOURCE_TIMER_RESOLUTION
import me.impa.knockonports.constants.MS_IN_SECOND
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.CheckAccessData
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.data.type.CheckAccessType
import me.impa.knockonports.di.DefaultDispatcher
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.helper.TextResource
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val MIN_GOOD_HTTP_CODE = 200
private const val MAX_GOOD_HTTP_CODE = 299
private const val MAX_CONCURRENT_CHECKS = 5

/**
 * The [AccessWatcher] class is responsible for periodically checking the availability of configured resources
 * (URLs, ports, or pings). It observes changes in the list of resources to track and the application settings
 * for the check period.
 *
 * The class uses a timer flow to trigger checks based on the configured period and the last known state
 * of each resource. Checks are performed asynchronously on an IO dispatcher and the results are updated
 * in a StateFlow, which can be observed. A semaphore is used to limit the number of concurrent resource checks.
 */
@Singleton
class AccessWatcher @Inject constructor(
    private val repository: KnocksRepository,
    settingsDataStore: SettingsDataStore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : DefaultLifecycleObserver {

    private lateinit var defaultScope: CoroutineScope

    private lateinit var ioScope: CoroutineScope

    private val checkSemaphore = Semaphore(MAX_CONCURRENT_CHECKS)

    private val trackResources: Flow<List<CheckAccessData>> = repository.getAccessResources()

    private val checkPeriod = settingsDataStore.resourceCheckPeriod

    private val _resourceState = MutableStateFlow<ImmutableMap<CheckAccessData, ResourceState>>(persistentMapOf())

    val resourceState: Flow<ImmutableMap<CheckAccessData, ResourceState>> = _resourceState

    private val timerFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(CHECK_RESOURCE_TIMER_RESOLUTION)
        }
    }

    private var processJob: Job? = null

    /**
     * Starts the resource check process.
     *
     */
    private suspend fun startResourceCheck() {
        combine(timerFlow, checkPeriod, _resourceState) { timer, period, resources ->
            resources.filter {
                when (it.value) {
                    is ResourceState.Unknown -> true
                    is ResourceState.Checking -> false
                    is ResourceState.Available -> it.value.date + period < timer
                    is ResourceState.Unavailable -> it.value.date + period < timer
                }
            }.map { it.key }
        }.cancellable().collect {
            it.takeIf { it.isNotEmpty() }?.let { checkResources(it) }
        }
    }

    /**
     * Initiates the process of checking the provided list of resources.
     * Updates the state of the given resources to [ResourceState.Checking] and launches
     * asynchronous jobs to perform the actual resource checks, limited by a semaphore.
     *
     * @param resources The list of [CheckAccessData] resources to be checked.
     */
    private fun checkResources(resources: List<CheckAccessData>) {
        _resourceState.update {
            it.mapValues { entry ->
                if (resources.contains(entry.key)) ResourceState.Checking() else entry.value
            }.toPersistentMap()
        }
        resources.forEach {
            ioScope.launch { checkSemaphore.withPermit { checkResource(it) } }
        }
    }

    /**
     * Initiates a resource check for the resource with the given [sequenceId].
     *
     * If the resource is already being tracked and its state is not [ResourceState.Checking],
     * its state is updated to [ResourceState.Checking] and the check is performed.
     * If the resource is not currently tracked, it attempts to retrieve it from the repository,
     * updates the state to [ResourceState.Checking] if found, and then performs the check.
     *
     * @param sequenceId The unique identifier of the resource to check.
     * @return The resulting [ResourceState] after the check, or `null` if the resource
     * was not found.
     */
    suspend fun checkResource(sequenceId: Long): ResourceState? =
        (_resourceState.value.entries.firstOrNull { it.key.id == sequenceId }?.key
            ?: repository.getAccessResourceById(sequenceId))?.let { resource ->
            _resourceState.update {
                it.mapValues { entry ->
                    if (entry.key == resource) ResourceState.Checking() else entry.value
                }.toPersistentMap()
            }
            checkResource(resource)
        }

    /**
     * Performs a resource check based on the type specified in the [CheckAccessData].
     *
     * Updates the [_resourceState] with the result of the check.
     *
     * @param resource The [CheckAccessData] object containing the details for the resource to check.
     * @return The [ResourceState] representing the result of the check.
     */
    private fun checkResource(resource: CheckAccessData): ResourceState {
        val result = when (resource.checkType) {
            CheckAccessType.URL -> checkUrl(resource.checkHost ?: "", resource.checkTimeout)
            CheckAccessType.PORT -> checkPort(
                resource.checkHost ?: "",
                resource.checkPort ?: 0,
                resource.checkTimeout
            )

            CheckAccessType.PING -> checkPing(resource.checkHost ?: "", resource.checkTimeout)
        }
        _resourceState.update {
            it.mapValues { entry ->
                if (entry.key == resource) result else entry.value
            }.toPersistentMap()
        }
        return result
    }

    /**
     * Checks the availability of a given URL by attempting to connect to it.
     *
     * This function establishes a connection to the specified URL with the given timeout.
     * If the connection is successful and the HTTP response code is within the range of 200-299 (inclusive),
     * the resource is considered available. Otherwise, it's considered unavailable.
     *
     * Note that due to potential multiple IP addresses for a hostname, the actual connection timeout
     * might be longer than the specified value, as each address is tried sequentially.
     *
     * @param url The URL string to check.
     * @param timeout The timeout in seconds for the connection.
     * @return A [ResourceState] indicating whether the URL is [ResourceState.Available] or [ResourceState.Unavailable].
     */
    @Suppress("ReturnCount")
    private fun checkUrl(url: String, timeout: Int): ResourceState {
        try {
            val connection = URL(url).openConnection().apply {
                connectTimeout = timeout * MS_IN_SECOND
                readTimeout = timeout * MS_IN_SECOND
                if (this is HttpURLConnection) {
                    requestMethod = "GET"
                    instanceFollowRedirects = false
                }
            }
            try {
                /*
                Important note: the real timeout might be more than the specified one, this is not an error.
                See: URLConnection.setConnectTimeout
                Quote: If the hostname resolves to multiple IP addresses, Android's default implementation of
                HttpURLConnection will try each in RFC 3484 order. If connecting to each of these addresses fails,
                multiple timeouts will elapse before the connect attempt throws an exception. Host names that
                support both IPv6 and IPv4 always have at least 2 IP addresses
                 */
                connection.connect()
                val code = (connection as? HttpURLConnection)?.responseCode
                    ?: throw IOException("Not an HTTP/HTTPS connection")
                return if (code in MIN_GOOD_HTTP_CODE..MAX_GOOD_HTTP_CODE) {
                    ResourceState.Available()
                } else {
                    ResourceState.Unavailable(error = TextResource.DynamicText(R.string.text_error_http_code, code))
                }
            } finally {
                (connection as? HttpURLConnection)?.disconnect()
            }
        } catch (_: SocketTimeoutException) {
            return ResourceState.Unavailable(error = TextResource.DynamicText(R.string.text_error_no_response))
        } catch (e: Exception) {
            return ResourceState.Unavailable(error = e.message?.let { TextResource.PlainText(it) }
                ?: TextResource.DynamicText(R.string.text_error_unknown))
        }
    }

    /**
     * Checks the availability of a specific port on a given host.
     *
     * @param host The hostname or IP address of the target.
     * @param port The port number to check.
     * @param timeout The timeout in seconds for the connection attempt.
     * @return [ResourceState.Available] if the port is reachable within the timeout,
     *   [ResourceState.Unavailable] otherwise.
     */
    @Suppress("ReturnCount")
    private fun checkPort(host: String, port: Int, timeout: Int): ResourceState {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeout * MS_IN_SECOND)
                return ResourceState.Available()
            }
        } catch (_: SocketTimeoutException) {
            return ResourceState.Unavailable(error = TextResource.DynamicText(R.string.text_error_no_response))
        } catch (e: Exception) {
            return ResourceState.Unavailable(error = e.message?.let { TextResource.PlainText(it) }
                ?: TextResource.DynamicText(R.string.text_error_unknown))
        }
    }

    /**
     * Checks the reachability of a given host using a ping request.
     *
     * @param host The hostname or IP address to ping.
     * @param timeout The timeout in seconds for the ping request.
     * @return A [ResourceState] indicating whether the host is available or unavailable.
     */
    @Suppress("ReturnCount")
    private fun checkPing(host: String, timeout: Int): ResourceState {
        val address = try {
            InetAddress.getByName(host)
        } catch (e: Exception) {
            return ResourceState.Unavailable(error = e.message?.let { TextResource.PlainText(it) }
                ?: TextResource.DynamicText(R.string.text_error_unknown))
        }
        return try {
            if (address.isReachable(timeout * MS_IN_SECOND))
                ResourceState.Available()
            else
                ResourceState.Unavailable(error = TextResource.DynamicText(R.string.text_error_no_response))
        } catch (_: SocketTimeoutException) {
            ResourceState.Unavailable(error = TextResource.DynamicText(R.string.text_error_no_response))
        } catch (e: Exception) {
            ResourceState.Unavailable(error = e.message?.let { TextResource.PlainText(it) }
                ?: TextResource.DynamicText(R.string.text_error_unknown))
        }
    }

    /**
     * Called when the LifecycleOwner is resumed.
     *
     * @param owner The LifecycleOwner that is being resumed.
     */
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        defaultScope = CoroutineScope(defaultDispatcher + SupervisorJob())
        ioScope = CoroutineScope(ioDispatcher + SupervisorJob())
        defaultScope.launch {
            trackResources.distinctUntilChanged().flowOn(ioDispatcher).collect { resources ->
                _resourceState.update { state ->
                    resources.associate {
                        it to state.getOrElse(it) { ResourceState.Unknown() }
                    }.toPersistentMap()
                }
            }
        }
        defaultScope.launch {
            _resourceState.map { it.any() }.distinctUntilChanged().cancellable().collect {
                processJob?.cancel()
                processJob = null
                if (it) {
                    processJob = launch { startResourceCheck() }
                }
            }
        }
    }

    /**
     * Called when the associated lifecycle moves to the ON_PAUSE state.
     * Cancels the coroutine scopes used by this watcher.
     *
     * @param owner The [LifecycleOwner] whose lifecycle is changing.
     */
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        ioScope.cancel()
        defaultScope.cancel()
    }

}