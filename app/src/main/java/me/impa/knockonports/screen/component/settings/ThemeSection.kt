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

package me.impa.knockonports.screen.component.settings

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.persistentMapOf
import me.impa.knockonports.R
import me.impa.knockonports.screen.component.common.HeaderSection
import me.impa.knockonports.screen.component.common.PrefColorSelection
import me.impa.knockonports.screen.component.common.PrefMultiSelection
import me.impa.knockonports.screen.component.common.PrefStepSlider
import me.impa.knockonports.screen.component.common.PrefSwitch
import me.impa.knockonports.screen.viewmodel.state.settings.ThemeUiState
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent
import me.impa.knockonports.ui.config.DarkMode
import me.impa.knockonports.ui.config.ThemeContrast
import me.impa.knockonports.ui.theme.themeMap

fun LazyListScope.themeSection(
    theme: ThemeUiState,
    onEvent: (UiEvent) -> Unit
) {
    item { HeaderSection(stringResource(R.string.title_settings_theme)) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        item {
            PrefSwitch(
                stringResource(R.string.title_settings_dynamic_theme),
                stringResource(R.string.text_settings_dynamic_theme),
                theme.dynamicColors, onClick = { onEvent(UiEvent.SetDynamicMode(!theme.dynamicColors)) }
            )
        }
    }
    item {
        val autoDarkMode = stringResource(R.string.text_settings_system_dark_light)
        val lightMode = stringResource(R.string.text_settings_light)
        val darkMode = stringResource(R.string.text_settings_dark)
        val modeMap = remember {
            persistentMapOf(
                DarkMode.AUTO.toString() to autoDarkMode,
                DarkMode.LIGHT.toString() to lightMode,
                DarkMode.DARK.toString() to darkMode
            )
        }
        PrefMultiSelection(
            stringResource(R.string.title_settings_dark_light),
            theme.darkMode.toString(), modeMap,
            onChanged = { onEvent(UiEvent.SetDarkMode(DarkMode.valueOf(it))) }
        )
    }
    if (!theme.dynamicColors) {
        item {
            PrefColorSelection(
                stringResource(R.string.title_settings_custom_theme),
                stringResource(R.string.text_settings_custom_theme),
                theme.customTheme,
                onChanged = { onEvent(UiEvent.SetCustomTheme(it)) }
            )
        }
        item {
            val contrastString = when (theme.contrast) {
                ThemeContrast.STANDARD -> stringResource(R.string.text_settings_contrast_standard)
                ThemeContrast.MEDIUM -> stringResource(R.string.text_settings_contrast_medium)
                ThemeContrast.HIGH -> stringResource(R.string.text_settings_contrast_high)
            }
            PrefStepSlider(
                stringResource(R.string.title_settings_contrast),
                contrastString,
                theme.contrast.ordinal,
                0,
                ThemeContrast.entries.size - 1,
                onChanged = { onEvent(UiEvent.SetContrast(ThemeContrast.entries[it])) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewThemeSection() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        themeSection(ThemeUiState(
            darkMode = DarkMode.AUTO,
            dynamicColors = false,
            customTheme = themeMap.keys.first(),
            contrast = ThemeContrast.STANDARD
        )) {}
    }
}