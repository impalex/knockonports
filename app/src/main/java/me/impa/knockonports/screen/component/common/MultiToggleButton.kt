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

@file:Suppress("LongParameterList")

package me.impa.knockonports.screen.component.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val ANIMATION_DURATION = 300

@Composable
fun MultiToggleButton(
    states: List<String>,
    initialStateIndex: Int = -1,
    orientation: MultiToggleButtonOrientation = MultiToggleButtonOrientation.HORIZONTAL,
    selectedTint: Color = MaterialTheme.colorScheme.primary,
    unselectedTint: Color = MaterialTheme.colorScheme.background,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onBackground,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    selectedStateDescription: String = "Selected",
    onStateChanged: (Int) -> Unit = {}
) {
    if (states.isEmpty())
        return

    var selectedStateIndex by rememberSaveable { mutableIntStateOf(initialStateIndex) }

    Layout(
        content = {
            MultiToggleButtonContent(
                states = states,
                selectedStateIndex = selectedStateIndex,
                orientation = orientation,
                selectedTint = selectedTint,
                unselectedTint = unselectedTint,
                selectedTextColor = selectedTextColor,
                unselectedTextColor = unselectedTextColor,
                borderColor = borderColor,
                selectedStateDescription = selectedStateDescription,
                onStateChanged = { newIndex ->
                    selectedStateIndex = newIndex
                    onStateChanged(newIndex)
                }
            )
        },
        measurePolicy = rememberEqualSizeMeasurePolicy(orientation),
        modifier = Modifier
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
    )
}

@Composable
private fun MultiToggleButtonContent(
    states: List<String>,
    selectedStateIndex: Int = -1,
    orientation: MultiToggleButtonOrientation,
    selectedTint: Color,
    unselectedTint: Color,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    borderColor: Color,
    selectedStateDescription: String,
    onStateChanged: (Int) -> Unit
) {

    states.forEachIndexed { index, state ->
        val isSelected = index == selectedStateIndex
        val textColor by animateColorAsState(
            targetValue = if (isSelected) selectedTextColor else unselectedTextColor,
            label = "textColorAnimation",
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )
        val backgroundTint by animateColorAsState(
            targetValue = if (isSelected) selectedTint else unselectedTint,
            label = "backgroundColorAnimation",
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )

        if (index != 0) {
            when (orientation) {
                MultiToggleButtonOrientation.HORIZONTAL ->
                    VerticalDivider(thickness = 1.dp, color = borderColor)

                MultiToggleButtonOrientation.VERTICAL ->
                    HorizontalDivider(thickness = 1.dp, color = borderColor)
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .semantics {
                    role = Role.RadioButton
                    contentDescription = state
                    if (isSelected) {
                        stateDescription = selectedStateDescription
                    }
                }
                .background(backgroundTint)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = {
                        if (!isSelected)
                            onStateChanged(index)
                    })
        ) {
            BasicText(
                state, color = { textColor }, softWrap = false, modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .requiredWidth(IntrinsicSize.Max)
            )
        }
    }
}

@Composable
private fun rememberEqualSizeMeasurePolicy(orientation: MultiToggleButtonOrientation): MeasurePolicy {
    return remember (key1 = orientation) {
        MeasurePolicy { measurables, constraints ->
            // Measure each children
            val maxHeight = measurables.maxOf { it.minIntrinsicHeight(constraints.maxHeight) }
            val maxWidth = measurables.maxOf { it.minIntrinsicWidth(constraints.maxWidth) }
            val baseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            // Equalize the size of children
            val updatedConstraints = constraints.copy(
                minWidth = maxWidth,
                maxWidth = maxWidth,
                minHeight = maxHeight,
                maxHeight = maxHeight
            )

            // Initial values for width & height
            var width = if (orientation == MultiToggleButtonOrientation.VERTICAL) maxWidth else 0
            var height = if (orientation == MultiToggleButtonOrientation.HORIZONTAL) maxHeight else 0
            var placeables = measurables.mapIndexed { index, measurable ->
                // Use baseConstraints for odd components (it's dividers), updatedConstraints for even components
                val placeable = measurable.measure(if (index % 2 == 1) baseConstraints else updatedConstraints)
                if (orientation == MultiToggleButtonOrientation.HORIZONTAL) {
                    width += placeable.width
                } else {
                    height += placeable.height
                }
                placeable
            }

            var x = 0
            var y = 0
            // Place children
            layout(width, height) {
                placeables.forEach {
                    it.placeRelative(x, y)
                    if (orientation == MultiToggleButtonOrientation.HORIZONTAL) {
                        x += it.width
                    } else {
                        y += it.height
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHorizontalMultiToggleButton() {
    MultiToggleButton(
        listOf("Item 1", "Item 2", "Item 3"),
        1,
        MultiToggleButtonOrientation.HORIZONTAL
    )
}

@Preview
@Composable
fun PreviewVerticalMultiToggleButton() {
    MultiToggleButton(
        listOf("Item 1", "Item 2", "Item 3"),
        1,
        MultiToggleButtonOrientation.VERTICAL
    )
}

enum class MultiToggleButtonOrientation {
    HORIZONTAL,
    VERTICAL
}

