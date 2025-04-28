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

package me.impa.knockonports.data.type

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A sealed class representing a text resource that can be either a plain string or a dynamic string
 * obtained from string resources.
 *
 * This class provides a way to handle different types of text in a unified manner, allowing for
 * both static text and text that requires string resource resolution with potential arguments.
 */
@Suppress("SpreadOperator")
sealed class TextResource {
    data class PlainText(val text: String) : TextResource()
    class DynamicText(@StringRes val resId: Int, vararg val args: Any) : TextResource()

    @Composable
    fun asString() = when (this) {
        is PlainText -> text
        is DynamicText -> stringResource(resId, *args)
    }

    fun asString(context: Context) = when (this) {
        is PlainText -> text
        is DynamicText -> context.getString(resId, *args)
    }
}