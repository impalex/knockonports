/*
 * Copyright (c) 2025 Alexander Yaburov
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

package me.impa.knockonports.mock

import kotlinx.coroutines.flow.Flow
import me.impa.knockonports.data.db.dao.SequenceDao
import me.impa.knockonports.data.db.entity.Sequence

object FakeSequenceDao: SequenceDao {
    override suspend fun insertSequence(sequence: Sequence): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertSequences(sequences: List<Sequence>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSequences(sequences: List<Sequence>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSequence(sequence: Sequence) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSequence(sequence: Sequence): Int {
        TODO("Not yet implemented")
    }

    override fun findAllSequences(): Flow<List<Sequence>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNextSequence(id: Long): Sequence? {
        TODO("Not yet implemented")
    }

    override suspend fun getPrevSequence(id: Long): Sequence? {
        TODO("Not yet implemented")
    }

    override suspend fun getFirstSequence(): Sequence? {
        TODO("Not yet implemented")
    }

    override suspend fun getLastSequence(): Sequence? {
        TODO("Not yet implemented")
    }

    override suspend fun findSequenceById(id: Long): Sequence? {
        TODO("Not yet implemented")
    }

    override suspend fun getMaxOrder(): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun getSequenceName(id: Long): String? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSequenceById(id: Long): Int {
        TODO("Not yet implemented")
    }
}