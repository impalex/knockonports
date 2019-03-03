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

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import android.content.Context
import me.impa.knockonports.data.AppData
import me.impa.knockonports.data.PortType
import me.impa.knockonports.database.converter.SequenceConverters
import me.impa.knockonports.database.dao.SequenceDao
import me.impa.knockonports.database.entity.Sequence

@Database(
        entities = [Sequence::class],
        version = 10
)
@TypeConverters(SequenceConverters::class)
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

    class Migration3To4: Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_base64` INTEGER")
        }
    }

    class Migration4To5: Migration(4, 5) {
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
                            portList.add("${portCur.getInt(0)}:${if (portCur.getInt(1) == PortType.UDP.ordinal) {
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

    class Migration5To6: Migration(5, 6) {
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
                    } + SequenceConverters.VALUE_SEPARATOR +
                            if (portCur.isNull(2)) {
                                ""
                            } else {
                                portCur.getInt(2).toString()
                            }
                    portMap[seqId] = if (portMap.containsKey(seqId)) {
                        portMap[seqId] + SequenceConverters.ENTRY_SEPARATOR
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

    class Migration6To7(val context: Context): Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val apps by lazy { AppData.loadInstalledApps(context) }
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_application_name` TEXT")
            val appMap = mutableMapOf<Long, String?>()
            val appCur = database.query("SELECT `_id`, `_application` FROM `tbSequence`")
            if (appCur.moveToFirst()) {
                do {
                    if (appCur.isNull(1) || appCur.isNull(0))
                        continue
                    val app = appCur.getString(1)
                    if (app.isEmpty())
                        continue
                    val seqId = appCur.getLong(0)
                    val appName = apps.firstOrNull { it.app == app }?.name
                    appMap[seqId] = appName
                } while (appCur.moveToNext())
                appMap.forEach {
                    database.execSQL("UPDATE `tbSequence` SET `_application_name`=? WHERE `_id`=?", arrayOf(it.value, it.key))
                }
            }
        }
    }

    class Migration7To8: Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_type` INTEGER")
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_icmp_string` TEXT")
            database.execSQL("UPDATE `tbSequence` SET `_type`=0")
        }
    }

    class Migration8To9: Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_icmp_type` INTEGER")
            database.execSQL("UPDATE `tbSequence` SET `_icmp_type`=1")
        }
    }

    class Migration9To10: Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `tbSequence_new` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`_name` TEXT, `_host` TEXT, `_order` INTEGER, `_delay` INTEGER, `_udp_content` TEXT, " +
                    "`_application` TEXT, `_base64` INTEGER, `_port_string` TEXT, `_application_name` TEXT, " +
                    "`_type` INTEGER, `_icmp_string` TEXT, `_icmp_type` INTEGER)")
            database.execSQL("INSERT INTO `tbSequence_new` (`_id`, " +
                    "`_name`, `_host`, `_order`, `_delay`, `_udp_content`, " +
                    "`_application`, `_base64`, `_port_string`, `_application_name`, " +
                    "`_type`, `_icmp_string`, `_icmp_type`) SELECT `_id`, " +
                    "`_name`, `_host`, `_order`, `_delay`, `_udp_content`, " +
                    "`_application`, `_base64`, `_port_string`, `_application_name`, " +
                    "`_type`, `_icmp_string`, `_icmp_type` from `tbSequence`")
            database.execSQL("DROP TABLE `tbSequence`")
            database.execSQL("ALTER TABLE `tbSequence_new` RENAME TO `tbSequence`")
        }
    }

    companion object {
        private var INSTANCE: KnocksDatabase? = null

        fun getInstance(context: Context): KnocksDatabase? {
            if (INSTANCE == null) {
                synchronized(KnocksDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            KnocksDatabase::class.java, "knocksdb")
                            .addMigrations(Migration1To2(),
                                    Migration2To3(),
                                    Migration3To4(),
                                    Migration4To5(),
                                    Migration5To6(),
                                    Migration6To7(context),
                                    Migration7To8(),
                                    Migration8To9(),
                                    Migration9To10())
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