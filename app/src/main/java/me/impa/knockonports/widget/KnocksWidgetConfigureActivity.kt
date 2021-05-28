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

package me.impa.knockonports.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.appcompat.app.AppCompatActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import me.impa.knockonports.R
import me.impa.knockonports.database.entity.Sequence.Companion.INVALID_SEQ_ID
import me.impa.knockonports.databinding.KnocksWidgetConfigureBinding

class KnocksWidgetConfigureActivity : AppCompatActivity(), ColorPickerDialogListener {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private lateinit var binding: KnocksWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        binding = KnocksWidgetConfigureBinding.inflate(layoutInflater)

        setResult(Activity.RESULT_CANCELED)

        setContentView(binding.root)

        binding.addButton.setOnClickListener {

            saveBackgroundPref(this, appWidgetId, binding.colorPanelBackground.color)
            saveForegroundPref(this, appWidgetId, binding.colorPanelForeground.color)
            saveButtonsPref(this, appWidgetId, binding.colorPanelArrows.color)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            KnocksWidget.updateAppWidget(this, appWidgetManager, appWidgetId)

            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val colorBackround = icicle?.getInt(PREF_BUNDLE + CHANGE_BACKGROUND_COLOR, loadBackgroundPref(this, appWidgetId))
                ?: loadBackgroundPref(this, appWidgetId)
        val colorForeground = icicle?.getInt(PREF_BUNDLE + CHANGE_FOREGROUND_COLOR, loadForegroundPref(this, appWidgetId))
                ?: loadForegroundPref(this, appWidgetId)
        val colorButtons = icicle?.getInt(PREF_BUNDLE + CHANGE_BUTTON_COLOR, loadButtonsPref(this, appWidgetId))
                ?: loadButtonsPref(this, appWidgetId)

        previewColor(CHANGE_BUTTON_COLOR, colorButtons)
        previewColor(CHANGE_FOREGROUND_COLOR, colorForeground)
        previewColor(CHANGE_BACKGROUND_COLOR, colorBackround)

        binding.colorPanelArrows.setOnClickListener { showColorPickerDialog(binding.colorPanelArrows.color, CHANGE_BUTTON_COLOR) }
        binding.colorPanelForeground.setOnClickListener { showColorPickerDialog(binding.colorPanelForeground.color, CHANGE_FOREGROUND_COLOR) }
        binding.colorPanelBackground.setOnClickListener { showColorPickerDialog(binding.colorPanelBackground.color, CHANGE_BACKGROUND_COLOR, true) }

    }

    private fun showColorPickerDialog(color: Int, dialogId: Int, showAlpha: Boolean = false) {
        ColorPickerDialog.newBuilder()
                .setDialogId(dialogId)
                .setColor(color)
                .setShowAlphaSlider(showAlpha)
                .show(this)
    }

    private fun previewColor(typeId: Int, color: Int) {
        when(typeId) {
            CHANGE_BACKGROUND_COLOR -> {
                binding.colorPanelBackground.color = color
                binding.widgetPreview.widgetLayout.setBackgroundColor(color)
            }
            CHANGE_FOREGROUND_COLOR -> {
                binding.colorPanelForeground.color = color
                binding.widgetPreview.appwidgetText.setTextColor(color)
            }
            CHANGE_BUTTON_COLOR -> {
                binding.colorPanelArrows.color = color
                ImageViewCompat.setImageTintList(binding.widgetPreview.widgetRightArrow, ColorStateList.valueOf(color))
                ImageViewCompat.setImageTintList(binding.widgetPreview.widgetLeftArrow, ColorStateList.valueOf(color))
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        previewColor(dialogId, color)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PREF_BUNDLE + CHANGE_BUTTON_COLOR, binding.colorPanelArrows.color)
        outState.putInt(PREF_BUNDLE + CHANGE_BACKGROUND_COLOR, binding.colorPanelBackground.color)
        outState.putInt(PREF_BUNDLE + CHANGE_FOREGROUND_COLOR, binding.colorPanelForeground.color)
    }

    companion object {

        private const val CHANGE_BACKGROUND_COLOR = 7000
        private const val CHANGE_FOREGROUND_COLOR = 7001
        private const val CHANGE_BUTTON_COLOR = 7002

        private const val PREFS_NAME = "me.impa.knockonports.widget.KnocksWidget"
        private const val PREF_BUNDLE = "COLOR_"
        private const val PREF_PREFIX_KEY = "appwidget_"
        private const val PREF_PREFIX_KEY_BACKGROUND = PREF_PREFIX_KEY + "back_"
        private const val PREF_PREFIX_KEY_FOREGROUND = PREF_PREFIX_KEY + "fore_"
        private const val PREF_PREFIX_KEY_BUTTONS = PREF_PREFIX_KEY + "btn_"
        private const val PREF_PREFIX_KEY_SEQUENCE = PREF_PREFIX_KEY + "seq_"

        internal fun saveBackgroundPref(context: Context, appWidgetId: Int, color: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(PREF_PREFIX_KEY_BACKGROUND + appWidgetId, color)
                    .apply()
        }

        internal fun loadBackgroundPref(context: Context, appWidgetId: Int): Int {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getInt(PREF_PREFIX_KEY_BACKGROUND + appWidgetId, ContextCompat.getColor(context, R.color.colorWidgetBackground))
        }

        internal fun deleteBackgroundPref(context: Context, appWidgetId: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .remove(PREF_PREFIX_KEY_BACKGROUND + appWidgetId)
                    .apply()
        }

        internal fun saveForegroundPref(context: Context, appWidgetId: Int, color: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(PREF_PREFIX_KEY_FOREGROUND + appWidgetId, color)
                    .apply()
        }

        internal fun loadForegroundPref(context: Context, appWidgetId: Int): Int {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getInt(PREF_PREFIX_KEY_FOREGROUND + appWidgetId, ContextCompat.getColor(context, R.color.colorWidgetForeground))
        }

        internal fun deleteForegroundPref(context: Context, appWidgetId: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .remove(PREF_PREFIX_KEY_FOREGROUND + appWidgetId)
                    .apply()
        }

        internal fun saveButtonsPref(context: Context, appWidgetId: Int, color: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(PREF_PREFIX_KEY_BUTTONS + appWidgetId, color)
                    .apply()
        }

        internal fun loadButtonsPref(context: Context, appWidgetId: Int): Int {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getInt(PREF_PREFIX_KEY_BUTTONS + appWidgetId, ContextCompat.getColor(context, R.color.colorWidgetButtons))
        }

        internal fun deleteButtonsPref(context: Context, appWidgetId: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .remove(PREF_PREFIX_KEY_BUTTONS + appWidgetId)
                    .apply()
        }

        internal fun saveSeqPref(context: Context, appWidgetId: Int, seq: Long) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(PREF_PREFIX_KEY_SEQUENCE + appWidgetId, seq)
                    .apply()
        }

        internal fun loadSeqPref(context: Context, appWidgetId: Int): Long {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getLong(PREF_PREFIX_KEY_SEQUENCE + appWidgetId, INVALID_SEQ_ID)
        }

        internal fun deleteSeqPref(context: Context, appWidgetId: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .remove(PREF_PREFIX_KEY_SEQUENCE + appWidgetId)
                    .apply()
        }

    }
}

