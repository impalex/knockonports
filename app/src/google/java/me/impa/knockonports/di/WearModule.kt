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

package me.impa.knockonports.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.impa.knockonports.service.wear.WearConnectionManager
import me.impa.knockonports.service.wear.WearableManager
import javax.inject.Singleton

@Suppress("Unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class WearModule {

    @Binds
    @Singleton
    abstract fun bindWearConnectionManager(wearConnectionManager: WearableManager): WearConnectionManager

}