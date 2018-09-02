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

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import me.impa.knockonports.database.entity.Port

@Dao
interface PortDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPorts(ports: List<Port>): List<Long>

    @Query("DELETE FROM tbPort WHERE _sequence_id=:sequenceId")
    fun deletePortsBySequenceId(sequenceId: Long): Int

    @Query("SELECT * FROM tbPort WHERE _sequence_id=:sequenceId ORDER BY _id")
    fun findPortsBySequenceId(sequenceId: Long): List<Port>
}