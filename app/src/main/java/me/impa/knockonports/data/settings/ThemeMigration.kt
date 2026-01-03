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

package me.impa.knockonports.data.settings

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import me.impa.knockonports.data.settings.SettingsDataStoreImpl.Companion.customThemeKey
import me.impa.knockonports.data.settings.SettingsDataStoreImpl.Companion.themeSeedKey
import me.impa.knockonports.ui.config.defaultThemes

val themeMigration = object : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        return currentData.contains(customThemeKey)
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val newPreferences = currentData.toMutablePreferences()

        val oldTheme = currentData[customThemeKey]

        defaultThemes[oldTheme]?.let {
            newPreferences[themeSeedKey] = it
        }
        newPreferences.remove(customThemeKey)
        return newPreferences
    }

    override suspend fun cleanUp() = Unit

}