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

package me.impa.knockonports.service

import android.app.IntentService
import android.content.Intent
import androidx.core.app.NotificationCompat
import me.impa.knockonports.R
import me.impa.knockonports.database.KnocksDatabase
import me.impa.knockonports.database.entity.Sequence
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn

class KnockerService: IntentService(KnockerService::class.java.name), AnkoLogger {

    private fun dummyServiceStart() {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setOngoing(true)
                .setContentTitle(resources.getString(R.string.notification_title))
                .setSmallIcon(R.drawable.ic_knock_notif)

        startForeground(FOREGROUND_ID, notificationBuilder.build())

        stopForeground(true)

    }

    override fun onHandleIntent(intent: Intent?) {

        if (intent == null) {
            dummyServiceStart()
            return
        }

        val seqId = intent.getLongExtra(SEQUENCE_ID, Sequence.INVALID_SEQ_ID)

        if (seqId == Sequence.INVALID_SEQ_ID) {
            warn {
                "Invalid sequence ID!"
            }
            dummyServiceStart()
            return
        }

        val db = KnocksDatabase.getInstance(this)
        val sequence = db?.sequenceDao()?.findSequenceById(seqId)
        if (sequence == null) {
            warn {
                "Couldn't find sequence ID $seqId"
            }
            dummyServiceStart()
            return
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setOngoing(true)
                .setContentTitle(resources.getString(R.string.notification_title))
                .setContentText(resources.getString(R.string.notification_desc, sequence.name))
                .setSmallIcon(R.drawable.ic_knock_notif)

        startForeground(FOREGROUND_ID, notificationBuilder.build())

        Knocker(this, sequence).execute()

        stopForeground(true)
    }

    companion object {
        private const val FOREGROUND_ID = 1337
        const val CHANNEL_ID = "KNOCKER_CHANNEL"
        const val SEQUENCE_ID = "KNOCK_SEQUENCE_ID"
    }


}