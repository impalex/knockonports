/*
 * Copyright (c) 2026 Alexander Yaburov
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

package me.impa.knockonports.screen

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.impa.knockonports.R
import me.impa.knockonports.extension.navigate
import me.impa.knockonports.navigation.NavigateUp
import me.impa.knockonports.screen.component.common.LocalAppEventBus
import me.impa.knockonports.screen.viewmodel.ColorPickerViewModel
import me.impa.knockonports.screen.viewmodel.state.colorpicker.ColorResult
import me.impa.knockonports.ui.theme.KnockOnPortsTheme

@Composable
fun ColorPickerScreen(
    resultChannel: String, showAlpha: Boolean,
    viewModel: ColorPickerViewModel, modifier: Modifier = Modifier
) {

    val controller = rememberColorPickerController()
    val color by viewModel.selectedColor.collectAsState()

    LaunchedEffect(Unit) {
        @Suppress("MagicNumber")
        controller.getColorFlow(200).collect { envelope ->
            if (envelope.fromUser)
                viewModel.updateColor(envelope.color)
        }
    }
    val bus = LocalAppEventBus.current

    val onDefault = remember { { controller.selectByColor(Color.fromColorLong(viewModel.defaultColor), true) } }
    val onSave = remember {
        {
            val newColor = if (viewModel.defaultColor == color.toColorLong()) Color.Unspecified else color
            bus.sendEvent(resultChannel, ColorResult(newColor))
            bus.navigate(NavigateUp)
        }
    }

    ColorPickerScreenContent(
        color = color,
        showAlpha = showAlpha,
        controller = controller,
        showDefaultButton = viewModel.defaultColor != Color.Unspecified.toColorLong(),
        onSave = onSave,
        onDefault = onDefault,
        modifier = modifier.then(Modifier.safeContentPadding()),
    )
}

@Composable
fun ColorPickerScreenContent(
    color: Color,
    showAlpha: Boolean,
    showDefaultButton: Boolean,
    controller: ColorPickerController, modifier: Modifier = Modifier,
    onDefault: () -> Unit = {},
    onSave: () -> Unit = {}

) {
    LaunchedEffect(Unit) {
        controller.selectByColor(color, false)
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Surface(modifier = modifier.then(Modifier.fillMaxSize()), shape = AlertDialogDefaults.shape,
        color = AlertDialogDefaults.containerColor) {
        if (isLandscape) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        ColorPreview(
                            controller, modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                        ColorSliders(showAlpha = showAlpha, controller = controller)
                    }
                    ColorPalette(
                        controller, modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                ButtonRow(showDefaultButton = showDefaultButton, onDefault = onDefault, onSave = onSave)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
            ) {
                ColorPreview(
                    controller, modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                ColorSliders(showAlpha = showAlpha, controller = controller)
                ColorPalette(
                    controller, modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                ButtonRow(showDefaultButton = showDefaultButton, onDefault = onDefault, onSave = onSave)
            }
        }
    }

}

@Composable
private fun ButtonRow(
    showDefaultButton: Boolean,
    onDefault: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (showDefaultButton) {
            Button(
                onClick = onDefault
            ) {
                Text(text = stringResource(R.string.action_default))
            }
        }
        Button(onClick = onSave) {
            Text(text = stringResource(R.string.action_save))
        }
    }
}

@Composable
fun ColorSliders(showAlpha: Boolean, controller: ColorPickerController) {

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val paddings = if (isLandscape)
        PaddingValues(top = 16.dp)
    else
        PaddingValues(bottom = 16.dp)

    Column {
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddings)
                .height(32.dp)
                .systemGestureExclusion(),
            controller = controller
        )
        if (showAlpha)
            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddings)
                    .height(32.dp)
                    .systemGestureExclusion(),
                controller = controller
            )
    }
}

@Composable
fun ColorPalette(controller: ColorPickerController, modifier: Modifier = Modifier) {
    HsvColorPicker(
        modifier = modifier,
        controller = controller
    )
}

@Composable
fun ColorPreview(controller: ColorPickerController, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AlphaTile(
            modifier = Modifier
                .size(96.dp)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline),
            controller = controller
        )
    }
}

@Preview(name = "Portrait")
@Composable
fun PreviewColorPickerScreenPortrait() {
    KnockOnPortsTheme {
        ColorPickerScreenContent(
            color = Color.Blue,
            showAlpha = true,
            showDefaultButton = true,
            controller = rememberColorPickerController(),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(name = "Landscape", widthDp = 800, heightDp = 390)
@Composable
fun PreviewColorPickerScreenLandscape() {
    KnockOnPortsTheme {
        ColorPickerScreenContent(
            color = Color.Blue,
            showAlpha = true,
            showDefaultButton = true,
            controller = rememberColorPickerController(),
            modifier = Modifier.fillMaxSize()
        )
    }
}
