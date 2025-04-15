/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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
import javax.inject.Singleton

/**
 * Repository class responsible for managing data related to knocks and sequences.
 * This class acts as an intermediary between the data sources (database, files, settings) and the
 * application logic (ViewModels, UseCases).  It handles operations for sequences, log entries, application settings,
 * and interactions with external resources like files and the widget.
 *
 * @property logEntryDao DAO for accessing and managing log entry data in the database.
 * @property sequenceDao DAO for accessing and managing sequence data in the database.
 * @property fileRepository Repository for handling file-related operations, specifically reading and
 *           writing sequences to/from files.
 * @property eventHolder Holds and manages shared events within the application.
 * @property settingsRepository Repository for managing application and theme settings.
 * @property widgetRepository Repository for interacting with the Knocks widget (updating its state).
 */
@Suppress("TooManyFunctions")
@Singleton
class KnocksRepository @Inject constructor(
    private val logEntryDao: LogEntryDao,
    private val sequenceDao: SequenceDao,
    private val fileRepository: FileRepository,
    private val eventHolder: SharedEventHolder,
    private val settingsRepository: SettingsRepository,
    private val widgetRepository: KnocksWidgetRepository
) {

    // region Sequences
    fun getSequences() = sequenceDao.findAllSequences()

    suspend fun findSequence(id: Long): Sequence? = sequenceDao.findSequenceById(id)

    suspend fun deleteSequence(sequence: Sequence): Int =
        sequenceDao.deleteSequence(sequence).also { widgetRepository.updateWidget() }

    suspend fun updateSequences(sequences: List<Sequence>) =
        sequenceDao.updateSequences(sequences).also { widgetRepository.updateWidget() }

    suspend fun saveSequence(sequence: Sequence): Long = (if (sequence.id == null) {
        sequenceDao.insertSequence(sequence)
    } else {
        sequenceDao.updateSequence(sequence)
        sequence.id
    }).also { widgetRepository.updateWidget() }


    suspend fun saveSequences(sequences: List<Sequence>) =
        sequenceDao.insertSequences(sequences).also { widgetRepository.updateWidget() }

    suspend fun getSequenceName(id: Long) = sequenceDao.getSequenceName(id)

    suspend fun getMaxOrder() = sequenceDao.getMaxOrder()

    // endregion

    // region Log Entries
    suspend fun saveLogEntry(logEntry: LogEntry) = logEntryDao.insertLogEntry(logEntry)

    fun getLogEntries() = logEntryDao.logEntriesById()

    suspend fun clearLogEntries() = logEntryDao.clearLogEntries()

    suspend fun cleanupLogEntries(keepCount: Int) = logEntryDao.cleanupLogEntries(keepCount)
    // endregion

    // region Files
    suspend fun readSequencesFromFile(uri: Uri) = fileRepository.readSequencesFromFile(uri)

    suspend fun writeSequencesToFile(uri: Uri, sequences: List<Sequence>) =
        fileRepository.writeSequencesToFile(uri, sequences)
    // endregion

    // region Events
    fun getCurrentEventFlow() = eventHolder.currentEventFlow

    fun sendEvent(appEvent: AppEvent) = eventHolder.sendEvent(appEvent)

    fun clearEvent() = eventHolder.clearEvent()
    // endregion

    // region "Settings"
    fun getAppSettings() = settingsRepository.appSettings

    fun updateAppSettings(newSettings: AppSettings) = settingsRepository.updateAppSettings(newSettings)

    fun getThemeSettings() = settingsRepository.themeSettings

    fun updateThemeSettings(newSettings: ThemeConfig) = settingsRepository.updateThemeSettings(newSettings)

    fun incrementKnockCount() = settingsRepository.incrementKnockCount()

    fun getKnockCount() = settingsRepository.knockCount

    fun doNotAskAboutNotifications() = settingsRepository.doNotAskAboutNotifications

    fun setDoNotAskAboutNotificationsFlag() = settingsRepository.setDoNotAskAboutNotificationsFlag()

    fun getFirstLaunchV2() = settingsRepository.firstLaunchV2
    fun getAskReviewTime() = settingsRepository.askReviewTime
    fun getDoNotAskForReview() = settingsRepository.doNotAskForReview

    fun postponeReviewRequest(time: Long) = settingsRepository.postponeReviewRequest(time)
    fun doNotAskForReview() = settingsRepository.doNotAskForReview()
    fun isInstalledFromPlayStore() = settingsRepository.isInstalledFromPlayStore
    fun clearFirstLaunchV2() = settingsRepository.clearFirstLaunchV2()
    // endregion

}
