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

import android.content.Context
import android.os.Handler
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import me.impa.knockonports.App
import me.impa.knockonports.R
import org.jetbrains.anko.contentView

object HintManager {

    enum class Hint {
        DELETE_ROW,
        CHECK_ICMP_SIZE
    }

    private var HintMap = Hint.values().associate { it to null as Boolean? }.toMutableMap()
    private val HintResources = mapOf(
            Hint.DELETE_ROW to R.string.hint_delete_step,
            Hint.CHECK_ICMP_SIZE to R.string.hint_icmp_size
    )


    private fun loadHintState(context: Context, hint: Hint): Boolean {
        val state = AppPrefs.getHintState(context, hint)
        HintMap[hint] = state
        return state
    }

    private fun isHintShown(context: Context, hint: Hint): Boolean =
            HintMap[hint] ?: loadHintState(context, hint)

    fun showHint(context: Context, hint: Hint) {
        if (!HintResources.containsKey(hint) || isHintShown(context, hint))
            return

        val view = (context.applicationContext as App).currentActivity?.contentView ?: return

        HintMap[hint] = true
        Handler().postDelayed({
            Snackbar.make(view, HintResources.getValue(hint), Snackbar.LENGTH_LONG)
                    .setAction(R.string.got_it) {
                        AppPrefs.markHintAsShown(context, hint)
                    }.apply {
                        getView().findViewById<TextView>(R.id.snackbar_text)?.setTextColor(ContextCompat.getColor(context, R.color.colorSnackbarText))
                    }.show()
        }, 500)
    }
}