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

package me.impa.knockonports.data.settings

import android.content.Context
import android.os.Build
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import me.impa.knockonports.constants.CURRENT_BETA_TEST_MESSAGE
import me.impa.knockonports.constants.DEFAULT_CHECK_PERIOD
import me.impa.knockonports.constants.MIN_IP4_HEADER_SIZE
import me.impa.knockonports.constants.POSTPONE_TIME_START
import me.impa.knockonports.helper.toEnum
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeConfig
import me.impa.knockonports.ui.config.ThemeContrast
import me.impa.knockonports.ui.theme.themeMap
import timber.log.Timber
import javax.inject.Singleton

/**
 * Interface for accessing and modifying application settings.
 *
 * This interface provides methods to get and set various settings related to theme,
 * miscellaneous app behavior, and general app configurations.
 * Settings are exposed as [Flow]s to allow for reactive updates.
 */
@Suppress("TooManyFunctions")
@Singleton
interface SettingsDataStore {
    // region Theme settings
    val themeSettings: Flow<ThemeConfig>
    suspend fun setDarkMode(darkMode: DarkMode)
    suspend fun setDynamicColors(useDynamicColors: Boolean)
    suspend fun setCustomTheme(customTheme: String)
    suspend fun setContrast(contrast: ThemeContrast)
    // endregion Theme settings

    // region Miscellaneous settings
    val knockCount: Flow<Long>
    suspend fun incrementKnockCount()
    val firstLaunch: Flow<Long>
    suspend fun setFirstLaunchTime()
    val doNotAskBefore: Flow<Long>
    suspend fun postponeReviewRequest(time: Long)
    val doNotAskReview: Flow<Boolean>
    suspend fun setDoNotAskForReviewFlag()
    val doNotAskNotification: Flow<Boolean>
    suspend fun setDoNotAskForNotificationsFlag()
    val betaMessageState: Flow<String?>
    suspend fun setCurrentBetaMessageRead()
    // endregion Miscellaneous settings

    // region App settings
    val widgetConfirmation: Flow<Boolean>
    suspend fun setWidgetConfirmation(confirmation: Boolean)
    val detectPublicIP: Flow<Boolean>
    suspend fun setDetectPublicIP(detect: Boolean)
    val ipv4Service: Flow<String>
    suspend fun setIpv4Service(service: String)
    val ipv6Service: Flow<String>
    suspend fun setIpv6Service(service: String)
    val customIpv4Service: Flow<String>
    suspend fun setCustomIpv4Service(service: String)
    val customIpv6Service: Flow<String>
    suspend fun setCustomIpv6Service(service: String)
    val detailedListView: Flow<Boolean>
    suspend fun setDetailedListView(detailed: Boolean)
    val customIp4Header: Flow<Boolean>
    suspend fun setCustomIp4Header(custom: Boolean)
    val ip4HeaderSize: Flow<Int>
    suspend fun setIp4HeaderSize(size: Int)
    val resourceCheckPeriod: Flow<Int>
    suspend fun setResourceCheckPeriod(period: Int)
    // endregion App settings

}

@Suppress("TooManyFunctions")
@Singleton
class SettingsDataStoreImpl(@ApplicationContext val context: Context) : SettingsDataStore {

