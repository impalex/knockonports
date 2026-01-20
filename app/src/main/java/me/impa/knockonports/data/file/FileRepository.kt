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

package me.impa.knockonports.data.file

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.data.json.SequencesData
import me.impa.knockonports.di.IoDispatcher
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
class FileRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    private val jsonCoder = Json { ignoreUnknownKeys = true }

    private suspend fun readFileContent(uri: Uri): String? = withContext(dispatcher) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.reader().readText()
        }
    }

    suspend fun readSequencesFromKnockdConf(uri: Uri): List<Sequence> = try {
        KnockdConfDecoder.decode(readFileContent(uri) ?: "")
    } catch (e: Exception) {
        Timber.e(e)
        throw e
    }


    suspend fun readSequencesFromFile(uri: Uri): List<Sequence> = try {
        val list = jsonCoder.decodeFromString<SequencesData>(readFileContent(uri) ?: "")
        list.asSequenceList()
    } catch (e: Exception) {
        Timber.e(e)
        throw e
    }


    suspend fun writeSequencesToFile(uri: Uri, sequences: List<Sequence>) {
        withContext(dispatcher) {
            val data = sequences.asJsonData()
            val jsonString = jsonCoder.encodeToString(SequencesData.serializer(), data)
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
}