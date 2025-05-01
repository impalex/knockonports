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

import kotlinx.coroutines.flow.Flow
import me.impa.knockonports.data.db.dao.LogEntryDao
import me.impa.knockonports.data.db.entity.LogEntry

object FakeLogEntryDao: LogEntryDao {
    override suspend fun insertLogEntry(logEntry: LogEntry): Long {
        TODO("Not yet implemented")
    }

    override fun logEntriesById(): Flow<List<LogEntry>> {
        TODO("Not yet implemented")
    }

    override suspend fun clearLogEntries(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun cleanupLogEntries(keepCount: Int): Int {
        TODO("Not yet implemented")
    }
}