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

import me.impa.knockonports.R
import me.impa.knockonports.data.type.CheckAccessType
import me.impa.knockonports.data.type.ContentEncodingType
import me.impa.knockonports.data.type.EventType
import me.impa.knockonports.data.type.IcmpType
import me.impa.knockonports.data.type.ProtocolVersionType
import me.impa.knockonports.data.type.SequenceStepType

/**
 * Returns the string resource ID associated with the [ProtocolVersionType].
 *
 * This function maps each [ProtocolVersionType] enum value to a corresponding
 * string resource ID, allowing for the display of user-friendly descriptions
 * for different protocol version preferences within the application.
 *
 * @return An integer representing the string resource ID.  The specific
 *  string associated with each ID is defined in the application's resources.
 */
fun ProtocolVersionType.stringResourceId() = when (this) {
    ProtocolVersionType.PREFER_IPV4 -> R.string.type_protocol_prefer_ipv4
    ProtocolVersionType.PREFER_IPV6 -> R.string.type_protocol_prefer_ipv6
    ProtocolVersionType.ONLY_IPV4 -> R.string.type_protocol_only_ipv4
    ProtocolVersionType.ONLY_IPV6 -> R.string.type_protocol_only_ipv6
    ProtocolVersionType.BOTH -> R.string.type_protocol_both_ipv4_ipv6
}

/**
 * Returns the string resource ID for the given [SequenceStepType].
 *
 * This function maps a [SequenceStepType] to its corresponding string resource ID,
 * which can be used to display a localized string representing the type of sequence step.
 *
 * @return The string resource ID for the given [SequenceStepType].  For example,
 *         if the type is [SequenceStepType.TCP], it will return [R.string.type_sequence_step_tcp].
 */
fun SequenceStepType.stringResourceId() = when (this) {
    SequenceStepType.TCP -> R.string.type_sequence_step_tcp
    SequenceStepType.UDP -> R.string.type_sequence_step_udp
    SequenceStepType.ICMP -> R.string.type_sequence_step_icmp
}

/**
 * Returns the string resource ID corresponding to the [ContentEncodingType].
 *
 * This function maps the enum values of [ContentEncodingType] to their respective string
 * resources, which can be used for displaying the content encoding type in the UI.
 *
 * @return The string resource ID (e.g., R.string.type_content_encoding_hex).
 */
fun ContentEncodingType.stringResourceId() = when (this) {
    ContentEncodingType.HEX -> R.string.type_content_encoding_hex
    ContentEncodingType.RAW -> R.string.type_content_encoding_raw
    ContentEncodingType.BASE64 -> R.string.type_content_encoding_base64
}

/**
 * Returns the string resource ID corresponding to the given [IcmpType].
 *
 * This function provides a mapping between the different [IcmpType] enum values and their
 * corresponding string resource IDs, which can be used to display human-readable descriptions
 * of the ICMP type in the user interface.
 *
 * @return The string resource ID associated with the [IcmpType].
 */
fun IcmpType.stringResourceId() = when (this) {
    IcmpType.WITHOUT_HEADERS -> R.string.type_icmp_size_without_headers
    IcmpType.WITH_ICMP_HEADER -> R.string.type_icmp_size_with_icmp_header
    IcmpType.WITH_IP_AND_ICMP_HEADERS -> R.string.type_icmp_size_all_headers
}

/**
 * Returns the string resource ID associated with the [EventType].
 *
 * This function maps each [EventType] to a corresponding string resource ID,
 * which can be used to display a localized string representation of the event type.
 *
 * @return The string resource ID for the given [EventType].
 */
fun EventType.stringResourceId() = when (this) {
    EventType.KNOCK -> R.string.log_knock
    EventType.EXPORT -> R.string.log_export
    EventType.IMPORT -> R.string.log_import
    EventType.UNKNOWN -> R.string.log_unknown
    EventType.SEQUENCE_SAVED -> R.string.log_sequence_saved
    EventType.SEQUENCE_DELETED -> R.string.log_sequence_deleted
    EventType.ERROR_IMPORT -> R.string.log_error_import
    EventType.ERROR_EXPORT -> R.string.log_error_export
    EventType.ERROR_UNKNOWN -> R.string.log_error_unknown
    EventType.ERROR_NETWORK -> R.string.log_error_network
    EventType.ERROR_INVALID_HOST -> R.string.log_error_invalid_host
    EventType.ERROR_RESOLVE_HOST -> R.string.log_error_resolve_host
    EventType.ERROR_EMPTY_SEQUENCE -> R.string.log_error_empty_sequence
}

/**
 * Returns the string resource ID associated with the [CheckAccessType].
 *
 * This extension function provides a convenient way to map each [CheckAccessType]
 * to its corresponding string resource for display or other purposes.
 *
 * @return The string resource ID ([R.string]) that represents the [CheckAccessType].
 */
fun CheckAccessType.stringResourceId() = when (this) {
    CheckAccessType.URL -> R.string.type_check_access_url
    CheckAccessType.PORT -> R.string.type_check_access_port
    CheckAccessType.PING -> R.string.type_check_access_ping
}