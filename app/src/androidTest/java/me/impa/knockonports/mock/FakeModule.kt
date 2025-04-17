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

import dagger.Module
import dagger.Provides
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.dao.LogEntryDao
import me.impa.knockonports.data.db.dao.SequenceDao
import me.impa.knockonports.data.settings.SettingsRepository
import me.impa.knockonports.di.AppModule
import me.impa.knockonports.di.DefaultDispatcher
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.di.MainDispatcher
import me.impa.knockonports.util.ShortcutManagerWrapper
import javax.inject.Singleton

@TestInstallIn(components = [SingletonComponent::class], replaces = [AppModule::class])
@Module
object FakeModule {
    private val testDispatcher = StandardTestDispatcher()

    @Singleton
    @Provides
    fun provideFakeKnocksRepository(): KnocksRepository = FakeRepository

    @Singleton
    @Provides
    fun provideSequenceDao(): SequenceDao = FakeSequenceDao

    @Singleton
    @Provides
    fun provideLogEntryDao(): LogEntryDao = FakeLogEntryDao

    @Singleton
    @Provides
    fun provideSettingsRepository(): SettingsRepository = FakeSettingsRepository

    @Singleton
    @Provides
    fun provideShortcutManagerWrapper(): ShortcutManagerWrapper = ShortcutManagerWrapper.Unavailable

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = testDispatcher

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = testDispatcher

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = testDispatcher

}