/*
 * Copyright (c) 2018 Alexander Yaburov
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

package me.impa.knockonports.database

import androidx.lifecycle.LiveData
import android.content.Context
import me.impa.knockonports.database.entity.LogEntry
import me.impa.knockonports.database.entity.Sequence

class KnocksRepository(context: Context) {

    private val db by lazy { KnocksDatabase.getInstance(context)!! }
    private val sequenceDao by lazy { db.sequenceDao() }
    private val logEntryDao by lazy { db.logEntryDao() }

    fun getSequences(): LiveData<List<Sequence>> = sequenceDao.findAllSequences()

    suspend fun findSequence(id: Long): Sequence? = sequenceDao.findSequenceById(id)

    suspend fun deleteSequence(sequence: Sequence): Int = sequenceDao.deleteSequence(sequence)

    suspend fun updateSequences(sequences: List<Sequence>) = sequenceDao.updateSequences(sequences)

    suspend fun saveSequence(sequence: Sequence) {
        if (sequence.id == null) {
            sequence.id = sequenceDao.insertSequence(sequence)
        } else {
            sequenceDao.updateSequence(sequence)
        }
    }

    suspend fun saveLogEntry(logEntry: LogEntry) = logEntryDao.insertLogEntry(logEntry)

    fun getLogEntries() = logEntryDao.logEntriesById()

    suspend fun clearLogEntries() = logEntryDao.clearLogEntries()

}