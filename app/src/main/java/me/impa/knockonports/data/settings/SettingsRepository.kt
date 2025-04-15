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

package me.impa.knockonports.data.settings

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.impa.knockonports.ui.config.ThemeConfig
import me.impa.knockonports.ui.theme.themeMap
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.constants.ONE_DAY_IN_MILLIS
import me.impa.knockonports.constants.POSTPONE_TIME_START
import me.impa.knockonports.util.isInstalledFromPlayStore
import timber.log.Timber

private const val CFG_APP_THEME = "CFG_APP_THEME"
private const val CFG_DYNAMIC_THEME = "CFG_DYNAMIC_THEME"
private const val CFG_DARK_MODE = "CFG_DARK_MODE"
private const val CFG_CONTRAST = "CFG_CONTRAST"
private const val CFG_CONFIRM_WIDGET = "CFG_CONFIRM_WIDGET"
private const val CFG_DETECT_PUBLIC_IP = "CFG_DETECT_PUBLIC_IP"
private const val CFG_IP_4_SERVICE = "CFG_IP4_SERVICE"
private const val CFG_IP_6_SERVICE = "CFG_IP6_SERVICE"
private const val CFG_IP_6_CUSTOM_SERVICE = "CFG_IP6_CUSTOM_SERVICE"
private const val CFG_IP_4_CUSTOM_SERVICE = "CFG_IP4_CUSTOM_SERVICE"
private const val CFG_FIRST_LAUNCH = "CFG_FIRST_LAUNCH"
private const val CFG_FIRST_LAUNCH_V2 = "CFG_FIRST_LAUNCH_V2"
private const val CFG_KNOCK_COUNT = "CFG_KNOCK_COUNT"
private const val CFG_DO_NOT_ASK_REVIEW = "CFG_DO_NOT_ASK_REVIEW"
private const val CFG_DO_NOT_ASK_BEFORE = "CFG_DO_NOT_ASK_BEFORE"
private const val CFG_DETAILED_LIST_VIEW = "CFG_DETAILED_LIST_VIEW"
private const val CFG_DO_NOT_ASK_NOTIFICATION = "CFG_DO_NOT_ASK_NOTIFICATION"

inline fun <reified T : Enum<T>> String.toEnum(default: T): T =
    enumValues<T>().firstOrNull { this.equals(it) } ?: default

