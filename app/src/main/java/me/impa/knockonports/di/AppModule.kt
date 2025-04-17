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

package me.impa.knockonports.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.KnocksRepositoryImpl
import me.impa.knockonports.data.KnocksWidgetRepository
import me.impa.knockonports.data.db.KnocksDatabase
import me.impa.knockonports.data.db.Migrations.Migration10To11
import me.impa.knockonports.data.db.Migrations.Migration11To12
import me.impa.knockonports.data.db.Migrations.Migration12To13
import me.impa.knockonports.data.db.Migrations.Migration13To14
import me.impa.knockonports.data.db.Migrations.Migration14To15
import me.impa.knockonports.data.db.Migrations.Migration15To16
import me.impa.knockonports.data.db.Migrations.Migration16To17
import me.impa.knockonports.data.db.Migrations.Migration17To18
import me.impa.knockonports.data.db.Migrations.Migration18To19
import me.impa.knockonports.data.db.Migrations.Migration19To20
import me.impa.knockonports.data.db.Migrations.Migration1To2
import me.impa.knockonports.data.db.Migrations.Migration2To3
import me.impa.knockonports.data.db.Migrations.Migration3To4
import me.impa.knockonports.data.db.Migrations.Migration4To5
import me.impa.knockonports.data.db.Migrations.Migration5To6
import me.impa.knockonports.data.db.Migrations.Migration6To7
import me.impa.knockonports.data.db.Migrations.Migration7To8
import me.impa.knockonports.data.db.Migrations.Migration8To9
import me.impa.knockonports.data.db.Migrations.Migration9To10
import me.impa.knockonports.data.db.dao.LogEntryDao
import me.impa.knockonports.data.db.dao.SequenceDao
import me.impa.knockonports.data.event.SharedEventHolder
import me.impa.knockonports.data.file.FileRepository
import me.impa.knockonports.data.settings.SettingsRepository
import me.impa.knockonports.data.settings.SettingsRepositoryImpl
import me.impa.knockonports.util.ShortcutManagerWrapper
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention
@Qualifier
annotation class MainDispatcher

@Suppress("TooManyFunctions", "Unused")
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideKnocksDatabase(@ApplicationContext context: Context): KnocksDatabase {
        return Room.databaseBuilder(
            context,
            KnocksDatabase::class.java,
            KnocksDatabase.DB_NAME
        )
            .addMigrations(
                Migration1To2(),
                Migration2To3(),
                Migration3To4(),
                Migration4To5(),
                Migration5To6(),
                Migration6To7(context),
                Migration7To8(),
                Migration8To9(),
                Migration9To10(),
                Migration10To11(),
                Migration11To12(),
                Migration12To13(),
                Migration13To14(),
                Migration14To15(),
                Migration15To16(),
                Migration16To17(),
                Migration17To18(),
                Migration18To19(),
                Migration19To20()
            )
            .build()
    }

    @Suppress("LongParameterList")
    @Provides
    @Singleton
    fun provideKnocksRepository(
        logEntryDao: LogEntryDao,
        sequenceDao: SequenceDao,
        fileRepository: FileRepository,
        eventHolder: SharedEventHolder,
        settingsRepository: SettingsRepository,
        widgetRepository: KnocksWidgetRepository
    ): KnocksRepository {
        return KnocksRepositoryImpl(
            logEntryDao = logEntryDao,
            sequenceDao = sequenceDao,
            fileRepository = fileRepository,
            eventHolder = eventHolder,
            settingsRepository = settingsRepository,
            widgetRepository = widgetRepository
        )
    }

    @Provides
    @Singleton
    fun provideLogEntryDao(knocksDatabase: KnocksDatabase): LogEntryDao {
        return knocksDatabase.logEntryDao()
    }

    @Provides
    @Singleton
    fun provideSequenceDao(knocksDatabase: KnocksDatabase): SequenceDao {
        return knocksDatabase.sequenceDao()
    }

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("me.impa.knockonports_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        sharedPreferences: SharedPreferences,
        packageManager: PackageManager
    ): SettingsRepository = SettingsRepositoryImpl(sharedPreferences, packageManager)

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    fun provideShortcutManagerWrapper(@ApplicationContext context: Context): ShortcutManagerWrapper =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManagerWrapper.Available(context.getSystemService(ShortcutManager::class.java))
        } else {
            ShortcutManagerWrapper.Unavailable
        }

    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager
}
