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

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.persistentMapOf
import me.impa.knockonports.R
import me.impa.knockonports.constants.MAX_TITLE_FONT_SCALE
import me.impa.knockonports.constants.MIN_TITLE_FONT_SCALE
import me.impa.knockonports.constants.TITLE_FONT_SCALE_STEP
import me.impa.knockonports.data.type.TitleOverflowType
import me.impa.knockonports.screen.component.common.HeaderSection
import me.impa.knockonports.screen.component.common.PrefMultiSelection
import me.impa.knockonports.screen.component.common.PrefStepSlider
import me.impa.knockonports.screen.component.common.PrefSwitch
import me.impa.knockonports.screen.viewmodel.state.settings.ListUiState
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent


fun LazyListScope.listSection(
    config: ListUiState,
    onEvent: (UiEvent) -> Unit = {}
) {
    item(key = "list") {
        HeaderSection(title = stringResource(R.string.title_settings_list), true)
    }
    item(key = "detailed_list_view") {
        PrefSwitch(
            title = stringResource(R.string.title_settings_detailed_list_view),
            description = stringResource(R.string.text_settings_detailed_list_view),
            value = config.detailedListView,
            onClick = { onEvent(UiEvent.SetDetailedView(!config.detailedListView)) }
        )
    }
    item(key = "multiline_title") {
        PrefSwitch(
            title = stringResource(R.string.title_settings_multiline_title),
            description = stringResource(R.string.text_settings_multiline_title),
            value = config.titleMultiline,
            onClick = { onEvent(UiEvent.SetTitleMultiline(!config.titleMultiline)) }
        )
    }
    item(key = "shorten_title") {
        val resources = LocalContext.current.resources
        val options = remember {
            persistentMapOf(
                TitleOverflowType.START.name to resources.getString(R.string.type_shorten_start),
                TitleOverflowType.MIDDLE.name to resources.getString(R.string.type_shorten_middle),
                TitleOverflowType.END.name to resources.getString(R.string.type_shorten_end),
            )
        }
        PrefMultiSelection(
            title = stringResource(R.string.title_settings_shorten_title),
            value = config.titleOverflow.name,
            map = options,
            onChanged = { onEvent(UiEvent.SetTitleOverflow(TitleOverflowType.valueOf(it))) }
        )
    }
    item(key = "font_scale") {
        PrefStepSlider(
            title = stringResource(R.string.title_settings_font_scale),
            description = "${config.titleFontScale}%",
            value = config.titleFontScale,
            minValue = MIN_TITLE_FONT_SCALE,
            maxValue = MAX_TITLE_FONT_SCALE,
            steps = (MAX_TITLE_FONT_SCALE - MIN_TITLE_FONT_SCALE) / TITLE_FONT_SCALE_STEP,
            onChanged = { onEvent(UiEvent.SetTitleFontScale(it)) }
        )
    }
}