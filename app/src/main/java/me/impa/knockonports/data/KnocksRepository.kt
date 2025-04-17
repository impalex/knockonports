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

package me.impa.knockonports.data

import android.net.Uri
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.impa.knockonports.data.db.dao.LogEntryDao
import me.impa.knockonports.data.db.dao.SequenceDao
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.data.event.SharedEventHolder
import me.impa.knockonports.data.file.FileRepository
import me.impa.knockonports.data.settings.AppSettings
import me.impa.knockonports.data.settings.SettingsRepository
import me.impa.knockonports.ui.config.ThemeConfig
import javax.inject.Inject

@Suppress("TooManyFunctions")
interface KnocksRepository {
    fun getSequences(): Flow<List<Sequence>>
    suspend fun findSequence(id: Long): Sequence?
    suspend fun deleteSequence(sequence: Sequence): Int
    suspend fun updateSequences(sequences: List<Sequence>)
    suspend fun saveSequence(sequence: Sequence): Long
    suspend fun saveSequences(sequences: List<Sequence>): List<Long>
    suspend fun getSequenceName(id: Long): String?
    suspend fun getMaxOrder(): Int?
    suspend fun saveLogEntry(logEntry: LogEntry): Long
    fun getLogEntries(): Flow<List<LogEntry>>
    suspend fun clearLogEntries(): Int
    suspend fun cleanupLogEntries(keepCount: Int): Int
    suspend fun readSequencesFromFile(uri: Uri): List<Sequence>
    suspend fun writeSequencesToFile(uri: Uri, sequences: List<Sequence>)
    fun getCurrentEventFlow(): StateFlow<AppEvent?>
    fun sendEvent(appEvent: AppEvent)
    fun clearEvent()
    fun getAppSettings(): StateFlow<AppSettings>
    fun updateAppSettings(newSettings: AppSettings)
    fun getThemeSettings(): StateFlow<ThemeConfig>
    fun updateThemeSettings(newSettings: ThemeConfig)
    fun incrementKnockCount()
    fun getKnockCount(): State<Long>
    fun doNotAskAboutNotifications(): State<Boolean>
    fun setDoNotAskAboutNotificationsFlag()
    fun getFirstLaunchV2(): State<Boolean>
    fun getAskReviewTime(): State<Long>
    fun getDoNotAskForReview(): State<Boolean>
    fun postponeReviewRequest(time: Long)
    fun doNotAskForReview()
    fun isInstalledFromPlayStore(): State<Boolean>
    fun clearFirstLaunchV2()
}