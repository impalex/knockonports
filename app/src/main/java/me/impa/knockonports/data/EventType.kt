/*
 * Copyright (c) 2020 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.impa.knockonports.data

import me.impa.knockonports.R
import me.impa.knockonports.ext.EnumCompanion

enum class EventType {
    UNKNOWN {
        override val resourceId: Int
            get() = R.string.log_unknown
    },
    KNOCK {
        override val resourceId: Int
            get() = R.string.log_knock
    },
    ERROR_NETWORK {
        override val resourceId: Int
            get() = R.string.log_error_network
    },
    ERROR_INVALID_HOST {
        override val resourceId: Int
            get() = R.string.log_error_invalid_host
    },
    ERROR_RESOLVE_HOST {
        override val resourceId: Int
            get() = R.string.log_error_resolve_host
    },
    ERROR_EMPTY_SEQUENCE {
        override val resourceId: Int
            get() = R.string.log_error_empty_sequence
    },
    ERROR_UNKNOWN {
        override val resourceId: Int
            get() = R.string.log_error_unknown
    },
    SEQUENCE_SAVED {
        override val resourceId: Int
            get() = R.string.log_sequence_saved
    },
    SEQUENCE_DELETED {
        override val resourceId: Int
            get() = R.string.log_sequence_deleted
    },
    EXPORT {
        override val resourceId: Int
            get() = R.string.log_export
    },
    IMPORT {
        override val resourceId: Int
            get() = R.string.log_import
    },
    ERROR_EXPORT {
        override val resourceId: Int
            get() = R.string.log_error_export
    },
    ERROR_IMPORT {
        override val resourceId: Int
            get() = R.string.log_error_import
    };

    abstract val resourceId: Int

    companion object : EnumCompanion<EventType>(values())
}