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

package me.impa.knockonports.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import java.io.Serializable

class DeepLinkPattern<T : NavKey>(
    val serializer: KSerializer<T>,
    val uriPattern: Uri
) {

    class PathSegment(
        val stringValue: String,
        val isParamArg: Boolean,
        val typeParser: TypeParser
    )

    private val regexPatternFillIn = Regex("\\{(.+?)\\}")

    val pathSegments: List<PathSegment> by lazy {
        buildList {
            uriPattern.pathSegments.forEach { segment ->
                var result = regexPatternFillIn.find(segment)
                if (result != null) {
                    val argName = result.groups[1]!!.value
                    val elementIndex = serializer.descriptor.getElementIndex(argName)
                    val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
                    add(PathSegment(argName, true, getTypeParser(elementDescriptor.kind)))
                } else {
                    add(PathSegment(segment, false, getTypeParser(PrimitiveKind.STRING)))
                }
            }
        }
    }

    val queryValueParsers: Map<String, TypeParser> by lazy {
        buildMap {
            uriPattern.queryParameterNames.forEach { paramName ->
                val elementIndex = serializer.descriptor.getElementIndex(paramName)
                val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
                put(paramName, getTypeParser(elementDescriptor.kind))
            }
        }
    }

}

private typealias TypeParser = (String) -> Serializable

private fun getTypeParser(kind: SerialKind): TypeParser {
    return when (kind) {
        PrimitiveKind.STRING -> Any::toString
        PrimitiveKind.INT -> String::toInt
        PrimitiveKind.BOOLEAN -> String::toBoolean
        PrimitiveKind.BYTE -> String::toByte
        PrimitiveKind.CHAR -> String::toCharArray
        PrimitiveKind.DOUBLE -> String::toDouble
        PrimitiveKind.FLOAT -> String::toFloat
        PrimitiveKind.LONG -> String::toLong
        PrimitiveKind.SHORT -> String::toShort
        else -> throw IllegalArgumentException(
            "Unsupported argument type of SerialKind:$kind. The argument type must be a Primitive."
        )
    }
}