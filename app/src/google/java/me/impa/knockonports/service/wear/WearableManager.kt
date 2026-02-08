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

package me.impa.knockonports.service.wear

import android.content.Context
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.service.sequence.KnockState
import me.impa.knockonports.shared.data.KnockStatus
import me.impa.knockonports.shared.data.SequenceInfo
import me.impa.knockonports.shared.data.SequenceList
import me.impa.knockonports.shared.service.ConnectionStatus
import me.impa.knockonports.shared.service.WearableConnection
import javax.inject.Inject

class WearableManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WearConnectionManager {

    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    private val wearableConnection by lazy { WearableConnection(context, BuildConfig.CAP_KNOCKLET_INSTALLED) }

    override val isCompanionReady: StateFlow<Boolean>
        get() = wearableConnection.companionReady

    override suspend fun getStatus(): WearConnectionStatus =
        wearableConnection.checkStatus().toDomain()

    override suspend fun openPlayStore() {
        scope.launch {
            wearableConnection.openPlayStore()
        }
    }

    override suspend fun sendSequences(sequenceList: List<Sequence>) {
        if (!wearableConnection.isWearAvailable.await())
            return
        val list = SequenceList(
            items = sequenceList.map {
                SequenceInfo(
                    id = it.id ?: 0,
                    name = it.name ?: ""
                )
            }
        )
        val dataBytes = list.encode()
        val request = PutDataRequest.create(BuildConfig.WEAR_PATH_SEQUENCE_LIST).apply {
            data = dataBytes
            setUrgent()
        }
        Wearable.getDataClient(context).putDataItem(request).await()
    }

    override suspend fun sendStatus(status: KnockState?) {
        if (!wearableConnection.isWearAvailable.await())
            return
        val knockData = status.toWearData()
        val dataBytes = knockData.encode()
        val request = PutDataRequest.create(BuildConfig.WEAR_PATH_KNOCK_STATUS).apply {
            data = dataBytes
            setUrgent()
        }
        Wearable.getDataClient(context).putDataItem(request).await()
    }
}

fun ConnectionStatus.toDomain() = when (this) {
    ConnectionStatus.AppNotInstalled -> WearConnectionStatus.AppNotInstalled
    ConnectionStatus.Checking -> WearConnectionStatus.Checking
    ConnectionStatus.NoNodesConnected -> WearConnectionStatus.NotAvailable
    ConnectionStatus.NoPlayServices -> WearConnectionStatus.NotAvailable
    ConnectionStatus.Ready -> WearConnectionStatus.Ready
}

fun KnockState?.toWearData() = if (this == null)
    KnockStatus(is_active = false)
else
    KnockStatus(
        is_active = true,
        id = id,
        sequence_name = sequenceName,
        attempt = attempt,
        max_attempts = maxAttempts,
        step = step,
        max_steps = maxSteps,
        is_waiting_for_resource = isWaitingForResource
    )