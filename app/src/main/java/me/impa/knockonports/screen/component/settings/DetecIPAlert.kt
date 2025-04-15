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

package me.impa.knockonports.screen.component.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import me.impa.knockonports.R
import me.impa.knockonports.extension.debounced

@Composable
fun DetectIPAlert(onDismiss: () -> Unit = {}, onConfirm: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.title_public_ip_alert)) },
        text = {
            Column {
                Text(text = stringResource(R.string.text_public_ip_alert_1))
                Text(text = stringResource(R.string.text_public_ip_alert_2), fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.text_public_ip_alert_3))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
        confirmButton = {
            Button(onClick = debounced(onConfirm)) {
                Text(text = stringResource(R.string.action_allow))
            }
        }
    )
}

@Preview
@Composable
fun PreviewDetectIPAlert() {
    DetectIPAlert()
}