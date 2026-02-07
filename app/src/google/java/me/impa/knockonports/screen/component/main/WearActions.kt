/*
 * Copyright (c) 2026 Alexander Yaburov
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

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.impa.knockonports.R
import me.impa.knockonports.extension.navigate
import me.impa.knockonports.navigation.WearRoute
import me.impa.knockonports.screen.component.common.LocalAppEventBus

@Composable
fun WearActions(onAction: () -> Unit) {
    val eventBus = LocalAppEventBus.current
    DropdownMenuItem(
        text = { Text(stringResource(R.string.action_wear_companion)) },
        onClick = {
            eventBus.navigate(WearRoute)
            onAction.invoke()
        }
    )
}