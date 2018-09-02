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

package me.impa.knockonports.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import me.impa.knockonports.database.entity.Sequence

@Dao
interface SequenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSequence(sequence: Sequence): Long

    @Update
    fun updateSequences(sequences: List<Sequence>)

    @Update
    fun updateSequence(sequence: Sequence): Int

    @Delete
    fun deleteSequence(sequence: Sequence): Int

    @Query("SELECT * FROM tbSequence ORDER BY _order")
    fun findAllSequences(): LiveData<List<Sequence>>

    @Query("SELECT * FROM tbSequence ORDER BY _order")
    fun getSequenceList(): List<Sequence>

    @Query("SELECT * FROM tbSequence WHERE (_order > (SELECT _order FROM tbSequence WHERE _id=:id)) ORDER BY _order ASC LIMIT 1")
    fun getNextSequence(id: Long): Sequence?

    @Query("SELECT * FROM tbSequence WHERE (_order < (SELECT _order FROM tbSequence WHERE _id=:id)) ORDER BY _order DESC LIMIT 1")
    fun getPrevSequence(id: Long): Sequence?

    @Query("SELECT * FROM tbSequence ORDER BY _order ASC LIMIT 1")
    fun getFirstSequence(): Sequence?

    @Query("SELECT * FROM tbSequence ORDER BY _order DESC LIMIT 1")
    fun getLastSequence(): Sequence?

    @Query("SELECT * FROM tbSequence WHERE _id=:id")
    fun findSequenceById(id: Long): Sequence?
}