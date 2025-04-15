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

package me.impa.knockonports.data.file

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.json.LegacySequencesData
import me.impa.knockonports.data.json.SequencesData
import me.impa.knockonports.data.json.SequencesDataV1
import me.impa.knockonports.extension.asJsonData
import me.impa.knockonports.extension.asSequenceList
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for reading and writing sequence data to and from files.
 *
 * This class utilizes the ContentResolver to interact with files identified by their URIs.
 * It handles serialization and deserialization of sequence data to/from JSON format.
 *
 * @property contentResolver The ContentResolver used to access files.  Injected via Hilt.
 */
@Singleton
class FileRepository @Inject constructor(private val contentResolver: ContentResolver) {

    private val jsonCoder = Json { ignoreUnknownKeys = true }

    suspend fun readSequencesFromFile(uri: Uri): List<Sequence> = withContext(Dispatchers.IO) {

        val fileContent = try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.reader().readText()
            }
        } catch (e: Exception) {
            Timber.e(e)
            null
        }

        return@withContext try {
            val list = jsonCoder.decodeFromString<SequencesData>(fileContent ?: "")
            Timber.d("SequencesData: $list")
            list.asSequenceList()
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }

    suspend fun writeSequencesToFile(uri: Uri, sequences: List<Sequence>) = withContext(Dispatchers.IO) {
        val data = sequences.asJsonData()
        val jsonString = jsonCoder.encodeToString(SequencesData.serializer(), data)
        Timber.d("SequencesData: $jsonString")
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }
}