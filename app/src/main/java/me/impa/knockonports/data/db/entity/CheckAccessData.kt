/*
 * Copyright (c) 2025 Alexander Yaburov
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

import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import me.impa.knockonports.data.type.CheckAccessType

@Stable
data class CheckAccessData(
    @ColumnInfo(name = "_id")
    val id: Long?,
    @ColumnInfo(name = "_check_access")
    val checkAccess: Boolean,
    @ColumnInfo(name = "_check_type")
    val checkType: CheckAccessType,
    @ColumnInfo(name = "_check_port")
    val checkPort: Int?,
    @ColumnInfo(name = "_check_host")
    val checkHost: String?,
    @ColumnInfo(name = "_check_timeout")
    val checkTimeout: Int,
    @ColumnInfo(name = "_check_post_knock")
    val checkPostKnock: Boolean,
    @ColumnInfo(name = "_check_retries")
    val checkRetries: Int
)
