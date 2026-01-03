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

package me.impa.knockonports.data.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton event bus for application-wide communication.
 *
 * This class facilitates communication between different components of the application
 * using Kotlin Coroutines `Channel` and `Flow`. It allows components to send events
 * and other components to subscribe to and receive these events in a decoupled manner.
 *
 * Events are identified by a string key, which by default is the fully qualified name
 * of the event's class.
 *
 * Example Usage:
 *
 * Sending an event:
 * ```
 * // Define a data class for the event
 * data class MyCustomEvent(val message: String)
 *
 * // Inject AppEventBus and send the event
 * appEventBus.sendEvent(event = MyCustomEvent("Hello, World!"))
 * ```
 *
 * Receiving an event:
 * ```
 * // In a coroutine scope (e.g., in a ViewModel)
 * viewModelScope.launch {
 *     appEventBus.getEventFlow<MyCustomEvent>().collect { event ->
 *         // Handle the received event
 *         println(event.message)
 *     }
 * }
 * ```
 */
@Singleton
class AppEventBus @Inject constructor() {

    val channelMap: MutableMap<String, Channel<Any?>> = mutableMapOf()

    fun getOrCreateChannel(key: String) =
        channelMap.getOrPut(key) { Channel(capacity = BUFFERED, onBufferOverflow = BufferOverflow.SUSPEND) }

    inline fun <reified T> getEventFlow(key: String = T::class.toString()) =
        getOrCreateChannel(key).receiveAsFlow()

    inline fun <reified T> sendEvent(key: String = T::class.toString(), event: T) {
        getOrCreateChannel(key).trySend(event)
    }

    inline fun <reified T> removeEvent(key: String = T::class.toString()) {
        channelMap.remove(key)
    }

    fun unregister(key: String) {
        channelMap.remove(key)?.close()
    }

}