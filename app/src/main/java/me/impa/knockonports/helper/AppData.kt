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

package me.impa.knockonports.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import me.impa.knockonports.R

/**
 * Data class representing an application.
 *
 * @property app The package name of the application.  Empty string represents "None".
 * @property name The user-visible name of the application.
 */
data class AppData(val app: String, val name: String) {

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        return this === other || (other is AppData && other.app == app && other.name == name)
    }

    override fun hashCode(): Int {
        var result = app.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object {
        /**
         * Loads a list of installed applications on the device.
         *
         * This function retrieves the list of installed applications, filters out those without a launch intent
         * (meaning they are not directly runnable by the user), and maps the remaining applications to [AppData]
         * objects containing the package name and application label.  The list is then sorted alphabetically
         * by application name and prepended with an entry representing "None", denoted by a blank package name
         * and the string resource `R.string.list_app_none`.
         *
         * @param context The application context.
         * @return A list of `AppData` objects representing installed applications, including an initial "None" entry.
         */
        @SuppressLint("QueryPermissionsNeeded")
        fun loadInstalledApps(context: Context): List<AppData> =
            sequenceOf(AppData("", context.resources.getString(R.string.list_app_none)))
                .plus(
                    context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    .asSequence()
                    .filter { context.packageManager.getLaunchIntentForPackage(it.packageName) != null }
                    .map { AppData(it.packageName, context.packageManager.getApplicationLabel(it).toString()) }
                    .sortedBy { it.name }
                    .toList())
                .toList()


    }
}