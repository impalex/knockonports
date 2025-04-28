/*
 * Copyright (c) 2018-2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package me.impa.knockonports.knock

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.impa.knockonports.R
import me.impa.knockonports.constants.INVALID_SEQ_ID
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.di.IoDispatcher
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * A service responsible for executing knocking sequences.
 *
 * This service manages a queue of sequence IDs, retrieves sequence data from the repository,
 * and uses a [Knocker] to perform the knocking actions. It runs as a foreground service with a
 * persistent notification, ensuring that the system doesn't kill it prematurely while sequences
 * are in progress.  The service handles errors during knocking and gracefully stops itself when
 * the sequence queue is empty or when a cancellation request is received.
 */
@AndroidEntryPoint
class KnockerService : Service() {

    @Inject
    lateinit var repository: KnocksRepository

    @IoDispatcher
    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var knocker: Knocker

    private var knockerJob: Job? = null

    private val sequenceQueue = ArrayDeque<Long?>()

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    /**
     * This method is called when the service is started using `startService()`.
     * It adds the sequence ID from the intent to the queue and starts the knocker job if it's not already running.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sequenceQueue.add(intent?.getLongExtra(SEQUENCE_ID, INVALID_SEQ_ID))
        if (knockerJob == null) {
            Timber.d("Starting service")
            makeForeground()
            knockerJob = launchKnockerJob()
        }
        return START_NOT_STICKY
    }

    /**
     * Callback invoked when the service's internal timer expires.
     */
    override fun onTimeout(startId: Int) {
        // Oops... In a normal situation we shouldn't get here. But if it does, we need to stop all operations.
        Timber.e("Service timeout")
        knockerJob?.cancel()
        stopSelf()
    }

    /**
     * Launches a coroutine job to handle the knocking process for sequences in the queue.
     */
    private fun launchKnockerJob() = CoroutineScope(ioDispatcher).launch {
        // Run while queue is not empty
        try {
            while (sequenceQueue.isNotEmpty()) {
                val sequenceId = sequenceQueue.removeFirstOrNull() ?: INVALID_SEQ_ID
                Timber.d("Sequence ID: $sequenceId")
                if (sequenceId != INVALID_SEQ_ID) {
                    updateNotification(repository.getSequenceName(sequenceId))
                    try {
                        knocker.knock(sequenceId)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Timber.e(e, "Error while knocking")
                    }
                }
            }
        } catch (_: CancellationException) {
            Timber.d("Knocker job cancelled")
        } finally {
            Timber.d("Stopping service")
            stopSelf()
        }
    }

    /**
     * Builds a notification for the ongoing service.
     *
     * @param sequenceName The name of the sequence being executed, or null if no sequence is active.
     *                     If an empty string is provided, it will be displayed as "Unnamed Sequence".
     * @return A [android.app.Notification] object configured for the ongoing service.
     */
    private fun getNotification(sequenceName: String? = null) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_knock_notif)
            .setContentTitle(resources.getString(R.string.notification_title))
            .apply {
                sequenceName?.let {
                    setContentText(
                        resources.getString(
                            R.string.notification_desc,
                            if (it.isEmpty()) resources.getString(R.string.text_unnamed_sequence) else it
                        )
                    )
                }
            }.build()

    private fun makeForeground() {
        startForeground(FOREGROUND_ID, getNotification())
    }

    /**
     * Updates the foreground notification displayed to the user.
     *
     * This function retrieves the `NotificationManager` system service and checks if notifications
     * are enabled for the app. If both conditions are true, it updates the notification with the
     * specified sequence name. If notifications are disabled or the `NotificationManager` cannot be
     * retrieved, the function does nothing.
     *
     * @param sequenceName The name of the current sequence being processed.  This name will be displayed
     * in the notification's content title.
     */
    private fun updateNotification(sequenceName: String?) {
        getSystemService(NotificationManager::class.java)
            ?.takeIf { it.areNotificationsEnabled() }
            ?.notify(FOREGROUND_ID, getNotification(sequenceName))
    }

    companion object {
        private const val FOREGROUND_ID = 1337
        const val CHANNEL_ID = "KNOCKER_CHANNEL"
        const val SEQUENCE_ID = "KNOCK_SEQUENCE_ID"
    }

}
