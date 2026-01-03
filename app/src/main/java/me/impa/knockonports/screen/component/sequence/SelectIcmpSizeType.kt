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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import me.impa.knockonports.R
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.extension.stringResourceId
import me.impa.knockonports.screen.component.common.DialogItemsSource
import me.impa.knockonports.screen.component.common.DialogMenu

@Composable
fun SelectIcmpSizeType(value: IcmpType, onUpdate: (IcmpType) -> Unit) {
    val resources = LocalResources.current
    val icmpSizeTypeList = remember(resources) {
        IcmpType.entries.associateBy({ it }, {resources.getString(it.stringResourceId()) })
    }

    DialogMenu(
        label = stringResource(R.string.field_icmp_packet_size),
        itemsSource = DialogItemsSource.ListItems(
            icmpSizeTypeList.keys.toList(),
            value.ordinal
        ),
        selectedItemToString = { icmpSizeTypeList.getOrDefault(it, "") },
        onItemSelected = { _, item ->
            onUpdate(item)
        }
    )
}