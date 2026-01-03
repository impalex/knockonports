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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.impa.knockonports.constants.CURRENT_BETA_TEST_MESSAGE
import me.impa.knockonports.constants.DEFAULT_CHECK_PERIOD
import me.impa.knockonports.data.settings.SettingsDataStore
import me.impa.knockonports.data.type.TitleOverflowType
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeConfig
import me.impa.knockonports.ui.config.ThemeContrast

object FakeSettingsDataStore : SettingsDataStore {

    override val themeSettings: Flow<ThemeConfig> = flowOf(ThemeConfig())
    override val knockCount: Flow<Long> = flowOf(0L)
    override val firstLaunch: Flow<Long> = flowOf(Long.MAX_VALUE)
    override val doNotAskBefore: Flow<Long> = flowOf(Long.MAX_VALUE)
    override val doNotAskReview: Flow<Boolean> = flowOf(true)
    override val doNotAskNotification: Flow<Boolean> = flowOf(true)
    override val betaMessageState: Flow<String?> = flowOf(CURRENT_BETA_TEST_MESSAGE)
    override val widgetConfirmation: Flow<Boolean> = flowOf(false)
    override val detectPublicIP: Flow<Boolean> = flowOf(false)
    override val ipv4Service: Flow<String> = flowOf("")
    override val ipv6Service: Flow<String> = flowOf("")
    override val customIpv4Service: Flow<String> = flowOf("")
    override val customIpv6Service: Flow<String> = flowOf("")
    override val detailedListView: Flow<Boolean> = flowOf(true)
    override val customIp4Header: Flow<Boolean> = flowOf(false)
    override val ip4HeaderSize: Flow<Int> = flowOf(0)
    override val resourceCheckPeriod: Flow<Int> = flowOf(DEFAULT_CHECK_PERIOD)
    override val isAppLockEnabled: Flow<Boolean> = flowOf(false)
    override val titleColorAvailable: Flow<Long> = flowOf(Color.Unspecified.toColorLong())
    override val titleColorUnavailable: Flow<Long> = flowOf(Color.Unspecified.toColorLong())

    override suspend fun setDarkMode(darkMode: DarkMode) {
        TODO("Not yet implemented")
    }

    override suspend fun setDynamicColors(useDynamicColors: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setCustomTheme(customTheme: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setContrast(contrast: ThemeContrast) {
        TODO("Not yet implemented")
    }

    override suspend fun setThemeSeed(seed: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun setAmoledTheme(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun incrementKnockCount() {
        TODO("Not yet implemented")
    }

    override suspend fun setFirstLaunchTime() {
        TODO("Not yet implemented")
    }

    override suspend fun postponeReviewRequest(time: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun setDoNotAskForReviewFlag() {
        TODO("Not yet implemented")
    }

    override suspend fun setDoNotAskForNotificationsFlag() {
        TODO("Not yet implemented")
    }

    override suspend fun setCurrentBetaMessageRead() {
        TODO("Not yet implemented")
    }

    override suspend fun setAppLock(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setWidgetConfirmation(confirmation: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setDetectPublicIP(detect: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setIpv4Service(service: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setIpv6Service(service: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setCustomIpv4Service(service: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setCustomIpv6Service(service: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setDetailedListView(detailed: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setCustomIp4Header(custom: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setIp4HeaderSize(size: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun setResourceCheckPeriod(period: Int) {
        TODO("Not yet implemented")
    }

    override val titleOverflow: Flow<TitleOverflowType>
        get() = flowOf(TitleOverflowType.END)

    override suspend fun setTitleOverflow(overflowType: TitleOverflowType) {
        TODO("Not yet implemented")
    }

    override val titleScale: Flow<Int>
        get() = flowOf(100)

    override suspend fun setTitleScale(scale: Int) {
        TODO("Not yet implemented")
    }

    override val titleMultiline: Flow<Boolean>
        get() = flowOf(false)

    override suspend fun setTitleMultiline(multiline: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setTitleColorAvailable(color: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun setTitleColorUnavailable(color: Long) {
        TODO("Not yet implemented")
    }
}