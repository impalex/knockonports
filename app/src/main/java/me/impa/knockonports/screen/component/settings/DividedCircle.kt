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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DividedCircle(
    topColor: Color,
    bottomLeftColor: Color,
    bottomRightColor: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier,
    outlineWidth: Float = 4f
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width.coerceAtMost(size.height) / 2 - outlineWidth / 2

        drawArc(
            color = topColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        drawArc(
            color = bottomLeftColor,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        drawArc(
            color = bottomRightColor,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        drawCircle(
            color = outlineColor,
            radius = radius,
            center = center,
            style = Stroke(width = outlineWidth)
        )

    }
}

@Preview
@Composable
fun PreviewDividedCircle() {
    DividedCircle(
        topColor = Color.Red,
        bottomLeftColor = Color.Green,
        bottomRightColor = Color.Blue,
        outlineColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(8.dp).size(32.dp)
    )
}