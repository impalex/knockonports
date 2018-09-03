/*
 * Copyright (c) 2018 Alexander Yaburov
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

package me.impa.knockonports.ext

import android.app.AlertDialog
import com.obsez.android.lib.filechooser.ChooserDialog
import java.io.File

// Hackish... Whatever.
inline fun <reified T> getHiddenField(obj: Any, field: String): T? {
    return try {
        val f = obj.javaClass.getDeclaredField(field)
        f.isAccessible = true
        val v = f.get(obj)
        if (v is T) { v } else { null }
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

fun ChooserDialog.getCurrentDir(): File? = getHiddenField(this, "_currentDir")

fun ChooserDialog.getAlertDialog(): AlertDialog? = getHiddenField(this, "_alertDialog")