/*
 * Copyright (c) 2018-2025 Alexander Yaburov
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

package me.impa.knockonports.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.impa.knockonports.data.db.entity.LogEntry

@Dao
interface LogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogEntry(logEntry: LogEntry): Long

    @Query("SELECT * FROM tbLog ORDER BY _id DESC")
    fun logEntriesById(): Flow<List<LogEntry>>

    @Query("DELETE FROM tbLog")
    suspend fun clearLogEntries(): Int

    @Query("DELETE FROM tbLog WHERE _id NOT IN (SELECT _id FROM tbLog ORDER BY _id DESC LIMIT :keepCount)")
    suspend fun cleanupLogEntries(keepCount: Int): Int
}