    // region Theme settings
    override val themeSettings = context.dataStore.data
        .distinctUntilChanged { first, second ->
            first[useDarkThemeKey] == second[useDarkThemeKey] &&
                    first[useDynamicColorsKey] == second[useDynamicColorsKey] &&
                    first[customThemeKey] == second[customThemeKey] &&
                    first[contrastKey] == second[contrastKey]
        }
        .map { preferences ->
            ThemeConfig(
                useDarkTheme = (preferences[useDarkThemeKey] ?: "").toEnum(DarkMode.AUTO),
                useDynamicColors = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) &&
                        (preferences[useDynamicColorsKey] != false),
                customTheme = (preferences[customThemeKey] ?: "").let {
                    if (themeMap.keys.contains(it)) it else themeMap.keys.first()
                },
                contrast = (preferences[contrastKey] ?: "").toEnum(ThemeContrast.STANDARD)
            )
        }

    override suspend fun setDarkMode(darkMode: DarkMode) {
        context.dataStore.edit { preferences ->
            preferences[useDarkThemeKey] = darkMode.name
        }
    }

    override suspend fun setDynamicColors(useDynamicColors: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[useDynamicColorsKey] = useDynamicColors
        }
    }

    override suspend fun setCustomTheme(customTheme: String) {
        context.dataStore.edit { preferences ->
            preferences[customThemeKey] = customTheme
        }
    }

    override suspend fun setContrast(contrast: ThemeContrast) {
        context.dataStore.edit { preferences ->
            preferences[contrastKey] = contrast.name
        }
    }
    // endregion Theme settings

    // region Miscellaneous settings
    override val knockCount = context.dataStore.data
        .distinctUntilChangedBy { it[knockCountKey] }
        .map { preferences -> preferences[knockCountKey] ?: 0 }

    override suspend fun incrementKnockCount() {
        context.dataStore.edit { preferences ->
            preferences[knockCountKey] = (preferences[knockCountKey] ?: 0) + 1
        }
    }

    override val firstLaunch = context.dataStore.data
        .distinctUntilChangedBy { it[firstLaunchKey] }
        .map { preferences -> preferences[firstLaunchKey] ?: 0 }

    override suspend fun setFirstLaunchTime() {
        context.dataStore.edit { preferences ->
            preferences[firstLaunchKey] = System.currentTimeMillis()
            preferences[doNotAskBeforeKey] = System.currentTimeMillis() + POSTPONE_TIME_START
        }
    }

    override val doNotAskBefore = context.dataStore.data
        .distinctUntilChangedBy { it[doNotAskBeforeKey] }
        .map { preferences -> preferences[doNotAskBeforeKey] ?: Long.MAX_VALUE }

    override suspend fun postponeReviewRequest(time: Long) {
        context.dataStore.edit { preferences ->
            preferences[doNotAskBeforeKey] = System.currentTimeMillis() + time
        }
    }

    override val doNotAskReview = context.dataStore.data
        .distinctUntilChangedBy { it[doNotAskReviewKey] }
        .map { preferences -> preferences[doNotAskReviewKey] == true }

    override suspend fun setDoNotAskForReviewFlag() {
        context.dataStore.edit { preferences ->
            preferences[doNotAskReviewKey] = true
        }
    }

    override val doNotAskNotification = context.dataStore.data
        .distinctUntilChangedBy { it[doNotAskNotificationKey] }
        .map { preferences -> preferences[doNotAskNotificationKey] == true }

    override suspend fun setDoNotAskForNotificationsFlag() {
        context.dataStore.edit { preferences ->
            preferences[doNotAskNotificationKey] = true
        }
    }

    override val betaMessageState = context.dataStore.data
        .distinctUntilChangedBy { it[betaMessageStateKey] }
        .map { preferences -> preferences[betaMessageStateKey] }

    override suspend fun setCurrentBetaMessageRead() {
        context.dataStore.edit { preferences ->
            preferences[betaMessageStateKey] = CURRENT_BETA_TEST_MESSAGE
        }
    }
    // endregion Miscellaneous settings

    // region App settings
    override val widgetConfirmation = context.dataStore.data
        .distinctUntilChangedBy { it[widgetConfirmationKey] }
        .map { preferences -> preferences[widgetConfirmationKey] == true }

    override suspend fun setWidgetConfirmation(confirmation: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[widgetConfirmationKey] = confirmation
        }
    }

    override val detectPublicIP = context.dataStore.data
        .distinctUntilChangedBy { it[detectPublicIPKey] }
        .map { preferences -> preferences[detectPublicIPKey] == true }

    override suspend fun setDetectPublicIP(detect: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[detectPublicIPKey] = detect
        }
    }

    override val ipv4Service = context.dataStore.data
        .distinctUntilChangedBy { it[ipv4ServiceKey] }
        .map { preferences ->
            (preferences[ipv4ServiceKey] ?: "").let {
                if (Ipv4ProviderMap.keys.contains(it)) it else Ipv4ProviderMap.keys.first()
            }
        }

    override suspend fun setIpv4Service(service: String) {
        context.dataStore.edit { preferences ->
            preferences[ipv4ServiceKey] = service
        }
    }

    override val ipv6Service = context.dataStore.data
        .distinctUntilChangedBy { it[ipv6ServiceKey] }
        .map { preferences ->
            (preferences[ipv6ServiceKey] ?: "").let {
                if (Ipv6ProviderMap.keys.contains(it)) it else Ipv6ProviderMap.keys.first()
            }
        }

    override suspend fun setIpv6Service(service: String) {
        context.dataStore.edit { preferences ->
            preferences[ipv6ServiceKey] = service
        }
    }

    override val customIpv4Service = context.dataStore.data
        .distinctUntilChangedBy { it[customIpv4ServiceKey] }
        .map { preferences -> preferences[customIpv4ServiceKey] ?: "" }

    override suspend fun setCustomIpv4Service(service: String) {
        context.dataStore.edit { preferences ->
            preferences[customIpv4ServiceKey] = service
        }
    }

    override val customIpv6Service = context.dataStore.data
        .distinctUntilChangedBy { it[customIpv6ServiceKey] }
        .map { preferences -> preferences[customIpv6ServiceKey] ?: "" }

    override suspend fun setCustomIpv6Service(service: String) {
        context.dataStore.edit { preferences ->
            preferences[customIpv6ServiceKey] = service
        }
    }

    override val detailedListView = context.dataStore.data
        .distinctUntilChangedBy { it[detailedListViewKey] }
        .map { preferences -> preferences[detailedListViewKey] != false }

    override suspend fun setDetailedListView(detailed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[detailedListViewKey] = detailed
        }
    }

    override val customIp4Header = context.dataStore.data
        .distinctUntilChangedBy { it[customIp4HeaderKey] }
        .map { preferences -> preferences[customIp4HeaderKey] == true }

    override suspend fun setCustomIp4Header(custom: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[customIp4HeaderKey] = custom
        }
    }

    override val ip4HeaderSize = context.dataStore.data
        .distinctUntilChangedBy { it[ip4HeaderSizeKey] }
        .map { preferences -> preferences[ip4HeaderSizeKey] ?: MIN_IP4_HEADER_SIZE }

    override suspend fun setIp4HeaderSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[ip4HeaderSizeKey] = size
        }
    }

    override val resourceCheckPeriod = context.dataStore.data
        .distinctUntilChangedBy { it[resourceCheckPeriodKey] }
        .map { preferences -> preferences[resourceCheckPeriodKey] ?: DEFAULT_CHECK_PERIOD }

    override suspend fun setResourceCheckPeriod(period: Int) {
        context.dataStore.edit { preferences ->
            preferences[resourceCheckPeriodKey] = period
        }
    }
    // endregion App settings

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings", produceMigrations = { context ->
            listOf(SharedPreferencesMigration(context, "me.impa.knockonports_preferences"))
        })

        // Theme settings
        private val useDarkThemeKey = stringPreferencesKey("CFG_DARK_MODE")
        private val useDynamicColorsKey = booleanPreferencesKey("CFG_DYNAMIC_THEME")
        private val customThemeKey = stringPreferencesKey("CFG_APP_THEME")
        private val contrastKey = stringPreferencesKey("CFG_CONTRAST")

        // Miscellaneous settings
        private val firstLaunchKey = longPreferencesKey("CFG_FIRST_LAUNCH")

        // private val firstLaunchV2Key = longPreferencesKey("CFG_FIRST_LAUNCH_V2") -- delete it later
        private val doNotAskBeforeKey = longPreferencesKey("CFG_DO_NOT_ASK_BEFORE")
        private val doNotAskReviewKey = booleanPreferencesKey("CFG_DO_NOT_ASK_REVIEW")
        private val doNotAskNotificationKey = booleanPreferencesKey("CFG_DO_NOT_ASK_NOTIFICATION")
        private val betaMessageStateKey = stringPreferencesKey("CFG_BETA_MESSAGE_STATE")
        private val knockCountKey = longPreferencesKey("CFG_KNOCK_COUNT")

        // App settings
        private val widgetConfirmationKey = booleanPreferencesKey("CFG_CONFIRM_WIDGET")
        private val detectPublicIPKey = booleanPreferencesKey("CFG_DETECT_PUBLIC_IP")
        private val ipv4ServiceKey = stringPreferencesKey("CFG_IP4_SERVICE")
        private val ipv6ServiceKey = stringPreferencesKey("CFG_IP6_SERVICE")
        private val customIpv4ServiceKey = stringPreferencesKey("CFG_IP4_CUSTOM_SERVICE")
        private val customIpv6ServiceKey = stringPreferencesKey("CFG_IP6_CUSTOM_SERVICE")
        private val detailedListViewKey = booleanPreferencesKey("CFG_DETAILED_LIST_VIEW")
        private val customIp4HeaderKey = booleanPreferencesKey("CFG_CUSTOM_IP4_HEADER")
        private val ip4HeaderSizeKey = intPreferencesKey("CFG_IP4_HEADER_SIZE")
        private val resourceCheckPeriodKey = intPreferencesKey("CFG_RESOURCE_CHECK_PERIOD")
    }

}