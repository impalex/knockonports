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

package me.impa.knockonports.database.entity

import android.arch.persistence.room.*

@Entity(
        tableName = "tbPort",
        indices = [Index(
                value = ["_sequence_id"],
                name = "idx_sequence_id"
        )],
        foreignKeys = [ForeignKey(
                entity = Sequence::class,
                parentColumns = arrayOf("_id"),
                childColumns = arrayOf("_sequence_id"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )]
)
class Port(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long?,
        @ColumnInfo(name = "_sequence_id")
        var sequenceId: Long,
        @ColumnInfo(name = "_number")
        var number: Int?,
        @ColumnInfo(name = "_type")
        var type: Int
) {

    override fun toString(): String {
        number ?: return ""
        return "$number:${if (type == PORT_TYPE_TCP) {
            "TCP"
        } else {
            "UDP"
        }}"
    }

    companion object {
        const val PORT_TYPE_UDP = 0
        const val PORT_TYPE_TCP = 1
    }
}
