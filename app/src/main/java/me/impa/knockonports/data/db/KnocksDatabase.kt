/*
 * Copyright (c) 2018-2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package me.impa.knockonports.data.db

import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.impa.knockonports.data.db.converter.DataConverters
import me.impa.knockonports.data.db.dao.LogEntryDao
import me.impa.knockonports.data.db.dao.SequenceDao
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence

@Database(
    entities = [Sequence::class, LogEntry::class],
    version = 20
)
@TypeConverters(DataConverters::class)
abstract class KnocksDatabase : RoomDatabase() {
    abstract fun sequenceDao(): SequenceDao
    abstract fun logEntryDao(): LogEntryDao

    companion object {
        const val DB_NAME = "knocksdb"
    }
}
