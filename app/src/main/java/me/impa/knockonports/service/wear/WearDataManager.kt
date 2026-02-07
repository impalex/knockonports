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

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.di.DefaultDispatcher
import me.impa.knockonports.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearDataManager @Inject constructor(
    private val repository: KnocksRepository,
    private val wearConnectionManager: WearConnectionManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : DefaultLifecycleObserver {

    private var ioScope: CoroutineScope? = null
    private val defaultScope = CoroutineScope(defaultDispatcher + SupervisorJob())

    private suspend fun initWearDataTransfer() {
        if (wearConnectionManager.getStatus() == WearConnectionStatus.NotAvailable)
            return

        ioScope?.cancel()
        ioScope = CoroutineScope(ioDispatcher + SupervisorJob()).apply {
            combine(repository.getSequences(), wearConnectionManager.isCompanionReady) { sequences, isReady ->
                if (isReady) {
                    sequences
                } else null
            }.filterNotNull().onEach { sequences ->
                wearConnectionManager.sendSequences(sequences)
            }.launchIn(this)
        }

    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        defaultScope.launch { initWearDataTransfer() }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        ioScope?.cancel()
        ioScope = null
    }
}