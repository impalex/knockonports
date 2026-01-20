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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.net.toUri
import me.impa.knockonports.BuildConfig

/**
 * Opens the Google Play Store app, navigating to the app details page for the calling application.
 *
 * @param context The context of the application or activity.  This is required to launch the Intent.
 */
fun openPlayStoreAppPage(context: Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=${context.packageName}".toUri()))
}

/**
 * Opens the Google Play Store app and searches for all applications published by a specific developer.
 *
 * @param context The context of the application or activity. This is required to launch the Intent.
 */
fun openPlayStoreDevPage(context: Context) {
    context.startActivity(Intent(Intent.ACTION_VIEW, "market://search?q=pub:Alexander+Yaburov".toUri()))
}

/**
 * Checks if the application was installed from the Google Play Store.
 *
 * For Android versions R (API level 30) and above, it uses [PackageManager.getInstallSourceInfo] to determine
 * the initiating and originating package names.
 * For older versions, it relies on the deprecated [PackageManager.getInstallerPackageName].
 *
 * @param packageManager The [PackageManager] instance to use for retrieving installation information.
 * @return `true` if the application was installed from the Google Play Store, `false` otherwise.
 */
fun isInstalledFromPlayStore(packageManager: PackageManager): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val installSourceInfo = packageManager.getInstallSourceInfo(BuildConfig.APPLICATION_ID)
        installSourceInfo.initiatingPackageName == "com.android.vending" ||
                installSourceInfo.originatingPackageName == "com.android.vending"
    } else {
        @Suppress("DEPRECATION") val installerPackageName =
            packageManager.getInstallerPackageName(BuildConfig.APPLICATION_ID)
        "com.android.vending" == installerPackageName
    }
