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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.R
import me.impa.knockonports.StartKnockingActivity
import me.impa.knockonports.constants.EXTRA_VALUE_SOURCE_WIDGET
import me.impa.knockonports.data.WidgetDataStore
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.screen.PreviewData
import java.io.File

class KnocksWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override val stateDefinition: GlanceStateDefinition<List<Sequence>>?
        get() = object : GlanceStateDefinition<List<Sequence>> {
            override suspend fun getDataStore(
                context: Context,
                fileKey: String
            ): DataStore<List<Sequence>> {
                return WidgetDataStore.get(context)
            }

            override fun getLocation(context: Context, fileKey: String): File {
                throw NotImplementedError()
            }

        }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            KnocksWidgetContent(currentState())
        }
    }
}

@Composable
private fun KnocksWidgetContent(sequences: List<Sequence>) {
    GlanceTheme {
        Scaffold(
            backgroundColor = GlanceTheme.colors.widgetBackground,
            titleBar = { if (showTitlebar()) WidgetTitleBar() else null },
            modifier = GlanceModifier.padding(
                bottom = 14.dp,
                top = if (showTitlebar()) 0.dp else 14.dp
            )
        ) {
            WidgetSequenceList(sequences)
        }
    }
}

@Composable
private fun WidgetTitleBar() {
    TitleBar(
        title = "Knock-knock",
        startIcon = ImageProvider(R.drawable.knocker_widget_icon),
        iconColor = GlanceTheme.colors.primary,
        textColor = GlanceTheme.colors.onSurface,
        modifier = GlanceModifier.clickable(
            onClick = actionStartActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "${BuildConfig.APP_SCHEME}://${BuildConfig.APP_HOST}/list".toUri()
                )
            )
        ),
        actions = {
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.add_icon),
                contentDescription = null,
                onClick = actionStartActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "${BuildConfig.APP_SCHEME}://${BuildConfig.APP_HOST}/sequence/null".toUri()
                    )
                ),
                contentColor = GlanceTheme.colors.secondary,
                backgroundColor = null
            )
        }
    )
}

@Composable
private fun WidgetSequenceList(sequences: List<Sequence>) {
    Box {
        if (sequences.isEmpty()) {
            NoData()
        } else {
            SequencesList(sequences)
        }
    }
}

@Composable
private fun SequencesList(sequences: List<Sequence>) {
    val context = LocalContext.current
    val resources = context.resources
    val noName = resources.getString(R.string.text_unnamed_sequence)
    LazyColumn {
        itemsIndexed(sequences) { index, item ->
            Column(modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                if (index > 0)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(GlanceTheme.colors.secondaryContainer)
                        .cornerRadius(8.dp)
                        .clickable {
                            item.id?.let { launchKnocker(context, it) }
                        }
                ) {
                    Text(
                        text = if (item.name.isNullOrBlank()) noName else item.name, maxLines = 1,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSecondaryContainer,
                            fontSize = 16.sp
                        ),
                        modifier = GlanceModifier.padding(horizontal = 8.dp, vertical = 16.dp)
                    )
                }

            }
        }
    }
}

@Composable
private fun NoData() {
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

@Composable
private fun showTitlebar(): Boolean =
    LocalSize.current.width > 180.dp

private fun launchKnocker(context: Context, sequenceId: Long) {
    val intent = Intent(context, StartKnockingActivity::class.java).apply {
        putExtra("EXTRA_SEQ_ID", sequenceId)
        putExtra("EXTRA_SOURCE", EXTRA_VALUE_SOURCE_WIDGET)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 200)
@Composable
fun PreviewWidget() {
    KnocksWidgetContent(PreviewData.mockSequences)
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 200)
@Composable
fun PreviewNoDataWidget() {
    KnocksWidgetContent(listOf())
}

