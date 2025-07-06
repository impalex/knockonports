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

package me.impa.knockonports.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import me.impa.knockonports.R
import me.impa.knockonports.StartKnockingActivity
import me.impa.knockonports.constants.EXTRA_VALUE_SOURCE_WIDGET

@Composable
fun WidgetEmptyListMessage() {
    val noDataText = LocalContext.current.resources.getString(R.string.text_widget_empty_list)
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = noDataText,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                color = GlanceTheme.colors.onSurface,
                fontSize = 16.sp
            )
        )
    }
}

fun launchKnocker(context: Context, sequenceId: Long) {
    val intent = Intent(context, StartKnockingActivity::class.java).apply {
        putExtra("EXTRA_SEQ_ID", sequenceId)
        putExtra("EXTRA_SOURCE", EXTRA_VALUE_SOURCE_WIDGET)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}



