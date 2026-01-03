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

package me.impa.knockonports.screen.viewmodel

import androidx.annotation.ColorLong
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel(assistedFactory = ColorPickerViewModel.ColorPickerViewModelFactory::class)
class ColorPickerViewModel @AssistedInject constructor(
    @Assisted("color") @ColorLong color: Long,
    @Assisted("defaultColor") @ColorLong val defaultColor: Long
) : ViewModel() {

    private val _selectedColor = MutableStateFlow(Color.fromColorLong(color))
    val selectedColor: StateFlow<Color> = _selectedColor

    fun updateColor(color: Color) {
        _selectedColor.value = color
    }

    @AssistedFactory
    interface ColorPickerViewModelFactory {
        fun create(@Assisted("color") @ColorLong color: Long,
                   @Assisted("defaultColor") @ColorLong defaultColor: Long): ColorPickerViewModel
    }
}