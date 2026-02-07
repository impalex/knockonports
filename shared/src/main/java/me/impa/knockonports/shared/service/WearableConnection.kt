/*
 * Copyright (c) 2026 Alexander Yaburov
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

package me.impa.knockonports.shared.service

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class WearableConnection(
    context: Context,
    private val capability: String
) {

    private val appContext = context.applicationContext

    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }
    private val nodeClient by lazy { Wearable.getNodeClient(context) }
    private val remoteActivityHelper by lazy { RemoteActivityHelper(context) }

    val companionReady: StateFlow<Boolean> = channelFlow {
        // Initial state
        try {
            capabilityClient.getCapability(capability, CapabilityClient.FILTER_ALL).await()
        } catch (_: Exception) {
            null
        }.also {
            trySend(it?.nodes?.isNotEmpty() == true)
        }

        val listener = CapabilityClient.OnCapabilityChangedListener { capInfo ->
            val nodes = capInfo.nodes
            trySend(nodes.isNotEmpty())
        }
        capabilityClient.addListener(listener, capability)
        awaitClose {
            capabilityClient.removeListener(listener)
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed(5_000), true)

    @Suppress("ReturnCount")
    suspend fun checkStatus(): ConnectionStatus {
        val gmsResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appContext)
        if (gmsResult != ConnectionResult.SUCCESS)
            return ConnectionStatus.NoPlayServices

        val nodes = try {
            nodeClient.connectedNodes.await()
        } catch (_: Exception) {
            null
        }

        if (nodes?.isEmpty() ?: true)
            return ConnectionStatus.NoNodesConnected

        val capabilityInfo = try {
            capabilityClient.getCapability(capability, CapabilityClient.FILTER_ALL)
                .await()
        } catch (_: Exception) {
            null
        }

        return when (capabilityInfo?.nodes?.isNotEmpty()) {
            true -> ConnectionStatus.Ready
            else -> ConnectionStatus.AppNotInstalled
        }
    }

    fun openPlayStore() {
        openRemoteUri("market://details?id=${appContext.packageName}")
    }

    fun openRemoteUri(uri: String) {
        Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(uri.toUri())
            .also {
                remoteActivityHelper.startRemoteActivity(it)
            }
    }
}