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

package me.impa.knockonports.screen.viewmodel.state.main

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import me.impa.knockonports.data.db.entity.Sequence

data class UiState(
    val sequences: ImmutableList<Sequence> = persistentListOf(),
    val areShortcutsAvailable: Boolean = false,
    val focusedSequenceId: Long? = null,
    val disableNotificationRequest: Boolean = false,
    val detailedList: Boolean = true,
    val editMode: Boolean = false,
    val editSequenceId: Long? = null
)