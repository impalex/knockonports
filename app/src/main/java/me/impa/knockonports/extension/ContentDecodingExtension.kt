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

import me.impa.knockonports.constants.RADIX_BASE16
import me.impa.knockonports.data.type.ContentEncodingType
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


/**
 * Decodes a string based on the specified [ContentEncodingType].
 *
 * This function attempts to decode the provided string [data] using the decoding scheme
 * indicated by `this` [ContentEncodingType].
 * It handles different encoding types (RAW, BASE64, HEX) and gracefully falls back to an empty byte array
 * if decoding fails.
 *
 * @param data The string to decode, which can be null.  If null, it's treated as an empty string for RAW and BASE64,
 *             and results in an empty ByteArray for HEX.
 * @return The decoded byte array.  Returns an empty byte array if [data] is null and the encoding type is HEX,
 *         if decoding fails due to invalid input, or if the encoding type is RAW and [data] is null.
 *
 *  For RAW encoding, the string is directly converted to a byte array.
 *  For BASE64 encoding, the string is decoded using Base64 with optional padding.
 *  For HEX encoding, the string is interpreted as a hexadecimal representation of bytes.
 */
@OptIn(ExperimentalEncodingApi::class)
fun ContentEncodingType.decode(data: String?) = when (this) {
    // NOTE: silent fallback to empty ByteArray is intended behaviour
    ContentEncodingType.RAW -> (data ?: "").toByteArray()
    ContentEncodingType.BASE64 -> try {
        Base64.Default.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL).decode((data ?: "").toByteArray())
    } catch (_: Exception) {
        byteArrayOf()
    }
    ContentEncodingType.HEX -> try {
        if (data == null) {
            byteArrayOf()
        } else {
            ByteArray(data.length / 2) {
                data.substring(it * 2, it * 2 + 2).toInt(RADIX_BASE16).toByte()
            }
        }
    } catch (_: Exception) {
        byteArrayOf()
    }
}
