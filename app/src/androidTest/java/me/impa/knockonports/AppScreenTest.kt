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

package me.impa.knockonports

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import me.impa.knockonports.constants.TAG_APP_SCREEN
import me.impa.knockonports.constants.TAG_AUTOMATE_MENU_ITEM
import me.impa.knockonports.constants.TAG_BACK_BUTTON
import me.impa.knockonports.constants.TAG_CLOSE_BUTTON
import me.impa.knockonports.constants.TAG_EDIT_CONFIG_TAB
import me.impa.knockonports.constants.TAG_EDIT_SEQUENCE_TAB
import me.impa.knockonports.constants.TAG_EDIT_HOST
import me.impa.knockonports.constants.TAG_EDIT_LOCAL_PORT
import me.impa.knockonports.constants.TAG_MAIN_DOTS_BUTTON
import me.impa.knockonports.constants.TAG_SEQUENCE_DOTS_BUTTON
import me.impa.knockonports.constants.TAG_SEQUENCE_ITEM
import me.impa.knockonports.constants.TAG_SETTINGS_MENU_ITEM
import me.impa.knockonports.di.AppModule
import me.impa.knockonports.mock.FakeModule
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule


@UninstallModules(AppModule::class)
@HiltAndroidTest
class AppScreenTest {

    @get:Rule(order = 1)
    val hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun makeScreenShots() {
        // Main screen
        composeTestRule.onNodeWithTag(TAG_APP_SCREEN).assertIsDisplayed()
        FakeModule.testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)
        Screengrab.screenshot("01_sequence_list")
        // Basic sequence settings
        val selectItemTag = "${TAG_SEQUENCE_ITEM}4"
        composeTestRule.onNodeWithTag(selectItemTag).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_EDIT_SEQUENCE_TAB).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TAG_EDIT_HOST).assertIsDisplayed()
        Thread.sleep(1000)
        Screengrab.screenshot("02_sequence_edit_basic")
        // Advanced sequence settings
        composeTestRule.onNodeWithTag(TAG_EDIT_CONFIG_TAB).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_EDIT_LOCAL_PORT).assertIsDisplayed()
        Thread.sleep(1000)
        Screengrab.screenshot("03_sequence_edit_advanced")
        // Automate URI
        composeTestRule.onNodeWithTag(TAG_BACK_BUTTON).performClick()
        composeTestRule.waitForIdle()
        val sequenceMenuTag = "${TAG_SEQUENCE_DOTS_BUTTON}4"
        composeTestRule.onNodeWithTag(sequenceMenuTag).assertIsDisplayed()
        composeTestRule.onNodeWithTag(sequenceMenuTag).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_AUTOMATE_MENU_ITEM).performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)
        Screengrab.screenshot("04_sequence_automate")
        // Settings
        composeTestRule.onNodeWithTag(TAG_CLOSE_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TAG_MAIN_DOTS_BUTTON).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_SETTINGS_MENU_ITEM).performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)
        Screengrab.screenshot("05_settings")
    }

    companion object {

        @ClassRule
        @JvmField
        val localeTestRule: LocaleTestRule = LocaleTestRule()
    }
}