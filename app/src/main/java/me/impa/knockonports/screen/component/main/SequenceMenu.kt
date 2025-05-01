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

import android.content.Context
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import me.impa.knockonports.R
import me.impa.knockonports.constants.TAG_AUTOMATE_MENU_ITEM
import me.impa.knockonports.constants.TAG_SEQUENCE_DOTS_BUTTON
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.extension.getShortcutInfo
import me.impa.knockonports.screen.viewmodel.state.main.UiEvent

@Composable
fun SequenceMenu(sequence: Sequence, isShortcutsAvailable: Boolean, onEvent: (UiEvent) -> Unit = {}) {
    var showMenu by remember { mutableStateOf(false) }
    fun execute(event: UiEvent) {
        onEvent(event)
        showMenu = false
    }

    val dotsTag = "${TAG_SEQUENCE_DOTS_BUTTON}${sequence.id}"
    val context = LocalContext.current
    Box {
        IconButton(
            onClick = debounced({ showMenu = true }),
            modifier = Modifier.testTag(dotsTag)
        ) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_edit)) },
                onClick = { execute(UiEvent.Edit(requireNotNull(sequence.id))) }
            )
            if (isShortcutsAvailable)
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_create_shortcut)) },
                    onClick = {
                        createShortcut(context, sequence)
                        showMenu = false
                    }
                )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_duplicate)) },
                onClick = { execute(UiEvent.Duplicate(requireNotNull(sequence.id))) }
            )
            DropdownMenuItem(
                modifier = Modifier.testTag(TAG_AUTOMATE_MENU_ITEM),
                text = { Text(stringResource(R.string.action_integration)) },
                onClick = { execute(UiEvent.Automate(requireNotNull(sequence.id))) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_delete)) },
                onClick = { execute(UiEvent.Delete(requireNotNull(sequence.id))) }
            )
        }
    }
}

private fun createShortcut(context: Context, sequence: Sequence) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.getSystemService(ShortcutManager::class.java).requestPinShortcut(
            sequence.getShortcutInfo(context, false), null
        )
    }
}