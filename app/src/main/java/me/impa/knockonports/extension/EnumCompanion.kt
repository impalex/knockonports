/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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

package me.impa.knockonports.extension

/**
 * Helper class to simplify retrieving enum values
 * @param V - enum type
 */
open class EnumCompanion<V : Enum<*>>(private val values: Array<V>, private val defaultValue: V? = null) {

    /**
     * Retrieve enum value by ordinal. If value not found returns [defaultValue]
     * or first enum value if [defaultValue] is null
     * @param ordinal ordinal
     * @return enum value
     *
     */
    fun fromOrdinal(ordinal: Int) = values.getOrElse(ordinal) { defaultValue ?: values[0] }
}
