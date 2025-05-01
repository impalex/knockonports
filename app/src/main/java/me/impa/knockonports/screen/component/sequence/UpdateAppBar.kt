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

package me.impa.knockonports.screen.component.sequence

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import me.impa.knockonports.R
import me.impa.knockonports.extension.OnDestination
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.navigation.AppNavGraph

@Composable
fun NavController.UpdateAppBar(onComposing: (AppBarState) -> Unit, saveSequence: () -> Unit) {
    OnDestination<AppNavGraph.SequenceRoute> {
        val title = stringResource(R.string.title_screen_sequence)
        LaunchedEffect(true) {
            onComposing(
                AppBarState(
                    title = title,
                    backAvailable = true,
                    actions = {
                        Button(
                            onClick = debounced(saveSequence),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = null)
                        }
                    }
                )
            )
        }
    }
}

