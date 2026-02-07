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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.service.sequence.KnockState
import javax.inject.Inject

class WearDummyService @Inject constructor() : WearConnectionManager {
    override val isCompanionReady: StateFlow<Boolean>
        get() = MutableStateFlow(false)

    override suspend fun getStatus(): WearConnectionStatus = WearConnectionStatus.NotAvailable

    override suspend fun openPlayStore() = Unit

    override suspend fun sendSequences(sequenceList: List<Sequence>) = Unit
    override suspend fun sendStatus(status: KnockState?) = Unit
}