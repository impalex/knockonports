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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import me.impa.knockonports.R
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun ReorderableCollectionItemScope.SequenceDragHandle(onDragEnded: () -> Unit) {
    val view = LocalView.current
    Icon(
        painter = painterResource(R.drawable.drag_indicator_icon),
        contentDescription = null,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .draggableHandle(
                onDragStarted = {
                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_START)
                },
                onDragStopped = {
                    ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_END)
                    onDragEnded()
                }
            )
    )

}