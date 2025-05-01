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

package me.impa.knockonports.data

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.impa.knockonports.data.db.entity.CheckAccessData
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.event.AppEvent

@Suppress("TooManyFunctions")
interface KnocksRepository {
    fun getSequences(): Flow<List<Sequence>>
    suspend fun findSequence(id: Long): Sequence?
    suspend fun deleteSequence(sequence: Sequence): Int
    suspend fun updateSequences(sequences: List<Sequence>)
    suspend fun saveSequence(sequence: Sequence): Long
    suspend fun saveSequences(sequences: List<Sequence>): List<Long>
    suspend fun getSequenceName(id: Long): String?
    suspend fun deleteSequenceById(id: Long): Int
    suspend fun getMaxOrder(): Int?
    fun getGroupList(): Flow<List<String>>
    fun getAccessResources(): Flow<List<CheckAccessData>>
    suspend fun getAccessResourceById(id: Long): CheckAccessData?
    suspend fun saveLogEntry(logEntry: LogEntry): Long
    fun getLogEntries(): Flow<List<LogEntry>>
    suspend fun clearLogEntries(): Int
    suspend fun cleanupLogEntries(keepCount: Int): Int
    suspend fun readSequencesFromFile(uri: Uri): List<Sequence>
    suspend fun writeSequencesToFile(uri: Uri, sequences: List<Sequence>)
    fun getCurrentEventFlow(): StateFlow<AppEvent?>
    fun sendEvent(appEvent: AppEvent)
    fun clearEvent()
}