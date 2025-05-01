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

package me.impa.knockonports.mock

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.dao.LogEntryDao
import me.impa.knockonports.data.db.dao.SequenceDao
import me.impa.knockonports.data.settings.DeviceState
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.di.AppModule
import me.impa.knockonports.di.DefaultDispatcher
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.di.MainDispatcher
import javax.inject.Singleton

@TestInstallIn(components = [SingletonComponent::class], replaces = [AppModule::class])
@Module
object FakeModule {
    val testDispatcher = StandardTestDispatcher()

    @Singleton
    @Provides
    fun provideFakeKnocksRepository(): KnocksRepository = FakeRepository

    @Singleton
    @Provides
    fun provideSequenceDao(): SequenceDao = FakeSequenceDao

    @Singleton
    @Provides
    fun provideLogEntryDao(): LogEntryDao = FakeLogEntryDao

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = testDispatcher

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = testDispatcher

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = testDispatcher

    @Provides
    @Singleton
    fun provideSettingsDataStore(): SettingsDataStore = FakeSettingsDataStore

    @Provides
    @Singleton
    fun provideDeviceState(): DeviceState = FakeDeviceState

}