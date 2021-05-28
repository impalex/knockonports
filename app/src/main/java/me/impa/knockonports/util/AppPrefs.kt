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
import androidx.preference.PreferenceManager
import me.impa.knockonports.R
import java.io.File
import java.lang.Exception

@Suppress("MemberVisibilityCanBePrivate")
object AppPrefs {

    private const val HINT_PREFS = "me.impa.knockonports.hint"
    private const val APP_PREFS = "me.impa.knockonports.app"
    const val KEY_FIRST_LAUNCH = "CFG_FIRST_LAUNCH"
    const val KEY_KNOCK_COUNT = "CFG_KNOCK_COUNT"
    const val KEY_DO_NOT_ASK_FOR_REVIEW = "CFG_DO_NOT_ASK_REVIEW"
    const val KEY_DO_NOT_ASK_BEFORE = "CFG_DO_NOT_ASK_BEFORE"
    const val KEY_ASK_CONFIRMATION = "CFG_CONFIRM_WIDGET"
    const val KEY_CUSTOM_IP4_SERVICE = "CFG_IP4_CUSTOM_SERVICE"
    const val KEY_CUSTOM_IP6_SERVICE = "CFG_IP6_CUSTOM_SERVICE"
    const val KEY_IP4_SERVICE = "CFG_IP4_SERVICE"
    const val KEY_IP6_SERVICE = "CFG_IP6_SERVICE"
    const val KEY_ABOUT_VERSION = "CFG_ABOUT_VERSION"
    const val KEY_REPORT_ISSUE = "CFG_REPORT_ISSUE"
    const val KEY_SOURCE = "CFG_SOURCE"
    const val KEY_CONTACT_AUTHOR = "CFG_CONTACT"
    const val POSTPONE_TIME = 5*24*60*60*1000L
    private const val POSTPONE_TIME_START = 7*24*60*60*1000L
    const val POSTPONE_TIME_CANCEL = 1*24*60*60*1000L

    const val THEME_DEFAULT = "DEFAULT"
    const val THEME_DARK = "DARK"
    private const val KEY_APP_THEME = "CFG_APP_THEME"

    private fun Context.appPrefs() = PreferenceManager.getDefaultSharedPreferences(this)

    private var restartHandler: (() -> Unit)? = null

    private fun migrate(context: Context, fileName: String) {
        val f = File("${context.filesDir.parent}/shared_prefs/${fileName}.xml")
        if (!f.exists())
            return
        val prefs = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val newPrefs = context.appPrefs()
        prefs.all.forEach { (key, value) ->
            when (value) {
                is Boolean -> newPrefs.edit().putBoolean(key, value).apply()
                is Float -> newPrefs.edit().putFloat(key, value).apply()
                is Int -> newPrefs.edit().putInt(key, value).apply()
                is Long -> newPrefs.edit().putLong(key, value).apply()
                is String -> newPrefs.edit().putString(key, value).apply()
            }
        }
        prefs.edit().clear().apply()
        try {
            f.delete()
        } catch (_: Exception) {
            // don't really care
        }
    }

    fun init(context: Context) {
        migrate(context, APP_PREFS)
        migrate(context, HINT_PREFS)
        PreferenceManager.setDefaultValues(context, R.xml.settings, false)
        context.appPrefs().registerOnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_APP_THEME) {
                restartHandler?.invoke()
            }
        }
    }

    fun registerRestartHandler(handler: () -> Unit) {
        restartHandler = handler
    }

    fun getHintState(context: Context, hint: HintManager.Hint) = context.appPrefs().getBoolean(hint.name, false)

    fun markHintAsShown(context: Context, hint: HintManager.Hint) =
            context.appPrefs().edit().putBoolean(hint.name, true).apply()

    fun getCurrentTheme(context: Context): String? = context.appPrefs().getString(KEY_APP_THEME, THEME_DEFAULT)

    fun saveCurrentTheme(context: Context, theme: String) = context.appPrefs().edit().putString(KEY_APP_THEME, theme).apply()

    fun getAskConfirmation(context: Context): Boolean = context.appPrefs().getBoolean(KEY_ASK_CONFIRMATION, false)

    fun getCustomIP4Service(context: Context): String? = context.appPrefs().getString(KEY_CUSTOM_IP4_SERVICE, null)

    fun getCustomIP6Service(context: Context): String? = context.appPrefs().getString(KEY_CUSTOM_IP6_SERVICE, null)

    fun getIP4Service(context: Context): String? = context.appPrefs().getString(KEY_IP4_SERVICE, null)

    fun getIP6Service(context: Context): String? = context.appPrefs().getString(KEY_IP6_SERVICE, null)

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