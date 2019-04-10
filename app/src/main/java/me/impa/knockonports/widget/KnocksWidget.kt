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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import me.impa.knockonports.R
import me.impa.knockonports.database.KnocksDatabase
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.database.entity.Sequence.Companion.INVALID_SEQ_ID
import me.impa.knockonports.ext.startSequence
import me.impa.knockonports.service.KnockerService
import org.jetbrains.anko.doAsyncResult

class KnocksWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            KnocksWidgetConfigureActivity.deleteBackgroundPref(context, appWidgetId)
            KnocksWidgetConfigureActivity.deleteButtonsPref(context, appWidgetId)
            KnocksWidgetConfigureActivity.deleteForegroundPref(context, appWidgetId)
            KnocksWidgetConfigureActivity.deleteSeqPref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // ...
    }

    override fun onDisabled(context: Context) {
        // ...
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val widgetId = intent.getIntExtra(INTENT_EXTRA_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            return
        when(intent.action) {
            ACTION_NEXT_SEQ -> nextSequence(context, widgetId)
            ACTION_PREV_SEQ -> prevSequence(context, widgetId)
            ACTION_KNOCK_SEQ -> knockOn(context, widgetId)
            else -> return
        }
        updateAppWidget(context, AppWidgetManager.getInstance(context), widgetId)
    }

    private fun nextSequence(context: Context, widgetId: Int) {
        val curSeq = KnocksWidgetConfigureActivity.loadSeqPref(context, widgetId)
        val db = KnocksDatabase.getInstance(context)
        val sequence = if (curSeq == INVALID_SEQ_ID) {
            doAsyncResult { db?.sequenceDao()?.getFirstSequence() }.get()
        } else {
            doAsyncResult { db?.sequenceDao()?.getNextSequence(curSeq) ?: db?.sequenceDao()?.getFirstSequence() }.get()
        } ?: return
        KnocksWidgetConfigureActivity.saveSeqPref(context, widgetId, sequence.id!!)
    }

    private fun prevSequence(context: Context, widgetId: Int) {
        val curSeq = KnocksWidgetConfigureActivity.loadSeqPref(context, widgetId)
        val db = KnocksDatabase.getInstance(context)
        val sequence = if (curSeq == INVALID_SEQ_ID) {
            doAsyncResult { db?.sequenceDao()?.getFirstSequence() }.get()
        } else {
            doAsyncResult { db?.sequenceDao()?.getPrevSequence(curSeq) ?: db?.sequenceDao()?.getLastSequence() }.get()
        } ?: return
        KnocksWidgetConfigureActivity.saveSeqPref(context, widgetId, sequence.id!!)
    }

    private fun knockOn(context: Context, widgetId: Int) {
        val sequenceId = KnocksWidgetConfigureActivity.loadSeqPref(context, widgetId)
        context.startSequence(sequenceId)
    }

    companion object {

        const val ACTION_NEXT_SEQ = "ACTION_NEXT"
        const val ACTION_PREV_SEQ = "ACTION_PREV"
        const val ACTION_KNOCK_SEQ = "ACTION_KNOCK"
        const val INTENT_EXTRA_ID = "EXTRA_WIDGET_ID"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val db = KnocksDatabase.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.knocks_widget)

            val seqId = KnocksWidgetConfigureActivity.loadSeqPref(context, appWidgetId)
            var sequence: Sequence? = null
            if (seqId != INVALID_SEQ_ID) {
                sequence = doAsyncResult { db?.sequenceDao()?.findSequenceById(seqId) }.get()
            }
            if (sequence == null) {
                sequence = doAsyncResult { db?.sequenceDao()?.getFirstSequence() }.get()
                if (sequence != null)
                    KnocksWidgetConfigureActivity.saveSeqPref(context, appWidgetId, sequence.id!!)
            }
            val widgetText = sequence?.name ?: "Knock-knock!"

            views.setTextViewText(R.id.appwidget_text, widgetText)
            views.setTextColor(R.id.appwidget_text, KnocksWidgetConfigureActivity.loadForegroundPref(context, appWidgetId))

            views.setInt(R.id.widget_layout, "setBackgroundColor", KnocksWidgetConfigureActivity.loadBackgroundPref(context, appWidgetId))

            val buttonColor = KnocksWidgetConfigureActivity.loadButtonsPref(context, appWidgetId)

            updateImage(context, views, R.id.widget_right_arrow, R.drawable.ic_keyboard_arrow_right_black_24dp, R.mipmap.ic_right_arrow, buttonColor)
            updateImage(context, views, R.id.widget_left_arrow, R.drawable.ic_keyboard_arrow_left_black_24dp, R.mipmap.ic_left_arrow, buttonColor)

            views.setOnClickPendingIntent(R.id.widget_left_arrow, getPendingIntent(context, ACTION_PREV_SEQ, appWidgetId))
            views.setOnClickPendingIntent(R.id.widget_right_arrow, getPendingIntent(context, ACTION_NEXT_SEQ, appWidgetId))
            views.setOnClickPendingIntent(R.id.appwidget_text, getPendingIntent(context, ACTION_KNOCK_SEQ, appWidgetId))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun updateImage(context: Context, views: RemoteViews, imageId: Int, drawableId: Int, olderBitmapId: Int, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                views.setImageViewResource(imageId, drawableId)
            } else {
                /*val d = ContextCompat.getDrawable(context, drawableId)
                val b = Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val c = Canvas(b)
                d.setBounds(0, 0, c.width, c.height)
                d.draw(c)*/
                val b = BitmapFactory.decodeResource(context.resources, olderBitmapId)
                views.setImageViewBitmap(imageId, b)
            }
            views.setInt(imageId, "setColorFilter", color)
        }

        private fun getPendingIntent(context: Context, action: String, widgetId: Int): PendingIntent {
            val intent = Intent(context, KnocksWidget::class.java)
            intent.action = action
            intent.putExtra(INTENT_EXTRA_ID, widgetId)
            return PendingIntent.getBroadcast(context, widgetId, intent, 0)
        }
    }
}

