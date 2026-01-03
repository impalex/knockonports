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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.impa.knockonports.R
import me.impa.knockonports.data.WidgetDataStore
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.screen.PreviewData

class LaunchKnocksWidget : GlanceAppWidget() {

    val scope = CoroutineScope(Dispatchers.Default)

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val currentSequence = currentState(KEY_SELECTED_SEQ) ?: -1
            val repository = remember { WidgetDataStore.get(context) }
            val sequences by repository.data.collectAsState(initial = listOf())
            LaunchKnocksWidgetContent(sequences, currentSequence) { offset ->
                var newIndex =
                    (if (currentSequence < 0) 0 else sequences.indexOfFirst { it.id == currentSequence }) + offset
                if (newIndex >= sequences.size)
                    newIndex = 0
                if (newIndex < 0)
                    newIndex = sequences.size - 1
                scope.launch {
                    updateAppWidgetState(context, id) {
                        it[KEY_SELECTED_SEQ] = sequences[newIndex].id ?: -1
                    }
                    update(context, id)
                }
            }
        }
    }

    companion object {
        val KEY_SELECTED_SEQ = longPreferencesKey("selected_seq")
    }
}

@Composable
private fun LaunchKnocksWidgetContent(sequences: List<Sequence>, selectedSequence: Long, onScroll: (Int) -> Unit) {
    GlanceTheme {
        Scaffold(horizontalPadding = 8.dp) {
            if (sequences.isEmpty())
                WidgetEmptyListMessage()
            else {
                val currentSequence = sequences.find { it.id == selectedSequence } ?: sequences.first()
                LaunchKnocksScroller(currentSequence, sequences.size > 1, onScroll)
            }
        }
    }
}

@Composable
private fun LaunchKnocksScroller(sequence: Sequence, canScroll: Boolean, onScroll: (Int) -> Unit) {
    val context = LocalContext.current
    val resources = context.resources
    val textColor = GlanceTheme.colors.onBackground
    val noName = remember { resources.getString(R.string.text_unnamed_sequence) }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = GlanceModifier.fillMaxSize()) {
        if (canScroll)
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.arrow_left_icon),
                contentDescription = null,
                onClick = { onScroll(-1) }
            )
        Box(modifier = GlanceModifier.defaultWeight().padding(8.dp).fillMaxHeight().clickable {
            launchKnocker(context, requireNotNull(sequence.id))
        }, contentAlignment = Alignment.Center) {
            Text(
                text = if (sequence.name.isNullOrBlank())
                    noName
                else
                    sequence.name,
                maxLines = 3,
                style = remember {
                    TextStyle(
                        textAlign = TextAlign.Center, color = textColor,
                        fontSize = 16.sp, fontWeight = FontWeight.Medium
                    )
                })
        }
        if (canScroll)
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.arrow_right_icon),
                contentDescription = null,
                onClick = { onScroll(1) }
            )
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 300, heightDp = 100)
@Composable
fun PreviewLaunchWidget() {
    LaunchKnocksWidgetContent(PreviewData.mockSequences, 1) {}
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 300, heightDp = 100)
@Composable
fun PreviewLaunchNoDataWidget() {
    LaunchKnocksWidgetContent(listOf(), 1) {}
}