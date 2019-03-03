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

package me.impa.knockonports.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import me.impa.knockonports.R
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

class RateAppFragment: DialogFragment(), AnkoLogger {

    var onDismiss: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_ask_review, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_rate_now).setOnClickListener {
            openPlayMarket(context!!)
            dismiss()
        }

        view.findViewById<Button>(R.id.button_rate_disable).setOnClickListener {
            turnOffAskReviewDialog(context!!)
            dismiss()
        }

        view.findViewById<Button>(R.id.button_rate_later).setOnClickListener {
            postponeReviewDialog(context!!, POSTPONE_TIME)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = super.onCreateDialog(savedInstanceState).apply { window?.setTitle(getString(R.string.title_ask_review)) }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        postponeReviewDialog(this@RateAppFragment.context!!, POSTPONE_TIME_CANCEL)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }

    companion object {
        const val FRAGMENT_ASK_REVIEW = "FRAGMENT_ASK_REVIEW"
        private const val APP_PREFS = "me.impa.knockonports.app"
        private const val KEY_FIRST_LAUNCH = "CFG_FIRST_LAUNCH"
        private const val KEY_KNOCK_COUNT = "CFG_KNOCK_COUNT"
        private const val KEY_DO_NOT_ASK_FOR_REVIEW = "CFG_DO_NOT_ASK_REVIEW"
        private const val KEY_DO_NOT_ASK_BEFORE = "CFG_DO_NOT_ASK_BEFORE"
        private const val POSTPONE_TIME = 5*24*60*60*1000L
        private const val POSTPONE_TIME_START = 7*24*60*60*1000L
        private const val POSTPONE_TIME_CANCEL = 1*24*60*60*1000L
        private const val KNOCKS_REQUIRED = 20L

        private fun Context.appPrefs() = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)

        fun checkFirstLaunch(context: Context) {
            val sharedPrefs = context.appPrefs()
            val firstLaunch = sharedPrefs.getLong(KEY_FIRST_LAUNCH, 0L)
            if (firstLaunch == 0L) {
                sharedPrefs.edit().putLong(KEY_FIRST_LAUNCH, System.currentTimeMillis()).apply()
                postponeReviewDialog(context, POSTPONE_TIME_START)
            }
        }
        fun incKnockCount(context: Context) {
            val sharedPrefs = context.appPrefs()
            val knockCount = sharedPrefs.getLong(KEY_KNOCK_COUNT, 0L)
            sharedPrefs.edit().putLong(KEY_KNOCK_COUNT, knockCount+1).apply()
        }

        private fun getKnockCount(context: Context): Long = context.appPrefs().getLong(KEY_KNOCK_COUNT, 0L)

        private fun isAskReviewDialogTurnedOff(context: Context): Boolean = context.appPrefs().getBoolean(KEY_DO_NOT_ASK_FOR_REVIEW, false)

        private fun getAskReviewTime(context: Context): Long = context.appPrefs().getLong(KEY_DO_NOT_ASK_BEFORE, 0L)

        fun turnOffAskReviewDialog(context: Context) {
            context.appPrefs().edit().putBoolean(KEY_DO_NOT_ASK_FOR_REVIEW, true).apply()
        }

        fun postponeReviewDialog(context: Context, timeMs: Long) {
            context.appPrefs().edit().putLong(KEY_DO_NOT_ASK_BEFORE, System.currentTimeMillis() + timeMs).apply()
        }

        fun openPlayMarket(context: Context) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
                turnOffAskReviewDialog(context)
            } catch (_: Exception) {
                context.toast(R.string.error_play_store)
            }
        }

        fun isTimeToAskForReview(context: Context) = !isAskReviewDialogTurnedOff(context)
                && getKnockCount(context) >= KNOCKS_REQUIRED
                && System.currentTimeMillis() > getAskReviewTime(context)
    }
}