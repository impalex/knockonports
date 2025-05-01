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

package me.impa.knockonports.screen.component.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import me.impa.knockonports.R

private const val ANIMATION_DURATION = 300

@Composable
fun ExpandableIconButton(
    isExpanded: Boolean,
    onStateChanged: () -> Unit,
    modifier: Modifier = Modifier,
    expandedContentDescription: String? = null,
    collapsedContentDescription: String? = null
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(ANIMATION_DURATION)
    )

    IconButton(onClick = onStateChanged,modifier = modifier) {
        Icon(
            painterResource(R.drawable.expand_more_icon),
            contentDescription = if (isExpanded) expandedContentDescription else collapsedContentDescription,
            modifier = Modifier.rotate(rotationAngle)
        )
    }
}