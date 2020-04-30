/*
 * Copyright (c) 2020 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.impa.knockonports.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.impa.knockonports.database.entity.LogEntry

@Dao
interface LogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogEntry(logEntry: LogEntry): Long

    @Query("SELECT * FROM tbLog ORDER BY _id DESC")
    fun logEntriesById(): DataSource.Factory<Int, LogEntry>
}
