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

package me.impa.knockonports.service

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.data.KnockletRepository
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.shared.data.KnockStatus
import me.impa.knockonports.shared.data.SequenceList
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WearDataListenerService : WearableListenerService() {

    @IoDispatcher
    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var repository: KnockletRepository

    val serviceScope by lazy { CoroutineScope(ioDispatcher + SupervisorJob()) }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val bytes = event.dataItem.data ?: return
                when (event.dataItem.uri.path) {
                    BuildConfig.WEAR_PATH_SEQUENCE_LIST -> handleSequenceList(bytes)
                    BuildConfig.WEAR_PATH_KNOCK_STATUS -> handleKnockStatus(bytes)
                }
            }
        }
    }

    private fun handleKnockStatus(bytes: ByteArray) {
        try {
            val status = KnockStatus.ADAPTER.decode(bytes)
            serviceScope.launch {
                repository.knockStatus.updateData { status }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun handleSequenceList(bytes: ByteArray) {
        try {
            val list = SequenceList.ADAPTER.decode(bytes)
            serviceScope.launch {
                repository.sequences.updateData { list }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

}