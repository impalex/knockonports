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

package me.impa.knockonports.screen.component.sequence

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import me.impa.knockonports.R
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.extension.stringResourceId
import me.impa.knockonports.screen.component.common.DialogItemsSource
import me.impa.knockonports.screen.component.common.DialogMenu

@Composable
fun SelectProtocolVersion(value: ProtocolVersionType, onUpdate: (ProtocolVersionType) -> Unit) {
    val resources = LocalContext.current.resources
    val ipvList = remember(resources) {
        ProtocolVersionType.entries.associateBy({ it }, { resources.getString(it.stringResourceId()) })
    }
    val ipvSource = remember(ipvList, value) {
        DialogItemsSource.ListItems(ipvList.keys.toList(), value.ordinal)
    }
    DialogMenu(
        label = stringResource(R.string.field_ip_version),
        itemsSource = ipvSource,
        selectedItemToString = { item -> ipvList[item] ?: "" },
        onItemSelected = { _, item -> onUpdate(item) }
    )
}