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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.impa.knockonports.R

@Composable
fun ReviewRequestDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onPostpone: () -> Unit = {},
    onDecline: () -> Unit = {},
    onRateNow: () -> Unit = {}
) {

    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties()) {
        Surface(
            modifier = modifier.then(Modifier.sizeIn(280.dp, 360.dp)),
            tonalElevation = AlertDialogDefaults.TonalElevation,
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.title_review_request, stringResource(R.string.app_name)),
                    color = AlertDialogDefaults.titleContentColor,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = stringResource(R.string.text_review_request),
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = AlertDialogDefaults.textContentColor,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onDecline, modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.action_no_thanks))
                }
                TextButton(
                    onClick = onPostpone, modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.action_remind_later))
                }
                TextButton(
                    onClick = onRateNow, modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.action_rate_now))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewReviewRequestDialog() {
    ReviewRequestDialog()
}