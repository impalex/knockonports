/*
 * Copyright (c) 2019 Alexander Yaburov
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

package me.impa.knockonports.util

import android.util.Log

interface Logging {
    val logTag: String
        get() = getTag(javaClass)
}

fun Logging.error(message: Any?, thr: Throwable? = null) {
    log(this, message, thr, Log.ERROR,
            { tag, msg -> Log.e(tag, msg) },
            { tag, msg, th -> Log.e(tag, msg, th) })
}

fun Logging.warn(message: Any?, thr: Throwable? = null) {
    log(this, message, thr, Log.WARN,
            { tag, msg -> Log.w(tag, msg) },
            { tag, msg, th -> Log.w(tag, msg, th) })
}

inline fun Logging.warn(message: () -> Any?) {
    val tag = logTag
    if (Log.isLoggable(tag, Log.WARN))
        Log.w(tag, message()?.toString() ?: "null")
}

inline fun Logging.info(message: () -> Any?) {
    val tag = logTag
    if (Log.isLoggable(tag, Log.INFO))
        Log.i(tag, message()?.toString() ?: "null")
}

inline fun Logging.debug(message: () -> Any?) {
    val tag = logTag
    if (Log.isLoggable(tag, Log.DEBUG))
        Log.d(tag, message()?.toString() ?: "null")
}

private inline fun log(logger: Logging, message: Any?, thr: Throwable?, level: Int, f: (String, String) -> Unit, fThrowable: (String, String, Throwable) -> Unit) {
    val tag = logger.logTag
    if (Log.isLoggable(tag, level)) {
        if (thr != null) {
            fThrowable(tag, message?.toString() ?: "null", thr)
        } else {
            f(tag, message?.toString() ?: "null")
        }
    }
}

private fun getTag(clazz: Class<*>): String {
    val tag = clazz.simpleName
    return tag.substring(0, 23.coerceAtMost(tag.length))
}