/**
 * Repository for managing application settings, persisting and retrieving them from SharedPreferences.
 *
 * This class provides access to application-wide settings ([AppSettings]) and theme-related settings ([ThemeConfig]).
 * It uses [StateFlow] to expose settings as reactive streams, allowing UI components to observe and react to changes.
 *
 * **AppSettings:**  General application settings like IP address service preferences, widget behavior, etc.
 *
 * **ThemeConfig:**  Settings related to the app's appearance, including dark mode, dynamic colors, and custom themes.
 *
 * In addition to the core settings, this repository also manages various utility settings related to app usage,
 * such as launch counts, review requests, and notification prompts.  These are kept separate from the main
 * settings objects for organizational purposes.
 *
 * @property sharedPreferences The SharedPreferences instance used for persistent storage of settings.
 * @property packageManager  The PackageManager instance used to determine installation source (e.g., Play Store).
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    packageManager: PackageManager
) {

    private val _appSettings = MutableStateFlow<AppSettings>(AppSettings())

    val appSettings: StateFlow<AppSettings>
        get() = _appSettings

    private val _themeSettings = MutableStateFlow<ThemeConfig>(ThemeConfig())
    val themeSettings: StateFlow<ThemeConfig>
        get() = _themeSettings

    // region Various Settings. There is no reason to keep these settings along with others
    private val _knockCount = mutableLongStateOf(0L)
    val knockCount: State<Long>
        get() = _knockCount

    private val _doNotAskAboutNotifications = mutableStateOf(false)
    val doNotAskAboutNotifications: State<Boolean>
        get() = _doNotAskAboutNotifications

    private val _firstLaunchV2 = mutableStateOf(false)
    val firstLaunchV2: State<Boolean>
        get() = _firstLaunchV2

    private val _askReviewTime = mutableLongStateOf(0L)
    val askReviewTime: State<Long>
        get() = _askReviewTime

    private val _doNotAskForReview = mutableStateOf(false)
    val doNotAskForReview: State<Boolean>
        get() = _doNotAskForReview
    // endregion

    private val _isInstalledFromPlayStore = mutableStateOf(isInstalledFromPlayStore(packageManager))
    val isInstalledFromPlayStore: State<Boolean>
        get() = _isInstalledFromPlayStore

    init {
        loadSettings()
    }

    private fun loadThemeSettings(): ThemeConfig =
        with(_themeSettings.value) {
            copy(
                useDarkTheme = (sharedPreferences.getString(CFG_DARK_MODE, useDarkTheme.name)
                    ?: useDarkTheme.name).toEnum(useDarkTheme),
                useDynamicColors = sharedPreferences.getBoolean(CFG_DYNAMIC_THEME, useDynamicColors)
                        && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S),
                customTheme = (sharedPreferences.getString(CFG_APP_THEME, customTheme) ?: customTheme).let {
                    if (themeMap.keys.contains(it)) {
                        it
                    } else {
                        themeMap.keys.first()
                    }
                },
                contrast = (sharedPreferences.getString(CFG_CONTRAST, contrast.name)
                    ?: contrast.name).toEnum(contrast)
            )

        }

    private fun saveThemeSettings() {
        with(_themeSettings.value) {
            sharedPreferences.edit {
                putString(CFG_APP_THEME, customTheme)
                putString(CFG_DARK_MODE, useDarkTheme.name)
                putBoolean(CFG_DYNAMIC_THEME, useDynamicColors)
                putString(CFG_CONTRAST, contrast.name)
            }
        }
    }

    private fun loadUtilitySettings() {
        _knockCount.longValue = sharedPreferences.getLong(CFG_KNOCK_COUNT, 0)
        Timber.d("Knock count: ${_knockCount.longValue}")
        _doNotAskAboutNotifications.value = sharedPreferences.getBoolean(CFG_DO_NOT_ASK_NOTIFICATION, false)
        Timber.d("Do not ask about notifications: ${_doNotAskAboutNotifications.value}")

        sharedPreferences.getLong(CFG_FIRST_LAUNCH, 0).also {
            if (it == 0L) {
                // First launch
                val currentTime = System.currentTimeMillis()
                sharedPreferences.edit {
                    putLong(CFG_FIRST_LAUNCH, currentTime)
                    putLong(CFG_FIRST_LAUNCH_V2, currentTime)
                    putLong(CFG_DO_NOT_ASK_BEFORE, currentTime + POSTPONE_TIME_START)
                }
            } else {
                sharedPreferences.getLong(CFG_FIRST_LAUNCH_V2, 0).also {
                    if (it == 0L) {
                        // V2 first launch
                        _firstLaunchV2.value = true
                        sharedPreferences.edit {
                            putLong(CFG_FIRST_LAUNCH_V2, System.currentTimeMillis())
                        }
                    }
                }
            }
        }

        _askReviewTime.longValue = sharedPreferences.getLong(CFG_DO_NOT_ASK_BEFORE, 0)
        Timber.d("Ask review time: ${_askReviewTime.longValue}")
        _doNotAskForReview.value = sharedPreferences.getBoolean(CFG_DO_NOT_ASK_REVIEW, false)
        Timber.d("Do not ask for review: ${_doNotAskForReview.value}")
    }

    private fun loadSettings() {
        // Load app settings from SharedPreferences
        loadUtilitySettings()
        _themeSettings.value = loadThemeSettings()
        _appSettings.value = _appSettings.value.copy(
            widgetConfirmation = sharedPreferences.getBoolean(CFG_CONFIRM_WIDGET, false),
            detectPublicIP = sharedPreferences.getBoolean(CFG_DETECT_PUBLIC_IP, false),
            ipv4Service = (sharedPreferences.getString(CFG_IP_4_SERVICE, "") ?: "").let {
                if (Ipv4ProviderMap.keys.contains(it)) {
                    it
                } else {
                    Ipv4ProviderMap.keys.first()
                }
            },
            ipv6Service = (sharedPreferences.getString(CFG_IP_6_SERVICE, "") ?: "").let {
                if (Ipv6ProviderMap.keys.contains(it)) {
                    it
                } else {
                    Ipv6ProviderMap.keys.first()
                }
            },
            customIpv4Service = sharedPreferences.getString(CFG_IP_4_CUSTOM_SERVICE, "") ?: "",
            customIpv6Service = sharedPreferences.getString(CFG_IP_6_CUSTOM_SERVICE, "") ?: "",
            detailedListView = sharedPreferences.getBoolean(CFG_DETAILED_LIST_VIEW, true)
        )
    }

    private fun saveAppSettings() {
        with(_appSettings.value) {
            sharedPreferences.edit {
                putBoolean(CFG_CONFIRM_WIDGET, widgetConfirmation)
                putBoolean(CFG_DETECT_PUBLIC_IP, detectPublicIP)
                putString(CFG_IP_4_SERVICE, ipv4Service)
                putString(CFG_IP_6_SERVICE, ipv6Service)
                putString(CFG_IP_4_CUSTOM_SERVICE, customIpv4Service)
                putString(CFG_IP_6_CUSTOM_SERVICE, customIpv6Service)
                putBoolean(CFG_DETAILED_LIST_VIEW, detailedListView)
            }
        }
    }

    fun updateAppSettings(newSettings: AppSettings) {
        _appSettings.value = newSettings
        saveAppSettings()
    }

    fun updateThemeSettings(newSettings: ThemeConfig) {
        _themeSettings.value = newSettings
        saveThemeSettings()
    }

    fun incrementKnockCount() {
        _knockCount.longValue = _knockCount.longValue + 1
        sharedPreferences.edit {
            putLong(CFG_KNOCK_COUNT, _knockCount.longValue)
        }
    }

    fun setDoNotAskAboutNotificationsFlag() {
        _doNotAskAboutNotifications.value = true
        sharedPreferences.edit {
            putBoolean(CFG_DO_NOT_ASK_NOTIFICATION, _doNotAskAboutNotifications.value)
        }
    }

    fun postponeReviewRequest(time: Long) {
        _askReviewTime.longValue = System.currentTimeMillis() + time
        Timber.d("Postponing review request for $time ms (until ${_askReviewTime.longValue})")
        sharedPreferences.edit {
            putLong(CFG_DO_NOT_ASK_BEFORE, _askReviewTime.longValue)
        }
    }

    fun doNotAskForReview() {
        _doNotAskForReview.value = true
        sharedPreferences.edit {
            putBoolean(CFG_DO_NOT_ASK_REVIEW, _doNotAskForReview.value)
        }
    }

    fun clearFirstLaunchV2() {
        _firstLaunchV2.value = false
    }
}