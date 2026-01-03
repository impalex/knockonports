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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import me.impa.knockonports.R
import me.impa.knockonports.extension.debounced
import me.impa.knockonports.extension.navigate
import me.impa.knockonports.navigation.ColorPickerRoute
import me.impa.knockonports.navigation.NavigateUp
import me.impa.knockonports.screen.component.common.LocalAppEventBus
import me.impa.knockonports.screen.component.common.LocalInnerPaddingValues
import me.impa.knockonports.screen.component.common.RegisterAppBar
import me.impa.knockonports.screen.viewmodel.ColorPickerViewModel
import me.impa.knockonports.screen.viewmodel.state.colorpicker.ColorResult

@Composable
fun ColorPickerScreen(
    resultChannel: String, showAlpha: Boolean,
    viewModel: ColorPickerViewModel, modifier: Modifier = Modifier
) {

    val controller = rememberColorPickerController()
    val color by viewModel.selectedColor.collectAsState()
    val paddings = LocalInnerPaddingValues.current

    LaunchedEffect(Unit) {
        @Suppress("MagicNumber")
        controller.getColorFlow(200).collect { envelope ->
            if (envelope.fromUser)
                viewModel.updateColor(envelope.color)
        }
    }
    val bus = LocalAppEventBus.current

    RegisterAppBar<ColorPickerRoute>(title = "", showBackButton = true) {
        if (viewModel.defaultColor != Color.Unspecified.toColorLong())
            IconButton(onClick = debounced(onClick = {
                controller.selectByColor(Color.fromColorLong(viewModel.defaultColor), true)
            })) {
                Icon(painter = painterResource(R.drawable.reset_iso_icon), contentDescription = null)
            }
        Button(
            onClick = debounced(onClick = {
                val newColor = if (viewModel.defaultColor == color.toColorLong()) Color.Unspecified else color
                bus.sendEvent(resultChannel, ColorResult(newColor))
                bus.navigate(NavigateUp)
            }),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = null)
        }
    }

    ColorPickerScreenContent(
        color = color,
        showAlpha = showAlpha,
        controller = controller,
        modifier = modifier.then(Modifier.padding(paddings))
    )
}

@Composable
fun ColorPickerScreenContent(
    color: Color,
    showAlpha: Boolean,
    controller: ColorPickerController, modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        controller.selectByColor(color, false)
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Surface(modifier = modifier.then(Modifier.fillMaxSize())) {
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
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
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
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
            }
        }
    }
}

@Composable
fun ColorSliders(showAlpha: Boolean, controller: ColorPickerController) {
    Column {
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .height(32.dp)
                .systemGestureExclusion(),
            controller = controller
        )
        if (showAlpha)
            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .height(32.dp)
                    .systemGestureExclusion(),
                controller = controller
            )
    }
}

@Composable
fun ColorPalette(controller: ColorPickerController, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            controller = controller
        )
    }
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
    ColorPickerScreenContent(
        color = Color.Blue,
        showAlpha = true,
        controller = rememberColorPickerController(),
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(name = "Landscape", widthDp = 800, heightDp = 390)
@Composable
fun PreviewColorPickerScreenLandscape() {
    ColorPickerScreenContent(
        color = Color.Blue,
        showAlpha = true,
        controller = rememberColorPickerController(),
        modifier = Modifier.fillMaxSize()
    )
}
