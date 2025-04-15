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

package me.impa.knockonports.screen.component.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.FlowPreview
import me.impa.knockonports.extension.debounced

@OptIn(FlowPreview::class)
@Composable
fun ValueTextField(
    label: String, value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = { },
    keyboardType: KeyboardType = KeyboardType.Companion.Text
) {
    val currentValue = remember(key1 = value) { mutableStateOf(value) }

    currentValue.debounced {
        onValueChange(it)
    }

    OutlinedTextField(
        value = currentValue.value,
        onValueChange = {
            currentValue.value = it
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = keyboardType
        ),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ValueTextField(
    label: String, value: Int?,
    modifier: Modifier = Modifier,
    onValueChange: (Int?) -> Unit = { },
    onValidate: (Int) -> Boolean = { true },
    keyboardType: KeyboardType = KeyboardType.Companion.Number
) {
    ValueTextField(label, value?.toString() ?: "", modifier, { newValue ->
        if (newValue == "") {
            onValueChange(null)
        } else {
            newValue.toIntOrNull()?.takeIf { onValidate(it) }?.let { onValueChange(it) }
        }
    }, keyboardType)
}