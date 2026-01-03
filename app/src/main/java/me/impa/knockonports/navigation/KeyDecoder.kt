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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class KeyDecoder(
    private val arguments: Map<String, Any>
) : AbstractDecoder() {

    private var elementIndex: Int = -1
    private var elementName: String = ""

    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        var currentIndex = elementIndex
        while(true) {
            currentIndex++
            if (currentIndex>=descriptor.elementsCount)
                return CompositeDecoder.DECODE_DONE
            val currentName = descriptor.getElementName(currentIndex)
            if (arguments.contains(currentName)) {
                elementName = currentName
                elementIndex = currentIndex
                return elementIndex
            }
        }
    }

    override fun decodeValue(): Any {
        val arg = arguments[elementName]
        checkNotNull(arg) { "Argument $elementName not found" }
        return arg
    }

    override fun decodeNull(): Nothing? = null

    override fun decodeNotNullMark(): Boolean = arguments[elementName] != null
}