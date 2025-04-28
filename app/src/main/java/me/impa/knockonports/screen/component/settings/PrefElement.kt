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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import me.impa.knockonports.R
import me.impa.knockonports.screen.component.common.DialogItemList
import me.impa.knockonports.screen.component.common.DialogItemsSource
import me.impa.knockonports.screen.component.common.DialogMenuItem
import me.impa.knockonports.ui.theme.themeMap
import me.impa.knockonports.ui.theme.variant.AppColorScheme
import timber.log.Timber
import kotlin.math.roundToInt


@Composable
fun PrefDescription(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                .wrapContentSize()
        )
        Text(
            subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .wrapContentSize()
        )
    }
}

@Composable
fun PrefDescriptionClickable(
    title: String, subtitle: String, modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null, onClick: () -> Unit = { }
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    PrefDescription(
        title = title, subtitle = subtitle,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = ripple())
                { onClick() })
    )
}

@Composable
fun PrefSwitch(
    title: String,
    description: String,
    value: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit = {}
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Row(
        modifier = modifier.clickable(interactionSource = interactionSource, indication = ripple()) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrefDescription(title = title, subtitle = description, modifier = Modifier.weight(1f))
        Switch(
            checked = value, onCheckedChange = { onClick() }, interactionSource = interactionSource,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun PrefStepSlider(
    title: String, description: String, value: Int, minValue: Int, maxValue: Int,
    modifier: Modifier = Modifier, steps: Int = maxValue - minValue, onChanged: (Int) -> Unit = {}
) {
    Column(modifier = modifier) {
        PrefDescription(title = title, subtitle = description, modifier = Modifier.fillMaxWidth())
        Slider(
            value = value.toFloat(),
            onValueChange = {
                it.roundToInt().takeIf { it != value }?.let { onChanged(it) }
            },
            valueRange = minValue.toFloat()..maxValue.toFloat(),
            steps = steps - 1,
            modifier = Modifier
                .systemGestureExclusion { coords ->
                    Rect(0f, 0f, (coords.size.width).toFloat(), (coords.size.height).toFloat())
                }
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )
    }
}


@Composable
fun PrefMultiSelection(
    title: String, value: String, map: ImmutableMap<String, String>,
    onChanged: (String) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val itemsSource = remember(map, value) {
        DialogItemsSource.ListItems(map.keys.toImmutableList(), map.keys.indexOf(value))
    }

    if (showDialog) {
        DialogItemList(
            onDismissRequest = { showDialog = false },
            itemsSource = itemsSource,
            selectedItemToString = { map[it].toString() },
            enableFilter = false,
            drawItem = { item, selected, enabled, image, onClick ->
                DialogMenuItem(
                    map[item].toString(),
                    selected, enabled, image, onClick
                )
            },
            onItemSelected = { index, item ->
                onChanged(item)
            }
        )
    }

    PrefDescriptionClickable(
        title = title,
        subtitle = map[value].toString(),
        interactionSource = interactionSource,
        onClick = { showDialog = true })

}

@Composable
fun PrefCustomProviderEditor(title: String, value: String, onChanged: (String) -> Unit = {}) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    if (showDialog)
        CustomServiceDialog(value, onDismissRequest = { showDialog = false }, onConfirm = {
            if (it != value)
                onChanged(it)
            showDialog = false
        })

    PrefDescriptionClickable(
        title, if (value.isNotBlank()) value else stringResource(R.string.text_custom_provider_not_specified),
        onClick = { showDialog = true })
}

private const val SELECTED_SCALE = 1.2f

@Composable
fun PrefColorSelection(
    title: String, description: String, value: String, modifier: Modifier = Modifier,
    onChanged: (String) -> Unit = {}
) {
    val themeKeys = remember(themeMap) { themeMap.keys.toImmutableList() }
    Column(modifier = modifier) {
        PrefDescription(title = title, subtitle = description, modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth()) {
            themeKeys.forEach {
                ColorItem(
                    it, themeMap[it]!!, value == it, onSelected = { onChanged(it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    tag: String, theme: AppColorScheme, isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit = {}
) {
    Box(
        modifier = modifier.then(
            Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = {
                        if (!isSelected)
                            onSelected(tag)
                    }
                )),
        contentAlignment = Alignment.Center) {

        val scale = if (isSelected) SELECTED_SCALE else 1f
        val animateScale by animateFloatAsState(
            targetValue = scale, label = "",
            animationSpec = spring()
        )
        DividedCircle(
            topColor = theme.colorPrimary,
            bottomLeftColor = theme.colorSecondary,
            bottomRightColor = theme.colorTetriary,
            outlineColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .scale(animateScale)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun PreviewPrefColorSelection() {
    PrefColorSelection(
        title = "Title",
        description = "Description",
        value = "SKY_STEEL",
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun PreviewPrefSwitch() {
    PrefSwitch(title = "Title", description = "Description", value = true, modifier = Modifier.fillMaxWidth())
}

@Preview
@Composable
fun PreviewPrefStepSlider() {
    PrefStepSlider(
        title = "Title", description = "Description", value = 1, minValue = 0, maxValue = 2,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun PreviewPrefDescription() {
    PrefMultiSelection(
        "Multi Selection", "1", persistentMapOf<String, String>("1" to "First Value", "2" to "Second Value"),
        onChanged = {})
}

@Preview
@Composable
fun PreviewConfigTextItem() {
    PrefDescription(title = "Title", subtitle = "Subtitle", modifier = Modifier.fillMaxWidth())
}
