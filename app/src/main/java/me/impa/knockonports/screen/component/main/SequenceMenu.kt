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

package me.impa.knockonports.screen.component.main

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import me.impa.knockonports.R
import me.impa.knockonports.constants.TAG_AUTOMATE_MENU_ITEM
import me.impa.knockonports.constants.TAG_SEQUENCE_DOTS_BUTTON
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.screen.event.SequenceMenuEvent

@Composable
fun SequenceMenu(id: Long?, isShortcutsAvailable: Boolean, onAction: (SequenceMenuEvent) -> Unit = {}) {
    var showMenu by remember { mutableStateOf(false) }
    fun execute(event: SequenceMenuEvent) {
        onAction(event)
        showMenu = false
    }
    val dotsTag = "${TAG_SEQUENCE_DOTS_BUTTON}$id"
    Box {
        IconButton(onClick = debounced({ showMenu = true }),
            modifier = Modifier.testTag(dotsTag)) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_edit)) },
                onClick = { execute(SequenceMenuEvent.Edit) }
            )
            if (isShortcutsAvailable)
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_create_shortcut)) },
                    onClick = { execute(SequenceMenuEvent.CreateShortcut) }
                )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_duplicate)) },
                onClick = { execute(SequenceMenuEvent.Duplicate) }
            )
            DropdownMenuItem(
                modifier = Modifier.testTag(TAG_AUTOMATE_MENU_ITEM),
                text = { Text(stringResource(R.string.action_integration)) },
                onClick = { execute(SequenceMenuEvent.Automation) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.action_delete)) },
                onClick = { execute(SequenceMenuEvent.Delete) }
            )
        }
    }
}

