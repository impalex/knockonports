/*
 * Copyright (c) 2018-2025 Alexander Yaburov
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

@file:Suppress("MagicNumber", "LongMethod", "NestedBlockDepth", "LoopWithTooManyJumpStatements")

package me.impa.knockonports.data.db

import android.content.Context
import android.util.Base64
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.impa.knockonports.constants.DEFAULT_CHECK_RETRIES
import me.impa.knockonports.constants.DEFAULT_CHECK_TIMEOUT
import me.impa.knockonports.data.db.converter.DataConverters
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.helper.AppData

class Migrations {
    class Migration1To2 : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_delay` INTEGER")
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_udp_content` TEXT")
        }
    }

    class Migration2To3 : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_application` TEXT")
        }
    }

    class Migration3To4 : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_base64` INTEGER")
        }
    }

    class Migration4To5 : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_port_string` TEXT")
            val seqCur = db.query("SELECT `_id` FROM `tbSequence`")
            val seqList = mutableListOf<Long>()
            if (seqCur.moveToFirst()) {
                do {
                    seqList.add(seqCur.getLong(0))
                } while (seqCur.moveToNext())
            } else {
                return
            }
            seqList.forEach {
                val portCur = db.query(
                    "SELECT `_number`, `_type` FROM `tbPort` WHERE `_sequence_id`=? ORDER BY `_id`",
                    arrayOf(it)
                )
                val portList = mutableListOf<String>()
                if (portCur.moveToFirst()) {
                    do {
                        if (!portCur.isNull(0) && !portCur.isNull(1)) {
                            portList.add(
                                "${portCur.getInt(0)}:${if (portCur.getInt(1) == 0) {
                                    "UDP"
                                } else {
                                    "TCP"
                                }}"
                            )
                        }
                    } while (portCur.moveToNext())
                }
                if (portList.isNotEmpty()) {
                    db.execSQL(
                        "UPDATE `tbSequence` SET `_port_string`=? WHERE `_id`=?",
                        arrayOf<Any>(portList.joinToString(", "), it)
                    )
                }
            }
        }
    }

    class Migration5To6 : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val portMap = mutableMapOf<Long, String>()
            val portCur = db.query("SELECT `_sequence_id`, `_number`, `_type` FROM `tbPort` ORDER BY `_id`")
            if (portCur.moveToFirst()) {
                do {
                    if (portCur.isNull(0)) {
                        continue
                    }
                    val seqId = portCur.getLong(0)
                    val str = if (portCur.isNull(1)) {
                        ""
                    } else {
                        portCur.getInt(1).toString()
                    } + DataConverters.VALUE_SEPARATOR +
                        if (portCur.isNull(2)) {
                            ""
                        } else {
                            portCur.getInt(2).toString()
                        }
                    portMap[seqId] = if (portMap.containsKey(seqId)) {
                        portMap[seqId] + DataConverters.ENTRY_SEPARATOR
                    } else {
                        ""
                    } + str
                } while (portCur.moveToNext())
                portMap.forEach {
                    db.execSQL(
                        "UPDATE `tbSequence` SET `_port_string`=? WHERE `_id`=?",
                        arrayOf<Any>(it.value, it.key)
                    )
                }
            }
            db.execSQL("DROP TABLE `tbPort`")
        }
    }

    class Migration6To7(val context: Context) : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val apps by lazy { AppData.loadInstalledApps(context) }
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_application_name` TEXT")
            val appMap = mutableMapOf<Long, String?>()
            val appCur = db.query("SELECT `_id`, `_application` FROM `tbSequence`")
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
                    db.execSQL("UPDATE `tbSequence` SET `_application_name`=? WHERE `_id`=?",
                    arrayOf<Any?>(it.value, it.key))
                }
            }
        }
    }

    class Migration7To8 : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_type` INTEGER")
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_icmp_string` TEXT")
            db.execSQL("UPDATE `tbSequence` SET `_type`=0")
        }
    }

    class Migration8To9 : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_icmp_type` INTEGER")
            db.execSQL("UPDATE `tbSequence` SET `_icmp_type`=1")
        }
    }

    class Migration9To10 : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE `tbSequence_new` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`_name` TEXT, `_host` TEXT, `_order` INTEGER, `_delay` INTEGER, `_udp_content` TEXT, " +
                    "`_application` TEXT, `_base64` INTEGER, `_port_string` TEXT, `_application_name` TEXT, " +
                    "`_type` INTEGER, `_icmp_string` TEXT, `_icmp_type` INTEGER)"
            )
            db.execSQL(
                "INSERT INTO `tbSequence_new` (`_id`, " +
                    "`_name`, `_host`, `_order`, `_delay`, `_udp_content`, " +
                    "`_application`, `_base64`, `_port_string`, `_application_name`, " +
                    "`_type`, `_icmp_string`, `_icmp_type`) SELECT `_id`, " +
                    "`_name`, `_host`, `_order`, `_delay`, `_udp_content`, " +
                    "`_application`, `_base64`, `_port_string`, `_application_name`, " +
                    "`_type`, `_icmp_string`, `_icmp_type` from `tbSequence`"
            )
            db.execSQL("DROP TABLE `tbSequence`")
            db.execSQL("ALTER TABLE `tbSequence_new` RENAME TO `tbSequence`")
        }
    }

    class Migration10To11 : Migration(10, 11) {
        @Suppress("CyclomaticComplexMethod")
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE `tbSequence_new` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`_name` TEXT, `_host` TEXT, `_order` INTEGER, `_delay` INTEGER, `_application` TEXT, " +
                    "`_application_name` TEXT, `_icmp_type` INTEGER, `_steps` TEXT)"
            )
            db.execSQL(
                "INSERT INTO `tbSequence_new` (`_id`, `_name`, `_host`, `_order`, `_delay`, " +
                    "`_application`, `_application_name`, `_icmp_type`) SELECT " +
                    "`_id`, `_name`, `_host`, `_order`, `_delay`, `_application`, `_application_name`, " +
                    "`_icmp_type` from `tbSequence`"
            )

            val seqMap = mutableMapOf<Long, String?>()
            val seqCur = db.query(
                "SELECT `_id`, `_type`, `_udp_content`, `_base64`, `_port_string`, `_icmp_string` FROM `tbSequence`"
            )
            if (seqCur.moveToFirst()) {
                do {
                    if (seqCur.isNull(0) || seqCur.isNull(1)) {
                        continue
                    }
                    val id = seqCur.getLong(0)
                    val type = seqCur.getLong(1)
                    if (type == 0L) {
                        val portString = if (seqCur.isNull(4)) "" else seqCur.getString(4)
                        if (portString.isBlank()) {
                            continue
                        }
                        val content = if (seqCur.isNull(
                                2
                            )
                        ) {
                            ""
                        } else {
                            Base64.encodeToString(
                                seqCur.getString(2).toByteArray(),
                                Base64.NO_PADDING or Base64.NO_WRAP
                            )
                        }
                        val base64 = if (seqCur.isNull(3)) 0 else seqCur.getLong(3)
                        seqMap[id] = portString.split('|').joinToString("|") {
                            val portData = it.split(":")
                            "${portData[1]}:${portData[0]}:::$content:" +
                                "${if (base64 == 0L || base64 == 1L) base64 else 0}"
                        }
                    } else if (type == 1L) {
                        val icmpString = if (seqCur.isNull(5)) "" else seqCur.getString(5)
                        if (icmpString.isBlank()) {
                            continue
                        }
                        seqMap[id] = icmpString.split("|").joinToString("|") {
                            val icmpData = it.split(":")
                            "2::${icmpData[0]}:${icmpData[1]}:${icmpData[3]}:${icmpData[2]}"
                        }
                    }
                } while (seqCur.moveToNext())
                seqMap.forEach {
                    db.execSQL(
                        "UPDATE `tbSequence_new` SET `_steps`=? WHERE `_id`=?",
                        arrayOf<Any?>(it.value, it.key)
                    )
                }
            }
            db.execSQL("DROP TABLE `tbSequence`")
            db.execSQL("ALTER TABLE `tbSequence_new` RENAME TO `tbSequence`")
        }
    }

    class Migration11To12 : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_descriptionType` INTEGER")
            db.execSQL("UPDATE `tbSequence` SET `_descriptionType`=0")
        }
    }

    class Migration12To13 : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_pin` TEXT")
        }
    }

    class Migration13To14 : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE `tbLog` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`_dt` INTEGER, `_event` INTEGER, `_data` TEXT)"
            )
        }
    }

    class Migration14To15 : Migration(14, 15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_ipv` INTEGER")
            db.execSQL("UPDATE `tbSequence` SET `_ipv`=0")
        }
    }

    class Migration15To16 : Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // NOTE IcmpType.WITH_IP_AND_ICMP_HEADER = 2 -- deprecated
            db.execSQL(
                "UPDATE `tbSequence` SET `_icmp_type`=${IcmpType.WITH_ICMP_HEADER.ordinal} " +
                    "WHERE `_icmp_type`=2"
            )
        }
    }

    class Migration16To17 : Migration(16, 17) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_localPort` INTEGER")
        }
    }

    class Migration17To18 : Migration(17, 18) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE `tbSequence_new` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `_name` TEXT, " +
                    "`_host` TEXT, `_order` INTEGER, `_delay` INTEGER, `_application` TEXT, " +
                    "`_application_name` TEXT, `_icmp_type` INTEGER, `_steps` TEXT, `_description_type` INTEGER, " +
                    "`_pin` TEXT, `_ipv` INTEGER, `_local_port` INTEGER)")
            db.execSQL("INSERT INTO `tbSequence_new` (`_id`, `_name`, `_host`, `_order`, `_delay`, " +
                    "`_application`, `_application_name`, `_icmp_type`, `_steps`, `_description_type`, `_pin`, " +
                    "`_ipv`, `_local_port`) SELECT `_id`, `_name`, `_host`, `_order`, `_delay`, `_application`, " +
                    "`_application_name`, `_icmp_type`, `_steps`, `_descriptionType`, `_pin`, `_ipv`, `_localPort` " +
                    "FROM `tbSequence`")
            db.execSQL("DROP TABLE `tbSequence`")
            db.execSQL("ALTER TABLE `tbSequence_new` RENAME TO `tbSequence`")
        }
    }

    class Migration18To19 : Migration(18, 19) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_ttl` INTEGER")
        }
    }

    class Migration19To20 : Migration(19, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_uri` TEXT")
        }
    }

    class Migration20To21 : Migration(20, 21) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_group` TEXT")
        }
    }

    class Migration21To22 : Migration(21, 22) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_check_access` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_check_type` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_check_port` INTEGER")
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_check_host` TEXT")
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_check_timeout` INTEGER NOT NULL " +
                    "DEFAULT $DEFAULT_CHECK_TIMEOUT")
        }
    }

    class Migration22To23 : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_check_post_knock` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE `tbSequence` ADD COLUMN `_check_retries` INTEGER NOT NULL " +
                    "DEFAULT $DEFAULT_CHECK_RETRIES")
        }
    }

}
