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

package me.impa.knockonports.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import me.impa.knockonports.database.dao.SequenceDao
import me.impa.knockonports.database.entity.Sequence

@Database(
        entities = [Sequence::class],
        version = 6
)
abstract class KnocksDatabase : RoomDatabase() {
    abstract fun sequenceDao(): SequenceDao

    class Migration1To2: Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_delay` INTEGER")
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_udp_content` TEXT")
        }
    }

    class Migration2To3: Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_application` TEXT")
        }

    }

    class Migration3to4: Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_base64` INTEGER")
        }
    }

    class Migration4to5: Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_port_string` TEXT")
            val seqCur = database.query("SELECT `_id` FROM `tbSequence`")
            val seqList = mutableListOf<Long>()
            if (seqCur.moveToFirst()) {
                do {
                    seqList.add(seqCur.getLong(0))
                } while (seqCur.moveToNext())
            } else return
            seqList.forEach {
                val portCur = database.query("SELECT `_number`, `_type` FROM `tbPort` WHERE `_sequence_id`=? ORDER BY `_id`", arrayOf(it))
                val portList = mutableListOf<String>()
                if (portCur.moveToFirst()) {
                    do {
                        if (!portCur.isNull(0) && !portCur.isNull(1)) {
                            portList.add("${portCur.getInt(0)}:${if (portCur.getInt(1) == Sequence.PORT_TYPE_UDP) {
                                "UDP"
                            } else {
                                "TCP"
                            }}")
                        }
                    } while (portCur.moveToNext())
                }
                if (portList.size > 0) {
                    database.execSQL("UPDATE `tbSequence` SET `_port_string`=? WHERE `_id`=?", arrayOf(portList.joinToString(", "), it))
                }
            }
        }
    }

    class Migration5to6: Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val portMap = mutableMapOf<Long, String>()
            val portCur = database.query("SELECT `_sequence_id`, `_number`, `_type` FROM `tbPort` ORDER BY `_id`")
            if (portCur.moveToFirst()) {
                do {
                    if (portCur.isNull(0))
                        continue
                    val seqId = portCur.getLong(0)
                    val str = if (portCur.isNull(1)) {
                        ""
                    } else {
                        portCur.getInt(1).toString()
                    } + Sequence.PORT_SEPARATOR +
                            if (portCur.isNull(2)) {
                                ""
                            } else {
                                portCur.getInt(2).toString()
                            }
                    portMap[seqId] = if (portMap.containsKey(seqId)) {
                        portMap[seqId] + Sequence.ENTRY_SEPARATOR
                    } else {
                        ""
                    } + str
                } while (portCur.moveToNext())
                portMap.forEach {
                    database.execSQL("UPDATE `tbSequence` SET `_port_string`=? WHERE `_id`=?", arrayOf(it.value, it.key))
                }
            }
            database.execSQL("DROP TABLE `tbPort`")
        }

    }

    companion object {
        private var INSTANCE: KnocksDatabase? = null

        fun getInstance(context: Context): KnocksDatabase? {
            if (INSTANCE == null) {
                synchronized(KnocksDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            KnocksDatabase::class.java, "knocksdb")
                            .addMigrations(Migration1To2(), Migration2To3(),
                                    Migration3to4(), Migration4to5(),
                                    Migration5to6())
                            .build()

                }
            }
            return INSTANCE
        }

        @Suppress("unused")
        fun destroyInstance() {
            synchronized(KnocksDatabase::class) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}