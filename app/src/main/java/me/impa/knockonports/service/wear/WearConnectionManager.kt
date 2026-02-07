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

import kotlinx.coroutines.flow.StateFlow
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.service.sequence.KnockState

interface WearConnectionManager {
    val isCompanionReady: StateFlow<Boolean>
    suspend fun getStatus(): WearConnectionStatus
    suspend fun openPlayStore()
    suspend fun sendSequences(sequenceList: List<Sequence>)
    suspend fun sendStatus(status: KnockState?)
}