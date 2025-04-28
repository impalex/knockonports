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

package me.impa.knockonports.screen.validate

import androidx.core.text.isDigitsOnly
import me.impa.knockonports.R
import me.impa.knockonports.data.type.TextResource

class NumberValidator : Validator<String?> {
    override fun validate(value: String?): ValidationResult =
        if (value.isNullOrEmpty() || value.isDigitsOnly()) ValidationResult.Valid
        else ValidationResult.Invalid(TextResource.DynamicText(R.string.text_error_number_only))
}