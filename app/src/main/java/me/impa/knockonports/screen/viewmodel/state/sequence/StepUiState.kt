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

package me.impa.knockonports.screen.viewmodel.state.sequence

import me.impa.knockonports.data.model.SequenceStep
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.SequenceStepType
import me.impa.knockonports.screen.validate.ValidationResult

data class StepUiState(
    val type: SequenceStepType = SequenceStepType.UDP,
    val port: Int? = null,
    val icmpSize: Int? = null,
    val icmpCount: Int? = null,
    val content: String? = null,
    val encoding: ContentEncodingType = ContentEncodingType.RAW,
    val id: Int,
    val portValidation: ValidationResult = ValidationResult.Valid
)

fun SequenceStep.toStepUiState(id: Int) = StepUiState(
    id = id,
    type = type ?: SequenceStepType.UDP,
    port = port,
    icmpSize = icmpSize,
    icmpCount = icmpCount,
    content = content,
    encoding = encoding ?: ContentEncodingType.RAW
)

fun StepUiState.toSequenceStep() = SequenceStep(
    type = type,
    port = port,
    icmpSize = icmpSize,
    icmpCount = icmpCount,
    content = content,
    encoding = encoding
)