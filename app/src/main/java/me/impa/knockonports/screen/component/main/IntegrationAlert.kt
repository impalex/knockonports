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

import android.content.ClipData
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.R
import me.impa.knockonports.constants.TAG_CLOSE_BUTTON

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrationAlert(id: Long, onDismiss: () -> Unit = {}) {
    val knockUri = "${BuildConfig.APP_SCHEME}://${BuildConfig.KNOCK_HOST}/$id"
    val clipboard = LocalClipboard.current
    val context = LocalContext.current

    AlertDialog(
        title = { Text(text = stringResource(R.string.title_integration_alert)) },
        text = {
            Column {
                Text(text = stringResource(R.string.text_integration_alert))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = knockUri, maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            clipboard.nativeClipboard.setPrimaryClip(ClipData.newPlainText("Knock Uri", knockUri))
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                                Toast.makeText(
                                    context, R.string.message_clipboard_confirmation,
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.action_copy))
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.testTag(TAG_CLOSE_BUTTON)) {
                Text(text = stringResource(R.string.action_close))
            }
        }
    )
}

@Suppress("MagicNumber")
@Preview
@Composable
fun PreviewIntegrationAlert() {
    IntegrationAlert(777)
}