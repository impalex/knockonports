/*
 * Copyright (c) 2026 Alexander Yaburov
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

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import me.impa.knockonports.shared.data.SequenceList
import java.io.InputStream
import java.io.OutputStream

object SequenceSerializer : Serializer<SequenceList> {
    override val defaultValue: SequenceList
        get() = SequenceList()

    override suspend fun readFrom(input: InputStream): SequenceList {
        try {
            return SequenceList.ADAPTER.decode(input)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: SequenceList, output: OutputStream) {
        SequenceList.ADAPTER.encode(output, t)
    }

}

val Context.sequenceDataStore: DataStore<SequenceList> by dataStore(
    fileName = "sequences.pb",
    serializer = SequenceSerializer
)