/*
 * Copyright (c) 2025 Alexander Yaburov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.impa.knockonports.service.sequence

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import me.impa.knockonports.R
import me.impa.knockonports.constants.INVALID_SEQ_ID
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.di.MainDispatcher
import me.impa.knockonports.service.wear.WearConnectionManager
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

    @MainDispatcher
    @Inject
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var knocker: Knocker

    @Inject
    lateinit var wearConnectionManager: WearConnectionManager

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
                    val notifJob = knocker.knockStatus.filterNotNull().onEach {
                        if (wearConnectionManager.isCompanionReady.value)
                            wearConnectionManager.sendStatus(it)
                        updateNotification(it)
                    }.onCompletion {
                        if (wearConnectionManager.isCompanionReady.value)
                            wearConnectionManager.sendStatus(null)
                    }.launchIn(CoroutineScope(mainDispatcher) + SupervisorJob())

                    try {
                        knocker.knock(sequenceId)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Timber.e(e, "Error while knocking")
                    }
                    finally {
                        notifJob.cancelAndJoin()
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
     * Builds the notification for the foreground service.
     *
     * This notification informs the user that a knocking sequence is in progress.
     *
     * @param knockState The current status of the knocking process. Used to update notification content.
     * @return A configured [android.app.Notification] object for the foreground service.
     */
    private fun getNotification(knockState: KnockState? = null) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_knock_notif)
            .setContentTitle(resources.getString(R.string.notification_title))
            .apply {
                knockState?.let { state ->
                    setContentText(
                        buildString {
                            if (state.maxAttempts > 1) {
                                append(
                                    resources.getString(
                                        R.string.notification_desc_attempt,
                                        state.attempt, state.maxAttempts
                                    )
                                )
                                append(" ")
                            }
                            if (state.isWaitingForResource) {
                                append(resources.getString(R.string.notification_desc_waiting_for_resource))
                            } else {
                                append(state.sequenceName.ifEmpty {
                                    resources.getString(R.string.text_unnamed_sequence)
                                })
                            }
                        }
                    )
                    if (state.maxSteps > 0) {
                        setProgress(state.maxSteps, state.step, false)
                    }
                }
            }.build()

    private fun makeForeground() {
        startForeground(FOREGROUND_ID, getNotification())
    }

    /**
     * Updates the foreground notification with the current status.
     *
     * This function retrieves the `NotificationManager` system service and checks if notifications
     * are enabled for the app. If they are, it updates the existing foreground notification
     * using the information from the provided [KnockState].
     *
     * @param knockState The current status of the knocking sequence, containing information
     * to display in the notification.
     */
    private fun updateNotification(knockState: KnockState?) {
        getSystemService(NotificationManager::class.java)
            ?.takeIf { it.areNotificationsEnabled() }
            ?.notify(FOREGROUND_ID, getNotification(knockState))
    }

    companion object {
        private const val FOREGROUND_ID = 1337
        const val CHANNEL_ID = "KNOCKER_CHANNEL"
        const val SEQUENCE_ID = "KNOCK_SEQUENCE_ID"
    }

}