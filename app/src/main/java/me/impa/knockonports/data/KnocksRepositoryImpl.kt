/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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

package me.impa.knockonports.data

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import me.impa.knockonports.data.db.dao.LogEntryDao
import me.impa.knockonports.data.db.dao.SequenceDao
import me.impa.knockonports.data.db.entity.CheckAccessData
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.file.FileRepository
import javax.inject.Inject

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
 * @property widgetRepository Repository for interacting with the Knocks widget (updating its state).
 */
@Suppress("TooManyFunctions")
class KnocksRepositoryImpl @Inject constructor(
    private val logEntryDao: LogEntryDao,
    private val sequenceDao: SequenceDao,
    private val fileRepository: FileRepository,
    private val widgetRepository: KnocksWidgetRepository
): KnocksRepository {

    // region Sequences
    override fun getSequences() = sequenceDao.findAllSequences().distinctUntilChanged()

    override fun getGroupList() = sequenceDao.getGroupList().distinctUntilChanged()

    override suspend fun findSequence(id: Long): Sequence? = sequenceDao.findSequenceById(id)

    override suspend fun deleteSequence(sequence: Sequence): Int =
        sequenceDao.deleteSequence(sequence).also { widgetRepository.updateWidget() }

    override suspend fun updateSequences(sequences: List<Sequence>) =
        sequenceDao.updateSequences(sequences).also { widgetRepository.updateWidget() }

    override suspend fun saveSequence(sequence: Sequence): Long = (if (sequence.id == null) {
        sequenceDao.insertSequence(sequence)
    } else {
        sequenceDao.updateSequence(sequence)
        sequence.id
    }).also { widgetRepository.updateWidget() }

    override suspend fun saveSequences(sequences: List<Sequence>) =
        sequenceDao.insertSequences(sequences).also { widgetRepository.updateWidget() }

    override suspend fun getSequenceName(id: Long) = sequenceDao.getSequenceName(id)

    override suspend fun getMaxOrder() = sequenceDao.getMaxOrder()

    override suspend fun deleteSequenceById(id: Long) =
        sequenceDao.deleteSequenceById(id).also { widgetRepository.updateWidget() }

    override fun getAccessResources(): Flow<List<CheckAccessData>> = sequenceDao.getAccessResources()

    override suspend fun getAccessResourceById(id: Long): CheckAccessData? = sequenceDao.getAccessResourceById(id)
    // endregion

    // region Log Entries
    override suspend fun saveLogEntry(logEntry: LogEntry) = logEntryDao.insertLogEntry(logEntry)

    override fun getLogEntries() = logEntryDao.logEntriesById().distinctUntilChanged()

    override suspend fun clearLogEntries() = logEntryDao.clearLogEntries()

    override suspend fun cleanupLogEntries(keepCount: Int) = logEntryDao.cleanupLogEntries(keepCount)
    // endregion

    // region Files
    override suspend fun readSequencesFromFile(uri: Uri) = fileRepository.readSequencesFromFile(uri)

    override suspend fun writeSequencesToFile(uri: Uri, sequences: List<Sequence>) =
        fileRepository.writeSequencesToFile(uri, sequences)

    override suspend fun readSequencesFromKnockdConf(uri: Uri) = fileRepository.readSequencesFromKnockdConf(uri)
    // endregion

}
