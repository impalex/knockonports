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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import me.impa.knockonports.R
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent

@Composable
fun IPHeaderSizeAlert(onEvent: (UiEvent) -> Unit = {}) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = { onEvent(UiEvent.ClearOverlay) },
        title = { Text(text = stringResource(R.string.title_custom_ip4hdr_alert)) },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(text = AnnotatedString.fromHtml(stringResource(R.string.text_custom_ip4hdr_alert)))
            }
        },
        dismissButton = {
            Button(onClick = { onEvent(UiEvent.ClearOverlay) }) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = debounced({ onEvent(UiEvent.ConfirmCustomIPHeaderSizeEnabled) }),
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = stringResource(R.string.action_understand).uppercase())
            }
        }
    )
}

@Preview
@Composable
fun PreviewIPHeaderSizeAlert() {
    IPHeaderSizeAlert()
}