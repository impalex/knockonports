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

package me.impa.knockonports.screen.component.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.R
import me.impa.knockonports.constants.CURRENT_BETA_TEST_MESSAGE
import me.impa.knockonports.constants.TAG_MAIN_DOTS_BUTTON
import me.impa.knockonports.constants.TAG_SETTINGS_MENU_ITEM
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.screen.viewmodel.state.main.MainBarEvent

@Composable
fun MainScreenActions(
    isRuLangAvailable: Boolean,
    isDetailedView: Boolean,
    onAction: (MainBarEvent) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    Row {
        if (BuildConfig.VERSION_NAME.contains("beta") && CURRENT_BETA_TEST_MESSAGE.isNotEmpty()) {
            var showBetaAlert by rememberSaveable { mutableStateOf(false) }
            if (showBetaAlert)
                BetaAlert { showBetaAlert = false }
            IconButton(
                onClick = debounced({ showBetaAlert = true })
            ) {
                Icon(painterResource(R.drawable.bug_report_icon), contentDescription = null)
            }
        }
        if (isRuLangAvailable) {
            DonateButton()
        }
        IconButton(onClick = debounced({ onAction(MainBarEvent.ToggleListMode) })) {
            if (isDetailedView) {
                Icon(painterResource(R.drawable.view_list), contentDescription = null)
            } else {
                Icon(painterResource(R.drawable.list), contentDescription = null)
            }
        }
        Button(onClick = debounced({ onAction(MainBarEvent.AddSequence) }),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }

        Box {
            IconButton(
                onClick = debounced({ showMenu = true }),
                modifier = Modifier.testTag(TAG_MAIN_DOTS_BUTTON)
            ) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                ExportMenuItem(onExport = { onAction(MainBarEvent.Export(it)) }, onDismiss = { showMenu = false })
                ImportMenuItem(onImport = { onAction(MainBarEvent.Import(it)) }, onDismiss = { showMenu = false })
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_show_log)) },
                    onClick = {
                        onAction(MainBarEvent.ShowLogs)
                        showMenu = false
                    })
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_settings)) },
                    modifier = Modifier.testTag(TAG_SETTINGS_MENU_ITEM),
                    onClick = {
                        onAction(MainBarEvent.Settings)
                        showMenu = false
                    })
            }
        }
    }

}

@Composable
fun DonateButton() {
    var showDonateDialog by rememberSaveable { mutableStateOf(false) }
    if (showDonateDialog) {
        DonateAlert { showDonateDialog = false }
    }
    IconButton(onClick = debounced({ showDonateDialog = true })) {
        Icon(Icons.Default.Favorite, contentDescription = null)
    }
}

@Preview
@Composable
fun PreviewMainScreenActions() {
    Row {
        MainScreenActions(true, true)
    }
}
