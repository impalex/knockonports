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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import me.impa.knockonports.R

@Composable
fun DonateAlert(onDismiss: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.title_donate_alert)) },
        text = {
            val scrollState = rememberScrollState()
            Text(
                text = AnnotatedString.fromHtml(stringResource(R.string.text_donate_alert)),
                modifier = Modifier.verticalScroll(scrollState)
            )
        },
        confirmButton = {
            val handler = LocalUriHandler.current
            Button(onClick = {
                onDismiss()
                handler.openUri("https://pay.cloudtips.ru/p/57c93be7")
            }) {
                Text(text = stringResource(R.string.action_support))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_not_now))
            }
        }
    )
}

@Preview
@Composable
fun PreviewDonateAlert() {
    DonateAlert()
}