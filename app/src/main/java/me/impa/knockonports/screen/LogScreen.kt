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

package me.impa.knockonports.screen

import android.content.res.Resources
import android.icu.text.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import me.impa.knockonports.R
import me.impa.knockonports.data.db.entity.LogEntry
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.extension.OnDestination
import me.impa.knockonports.extension.stringResourceId
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.navigation.AppNavGraph
import me.impa.knockonports.screen.viewmodel.LogViewModel

@Composable
fun LogScreen(
    onComposing: (AppBarState) -> Unit, navController: NavController,
    modifier: Modifier = Modifier, viewModel: LogViewModel = hiltViewModel()
) {
    val title = stringResource(R.string.title_screen_log)
    navController.OnDestination<AppNavGraph.LogRoute> {
        LaunchedEffect(true) {
            onComposing(
                AppBarState(
                    title = title,
                    backAvailable = true,
                    actions = {
                        IconButton(onClick = { viewModel.clearLog() }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                )
            )
        }
    }
    val events by viewModel.logEntries.collectAsState()

    LogEventList(events, modifier)
}

val errorEventTypes = setOf(
    EventType.ERROR_IMPORT,
    EventType.ERROR_EXPORT,
    EventType.ERROR_UNKNOWN,
    EventType.ERROR_NETWORK,
    EventType.ERROR_INVALID_HOST,
    EventType.ERROR_RESOLVE_HOST,
    EventType.ERROR_EMPTY_SEQUENCE,
)

fun stringResourceSafe(resources: Resources, id: Int, vararg formatArgs: Any): String {
    return try {
        resources.getString(id, *formatArgs)
    } catch (e: Exception) {
        e.message.toString()
    }
}

@Suppress("SpreadOperator") // Like... seriously?
@Composable
fun stringResourceSafe(id: Int, formatArgs: List<Any?>): String {
    val args = formatArgs.map { it ?: "" }.toTypedArray()
    val resources = LocalView.current.resources
    return stringResourceSafe(resources, id, *args)
}

@Composable
fun LogEventList(events: List<LogEntry>, modifier: Modifier = Modifier) {
    val lazyState = rememberLazyListState()
    LazyColumn(state = lazyState, modifier = modifier) {
        itemsIndexed(items = events, key = { _, item -> item.id ?: 0L }) { index, item ->
            if (index > 0)
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Column(modifier = Modifier.fillMaxWidth()) {
                val date = DateFormat.getDateTimeInstance().format(item.date ?: 0L)
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth()
                ) {
                    Text(
                        style = MaterialTheme.typography.bodySmall,
                        text = date, modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (item.event in errorEventTypes) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.surface
                        )
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResourceSafe(
                            item.event?.stringResourceId() ?: R.string.log_unknown,
                            item.data ?: listOf()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (item.event in errorEventTypes) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLogEventList() {
    LogEventList(
        PreviewData.mockLogEntries,
        modifier = Modifier.fillMaxSize()
    )
}