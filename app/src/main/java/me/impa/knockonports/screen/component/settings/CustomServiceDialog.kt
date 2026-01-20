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

package me.impa.knockonports.screen.component.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.impa.knockonports.R
import me.impa.knockonports.ui.theme.KnockOnPortsTheme

@Composable
fun CustomServiceDialog(initialValue: String, onDismissRequest: () -> Unit = {}, onConfirm: (String) -> Unit = {}) {
    var url = rememberSaveable { initialValue }
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = stringResource(R.string.title_custom_service_dialog)) },
        text = {
            Column(modifier = Modifier.wrapContentHeight()) {
                Text(
                    text = AnnotatedString.fromHtml(stringResource(R.string.text_custom_service_dialog))
                )
                OutlinedTextField(
                    value = url,
                    label = { Text(stringResource(R.string.field_custom_ip_provider)) },
                    onValueChange = { url = it },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(url) }) {
                Text(text = stringResource(R.string.action_confirm))
            }
        }
    )
}

@Preview
@Composable
fun PreviewCustomServiceDialog() {
    KnockOnPortsTheme {
        CustomServiceDialog("https://localhost.domain")
    }
}