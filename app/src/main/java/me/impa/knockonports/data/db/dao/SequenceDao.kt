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

package me.impa.knockonports.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.impa.knockonports.data.db.entity.Sequence

@Dao
interface SequenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSequence(sequence: Sequence): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSequences(sequences: List<Sequence>): List<Long>

    @Update
    suspend fun updateSequences(sequences: List<Sequence>)

    @Update
    suspend fun updateSequence(sequence: Sequence)

    @Delete
    suspend fun deleteSequence(sequence: Sequence): Int

    @Query("SELECT * FROM tbSequence ORDER BY _order")
    fun findAllSequences(): Flow<List<Sequence>>

    @Query(
        "SELECT * FROM tbSequence WHERE (_order > (SELECT _order FROM tbSequence WHERE _id=:id)) " +
            "ORDER BY _order ASC LIMIT 1"
    )
    suspend fun getNextSequence(id: Long): Sequence?

    @Query(
        "SELECT * FROM tbSequence WHERE (_order < (SELECT _order FROM tbSequence WHERE _id=:id)) " +
            "ORDER BY _order DESC LIMIT 1"
    )
    suspend fun getPrevSequence(id: Long): Sequence?

    @Query("SELECT * FROM tbSequence ORDER BY _order ASC LIMIT 1")
    suspend fun getFirstSequence(): Sequence?

    @Query("SELECT * FROM tbSequence ORDER BY _order DESC LIMIT 1")
    suspend fun getLastSequence(): Sequence?

    @Query("SELECT * FROM tbSequence WHERE _id=:id")
    suspend fun findSequenceById(id: Long): Sequence?

    @Query("SELECT MAX(_order) FROM tbSequence")
    suspend fun getMaxOrder(): Int?

    @Query("SELECT _name FROM tbSequence WHERE _id=:id")
    suspend fun getSequenceName(id: Long): String?

    @Query("DELETE FROM tbSequence WHERE _id=:id")
    suspend fun deleteSequenceById(id: Long): Int
}
