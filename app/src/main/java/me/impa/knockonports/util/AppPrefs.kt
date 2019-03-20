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

object AppPrefs {

    private const val HINT_PREFS = "me.impa.knockonports.hint"

    private const val APP_PREFS = "me.impa.knockonports.app"
    private const val KEY_FIRST_LAUNCH = "CFG_FIRST_LAUNCH"
    private const val KEY_KNOCK_COUNT = "CFG_KNOCK_COUNT"
    private const val KEY_DO_NOT_ASK_FOR_REVIEW = "CFG_DO_NOT_ASK_REVIEW"
    private const val KEY_DO_NOT_ASK_BEFORE = "CFG_DO_NOT_ASK_BEFORE"
    const val POSTPONE_TIME = 5*24*60*60*1000L
    private const val POSTPONE_TIME_START = 7*24*60*60*1000L
    const val POSTPONE_TIME_CANCEL = 1*24*60*60*1000L

    const val THEME_DEFAULT = "DEFAULT"
    const val THEME_DARK = "DARK"
    const val KEY_APP_THEME = "CFG_APP_THEME"

    private fun Context.appPrefs() = this.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
    private fun Context.hintPrefs() = this.getSharedPreferences(HINT_PREFS, Context.MODE_PRIVATE)

    fun getHintState(context: Context, hint: HintManager.Hint) = context.hintPrefs().getBoolean(hint.name, false)

    fun markHintAsShown(context: Context, hint: HintManager.Hint) =
            context.hintPrefs().edit().putBoolean(hint.name, true).apply()

    fun getCurrentTheme(context: Context): String? = context.appPrefs().getString(KEY_APP_THEME, THEME_DEFAULT)

    fun saveCurrentTheme(context: Context, theme: String) = context.appPrefs().edit().putString(KEY_APP_THEME, theme).apply()

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

    fun getKnockCount(context: Context): Long = context.appPrefs().getLong(KEY_KNOCK_COUNT, 0L)

    fun isAskReviewDialogTurnedOff(context: Context): Boolean = context.appPrefs().getBoolean(KEY_DO_NOT_ASK_FOR_REVIEW, false)

    fun getAskReviewTime(context: Context): Long = context.appPrefs().getLong(KEY_DO_NOT_ASK_BEFORE, 0L)

    fun turnOffAskReviewDialog(context: Context) {
        context.appPrefs().edit().putBoolean(KEY_DO_NOT_ASK_FOR_REVIEW, true).apply()
    }

    fun postponeReviewDialog(context: Context, timeMs: Long) {
        context.appPrefs().edit().putLong(KEY_DO_NOT_ASK_BEFORE, System.currentTimeMillis() + timeMs).apply()
    }



}