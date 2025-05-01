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

import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import me.impa.knockonports.service.sequence.KnockerService.Companion.SEQUENCE_ID
import javax.inject.Inject

/**
 *  KnockHelper is a utility class responsible for starting the KnockerService.
 *  It handles differences in service starting procedures between Android versions
 *  (before and after Android Oreo).
 *
 *  @property context The application context used for starting the service.
 */
class KnockHelper @Inject constructor(@ApplicationContext val context: Context) {
    /**
     * Starts the KnockerService.
     */
    fun start(sequenceId: Long) {
        val intent = Intent(context, KnockerService::class.java)
        intent.putExtra(SEQUENCE_ID, sequenceId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
    }
}