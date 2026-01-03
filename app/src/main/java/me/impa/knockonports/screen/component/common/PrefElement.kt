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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.systemGestureExclusion
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import me.impa.knockonports.R
import me.impa.knockonports.extension.navigate
import me.impa.knockonports.navigation.ColorPickerRoute
import me.impa.knockonports.screen.component.settings.CustomServiceDialog
import me.impa.knockonports.screen.viewmodel.state.colorpicker.ColorResult
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi


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
            onValueChange = { newValue ->
                newValue.roundToInt().takeIf { it != value }?.let { onChanged(it) }
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
        title, value.ifBlank { stringResource(R.string.text_custom_provider_not_specified) },
        onClick = { showDialog = true })
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun PrefCustomColorSelection(
    title: String, subtitle: String, value: Color,
    defaultValue: Color,
    showAlpha: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    onChanged: (Color) -> Unit = {}
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val uid = remember(title) { title.hashCode().toString() }

    ResultEventEffect<ColorResult>(key = uid) {
        onChanged(it.color)
    }
    val bus = LocalAppEventBus.current

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier.then(
            Modifier.clickable(interactionSource = interactionSource, onClick = {
                bus.navigate(
                    ColorPickerRoute(
                        value.toColorLong(),
                        defaultValue.toColorLong(),
                        showAlpha,
                        uid
                    )
                )
            })
        )
    ) {
        PrefDescription(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(value)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = CircleShape)
        ) {
        }
    }
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
        "Multi Selection", "1", persistentMapOf("1" to "First Value", "2" to "Second Value"),
        onChanged = {})
}

@Preview
@Composable
fun PreviewConfigTextItem() {
    PrefDescription(title = "Title", subtitle = "Subtitle", modifier = Modifier.fillMaxWidth())
}

@Preview
@Composable
fun PreviewCustomColorSelection() {
    PrefCustomColorSelection(
        title = "Title", subtitle = "Description", value = Color.Green,
        defaultValue = Color.Unspecified,
        showAlpha = true,
        modifier = Modifier.fillMaxWidth()
    )
}