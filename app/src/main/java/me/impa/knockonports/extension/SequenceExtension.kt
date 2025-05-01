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

package me.impa.knockonports.extension

import me.impa.knockonports.data.db.entity.Sequence

/**
 * Converts a sequence of steps into a string representation.
 *
 * This function filters the sequence to include only valid steps, then joins their descriptions into a single string.
 * If the resulting string is empty, it returns null.
 *
 * @return A string representation of the sequence, or null if the sequence is empty or contains no valid steps.
 */
fun Sequence.sequenceString() =
    steps?.filter { it.isValid() }?.joinToString { it.description() }?.ifBlank { null }
