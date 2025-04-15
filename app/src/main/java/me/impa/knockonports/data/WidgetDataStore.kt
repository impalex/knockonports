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

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.impa.knockonports.data.db.entity.Sequence
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetDataStore @Inject constructor(
    private val widgetRepository: KnocksWidgetRepository
) : DataStore<List<Sequence>> {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetDataStoreEntryPoint {
        fun widgetDataStore(): WidgetDataStore
    }

    override val data: Flow<List<Sequence>>
        get() = widgetRepository.getSequences()

    override suspend fun updateData(transform: suspend (List<Sequence>) -> List<Sequence>): List<Sequence> {
        throw NotImplementedError()
    }

    companion object {
        fun get(applicationContext: Context): WidgetDataStore {
            val widgetRepositoryEntryPoint: WidgetDataStoreEntryPoint =
                EntryPoints.get(applicationContext, WidgetDataStoreEntryPoint::class.java)
            return widgetRepositoryEntryPoint.widgetDataStore()
        }
    }


}