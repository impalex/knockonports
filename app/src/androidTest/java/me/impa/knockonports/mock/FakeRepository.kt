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

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.event.AppEvent
import me.impa.knockonports.data.settings.AppSettings
import me.impa.knockonports.ui.config.ThemeConfig

object FakeRepository: KnocksRepository {
    override fun getSequences(): Flow<List<Sequence>> {
        return flowOf(fakeSequenceList)
    }

    override suspend fun findSequence(id: Long): Sequence? {
        return fakeSequenceList.firstOrNull { it.id == id }
    }

    override suspend fun deleteSequence(sequence: Sequence): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateSequences(sequences: List<Sequence>) {
        TODO("Not yet implemented")
    }

    override suspend fun saveSequence(sequence: Sequence): Long {
        TODO("Not yet implemented")
    }

    override suspend fun saveSequences(sequences: List<Sequence>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getSequenceName(id: Long): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getMaxOrder(): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun saveLogEntry(logEntry: LogEntry): Long {
        TODO("Not yet implemented")
    }

    override fun getLogEntries(): Flow<List<LogEntry>> {
        TODO("Not yet implemented")
    }

    override suspend fun clearLogEntries(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun cleanupLogEntries(keepCount: Int): Int = 0

    override suspend fun readSequencesFromFile(uri: Uri): List<Sequence> {
        TODO("Not yet implemented")
    }

    override suspend fun writeSequencesToFile(
        uri: Uri,
        sequences: List<Sequence>
    ) {
        TODO("Not yet implemented")
    }

    override fun getCurrentEventFlow(): StateFlow<AppEvent?> = MutableStateFlow(null)

    override fun sendEvent(appEvent: AppEvent) {
        TODO("Not yet implemented")
    }

    override fun clearEvent() {
        TODO("Not yet implemented")
    }

    override fun getAppSettings(): StateFlow<AppSettings> = MutableStateFlow(AppSettings())

    override fun updateAppSettings(newSettings: AppSettings) {
        TODO("Not yet implemented")
    }

    override fun getThemeSettings(): StateFlow<ThemeConfig> {
        return MutableStateFlow(ThemeConfig(
            useDynamicColors = false
        ))
    }

    override fun updateThemeSettings(newSettings: ThemeConfig) {
        TODO("Not yet implemented")
    }

    override fun incrementKnockCount() {
        TODO("Not yet implemented")
    }

    override fun getKnockCount(): State<Long> = mutableStateOf(100)

    override fun doNotAskAboutNotifications(): State<Boolean> = mutableStateOf(true)

    override fun setDoNotAskAboutNotificationsFlag() {
        TODO("Not yet implemented")

    }

    override fun getFirstLaunchV2(): State<Boolean> = mutableStateOf(false)

    override fun getAskReviewTime(): State<Long> = mutableStateOf(0)

    override fun getDoNotAskForReview(): State<Boolean> = mutableStateOf(true)

    override fun postponeReviewRequest(time: Long) {
        TODO("Not yet implemented")
    }

    override fun doNotAskForReview() {
        TODO("Not yet implemented")
    }

    override fun isInstalledFromPlayStore(): State<Boolean> = mutableStateOf(true)

    override fun clearFirstLaunchV2() {
        TODO("Not yet implemented")
    }
}