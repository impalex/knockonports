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

package me.impa.knockonports.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.impa.knockonports.data.type.EventType

@Entity(tableName = "tbLog")
data class LogEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long? = null,
    @ColumnInfo(name = "_dt")
    val date: Long? = System.currentTimeMillis(),
    @ColumnInfo(name = "_event")
    val event: EventType?,
    @ColumnInfo(name = "_data")
    val data: List<String?>? = null
)
