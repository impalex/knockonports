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

package me.impa.knockonports.extension

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.jvm.java
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.R
import me.impa.knockonports.StartKnockingActivity
import me.impa.knockonports.constants.EXTRA_SEQ_ID
import me.impa.knockonports.constants.EXTRA_SOURCE
import me.impa.knockonports.constants.EXTRA_VALUE_SOURCE_SHORTCUT
import me.impa.knockonports.constants.INVALID_SEQ_ID

fun shortcutId(sequenceId: Long?, isAuto: Boolean = true) = "KnockShortcut${sequenceId}${if (!isAuto) "MANUAL" else ""}"

fun Sequence.shortcutId(isAuto: Boolean = true) = shortcutId(this.id, isAuto)

@RequiresApi(Build.VERSION_CODES.N_MR1)
fun Sequence.getShortcutInfo(context: Context, isAuto: Boolean = false,
                             iconResource: Int = R.mipmap.ic_play_seq): ShortcutInfo =
    ShortcutInfo.Builder(context, this.shortcutId(isAuto))
        .setShortLabel(name ?: context.getString(R.string.text_unnamed_sequence))
        .setLongLabel(name ?: context.getString(R.string.text_unnamed_sequence))
        .setIcon(Icon.createWithResource(context, iconResource))
        .setIntent(Intent(context, StartKnockingActivity::class.java).apply {
            putExtra(EXTRA_SEQ_ID, this@getShortcutInfo.id ?: INVALID_SEQ_ID)
            putExtra(EXTRA_SOURCE, EXTRA_VALUE_SOURCE_SHORTCUT)
            action = Intent.ACTION_VIEW
        }).build()

