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

package me.impa.knockonports.database.entity

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import me.impa.knockonports.EXTRA_SEQ_ID
import me.impa.knockonports.R
import me.impa.knockonports.StartKnockActivity
import me.impa.knockonports.data.DescriptionType
import me.impa.knockonports.data.IcmpType
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.json.SequenceStep

@Entity(tableName = "tbSequence")
data class Sequence(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long?,
        @ColumnInfo(name = "_name")
        var name: String?,
        @ColumnInfo(name = "_host")
        var host: String?,
        @ColumnInfo(name = "_order")
        var order: Int?,
        @ColumnInfo(name = "_delay")
        var delay: Int?,
        @ColumnInfo(name = "_application")
        var application: String?,
        @ColumnInfo(name = "_application_name")
        var applicationName: String?,
        @ColumnInfo(name = "_icmp_type")
        var icmpType: IcmpType?,
        @ColumnInfo(name = "_steps")
        var steps: List<SequenceStep>?,
        @ColumnInfo(name = "_descriptionType")
        var descriptionType: DescriptionType?,
        @ColumnInfo(name = "_pin")
        var pin: String?
) {

    fun getReadableDescription(): String? = when(descriptionType) {
        DescriptionType.HIDE -> null
        else -> steps?.filter { it.isValid() }?.joinToString {
            when (it.type) {
                SequenceStepType.UDP -> "${it.port ?: 0}:UDP"
                SequenceStepType.TCP -> "${it.port ?: 0}:TCP"
                SequenceStepType.ICMP -> "${it.icmpSize}x${it.icmpCount}:ICMP"
                else -> ""
            }
        }
    }

    companion object {
        const val INVALID_SEQ_ID = -100500L

        fun shortcutId(id: Long, isAuto: Boolean = true) = "KnockShortcut$id${if (!isAuto) "MANUAL" else ""}"

        @RequiresApi(Build.VERSION_CODES.N_MR1)
        fun getShortcutInfo(context: Context, sequence: Sequence, isAuto: Boolean = false, iconResource: Int = R.drawable.ic_play_arrow_24dp): ShortcutInfo =
                ShortcutInfo.Builder(context, shortcutId(sequence.id!!, isAuto))
                        .setShortLabel(sequence.name!!)
                        .setLongLabel(sequence.name!!)
                        .setIcon(Icon.createWithResource(context, iconResource))
                        .setIntent(Intent(context, StartKnockActivity::class.java).apply {
                            putExtra(EXTRA_SEQ_ID, sequence.id!!)
                            action = Intent.ACTION_VIEW
                        }).build()
    }
}