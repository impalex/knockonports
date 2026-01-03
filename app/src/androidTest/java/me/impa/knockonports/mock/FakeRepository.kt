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

package me.impa.knockonports.mock

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.CheckAccessData
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence

object FakeRepository: KnocksRepository {
    override fun getSequences(): Flow<List<Sequence>> {
        return flowOf(fakeSequenceList)
    }

    override suspend fun findSequence(id: Long): Sequence? {
        return fakeSequenceList.firstOrNull { it.id == id }
    }

    override suspend fun deleteSequence(sequence: Sequence): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateSequences(sequences: List<Sequence>) {
        TODO("Not yet implemented")
    }

    override suspend fun saveSequence(sequence: Sequence): Long {
        TODO("Not yet implemented")
    }

    override suspend fun saveSequences(sequences: List<Sequence>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getSequenceName(id: Long): String? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSequenceById(id: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getMaxOrder(): Int? {
        TODO("Not yet implemented")
    }

    override fun getGroupList(): Flow<List<String>> {
        return flowOf(listOf("Group 1"))
    }

    override fun getAccessResources(): Flow<List<CheckAccessData>> {
        return flowOf(emptyList())
    }

    override suspend fun getAccessResourceById(id: Long): CheckAccessData? {
        TODO("Not yet implemented")
    }

    override suspend fun saveLogEntry(logEntry: LogEntry): Long {
        TODO("Not yet implemented")
    }

    override fun getLogEntries(): Flow<List<LogEntry>> {
        TODO("Not yet implemented")
    }

    override suspend fun clearLogEntries(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun cleanupLogEntries(keepCount: Int): Int = 0

    override suspend fun readSequencesFromFile(uri: Uri): List<Sequence> {
        TODO("Not yet implemented")
    }

    override suspend fun writeSequencesToFile(
        uri: Uri,
        sequences: List<Sequence>
    ) {
        TODO("Not yet implemented")
    }

}