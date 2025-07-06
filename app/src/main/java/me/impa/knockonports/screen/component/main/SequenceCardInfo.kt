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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.impa.knockonports.data.type.TitleOverflowType
import me.impa.knockonports.data.type.toTextOverflow

@Suppress("LongParameterList")
@Composable
fun RowScope.SequenceCardInfo(
    name: String, host: String, details: String, showDetails: Boolean,
    titleOverflowType: TitleOverflowType,
    multilineTitle: Boolean,
    titleStyle: TextStyle
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp, end = 8.dp, bottom = if (showDetails) 0.dp else 8.dp)
            .weight(1f)
    ) {
        Text(
            text = name,
            maxLines = if (multilineTitle) Int.MAX_VALUE else 1,
            overflow = titleOverflowType.toTextOverflow(),
            style = titleStyle
        )
        if (showDetails) {
            Text(
                text = host,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = details,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

