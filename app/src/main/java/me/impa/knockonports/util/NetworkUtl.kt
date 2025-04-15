/*
 * Copyright (c) 2025 Alexander Yaburov
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

package me.impa.knockonports.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.URLUtil
import timber.log.Timber
import java.net.URL
import java.util.regex.Pattern

/**
 * Checks if a network connection is currently available.
 *
 * This function determines network availability by examining the active network
 * and its capabilities.  It checks for the presence of Wi-Fi, Ethernet, cellular,
 * or VPN transports.  If any of these transports are active, the function
 * returns `true`, indicating network connectivity.  If no transports are active
 * or if an exception occurs during the process (e.g., due to missing permissions
 * or connectivity service issues), the function returns `false`.
 *
 * @param context The application or activity context used to access system services.
 * @return `true` if a network connection is available, `false` otherwise.
 */
fun isNetworkAvailable(context: Context) = try {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork
    val caps = connectivityManager.getNetworkCapabilities(networkCapabilities)!!
    when {
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
        else -> false
    }
} catch (_: Exception) {
    false
}

enum class IpAddressType {
    IPV4,
    IPV6
}

private val ipRegexMap = mapOf(
    IpAddressType.IPV4 to Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
            "(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$"),
    IpAddressType.IPV6 to Pattern.compile("^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|" +
            "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
            "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
            "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
            ":((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
            "::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|" +
            "1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}" +
            "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$")
)

/**
 * Retrieves the public IP address of the device by querying a specified service URL.
 *
 * The function attempts to connect to the provided URL, reads the response, and validates it
 * against a regex pattern based on the provided [IpAddressType].  If the response is a valid IP
 * address of the specified type, it is returned; otherwise, null is returned.
 *
 * @param serviceUrl The URL of the service that provides the public IP address.  Must be a valid URL.
 *        If null or invalid, the function will log an error and return null.
 * @param type The type of IP address expected (IPv4 or IPv6).  Used to determine the appropriate regex for validation.
 * @return The public IP address as a String if successfully retrieved and validated; otherwise, null.
 *
 * **Example:**
 * ```kotlin
 * val ipAddress = getPublicIp("https://api.ipify.org", IpAddressType.IPv4)
 * if (ipAddress != null) {
 *     Timber.i("Public IP address: $ipAddress")
 * } else {
 *     Timber.e("Failed to retrieve public IP address.")
 * }
 * ```
 */
fun getPublicIp(serviceUrl: String?, type: IpAddressType): String? {
    if (!URLUtil.isValidUrl(serviceUrl)) {
        Timber.e("Invalid URL: $serviceUrl")
        return null
    }

    Timber.i("Trying to get public IP address from $serviceUrl")

    return try {
        val response = URL(serviceUrl).openConnection().apply {
            connectTimeout = 5000
            readTimeout = 5000
        }.getInputStream().bufferedReader().use { it.readText() }.trim()
        if (ipRegexMap[type]?.matcher(response)?.matches() == true) response else null
    } catch (e: Exception) {
        Timber.e(e, "Error getting public IP address")
        null
    }
}