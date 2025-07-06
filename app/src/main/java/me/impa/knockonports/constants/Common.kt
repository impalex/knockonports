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

package me.impa.knockonports.constants

const val MS_IN_SECOND = 1000

const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L

// 5 days
const val POSTPONE_TIME = 5 * ONE_DAY_IN_MILLIS

// 7 days
const val POSTPONE_TIME_START = 7 * ONE_DAY_IN_MILLIS

// 1 day
const val POSTPONE_TIME_CANCEL = ONE_DAY_IN_MILLIS

// Wait for some knocks before showing review dialog
const val REVIEW_KNOCKS_REQUIRED = 20L

const val MIN_PORT = 1
const val MAX_PORT = 65535
const val MIN_TTL = 0
const val MAX_TTL = 255
const val MIN_SLEEP = 0
const val MAX_SLEEP = 15000
const val MIN_IP4_HEADER_SIZE = 20
const val MAX_IP4_HEADER_SIZE = 60
const val IP6_HEADER_SIZE = 40
const val ICMP_HEADER_SIZE = 8
const val MAX_PACKET_SIZE = 65535
const val RADIX_BASE16 = 16
const val MIN_CHECK_TIMEOUT = 1
const val MAX_CHECK_TIMEOUT = 10
const val DEFAULT_CHECK_TIMEOUT = 3
const val CHECK_RESOURCE_TIMER_RESOLUTION = 10 * 1000L
const val MIN_CHECK_PERIOD = 30 * 1000
const val MAX_CHECK_PERIOD = 5 * 60 * 1000
const val DEFAULT_CHECK_PERIOD = 90 * 1000
const val CHECK_PERIOD_STEP = 15 * 1000
const val MAX_CHECK_RETRIES = 5
const val MIN_CHECK_RETRIES = 1
const val DEFAULT_CHECK_RETRIES = 2

const val DEFAULT_TITLE_FONT_SCALE = 100
const val MIN_TITLE_FONT_SCALE = 50
const val MAX_TITLE_FONT_SCALE = 150
const val TITLE_FONT_SCALE_STEP = 5

const val KEEP_LAST_LOG_ENTRY_COUNT = 100

const val INVALID_SEQ_ID = Long.MIN_VALUE

const val EXTRA_SEQ_ID = "EXTRA_SEQ_ID"
const val EXTRA_SOURCE = "EXTRA_SOURCE"
const val EXTRA_VALUE_SOURCE_WIDGET = "WIDGET"
const val EXTRA_VALUE_SOURCE_SHORTCUT = "SHORTCUT"

const val CURRENT_BETA_TEST_MESSAGE = ""