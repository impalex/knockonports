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

package me.impa.knockonports.screen.component.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.toPersistentMap
import me.impa.knockonports.data.settings.AppSettings
import me.impa.knockonports.R
import me.impa.knockonports.data.settings.Ipv4ProviderMap
import me.impa.knockonports.data.settings.Ipv6ProviderMap
import me.impa.knockonports.data.settings.PROVIDER_CUSTOM

fun LazyListScope.generalSection(
    config: AppSettings,
    onWidgetConfirmationChanged: (Boolean) -> Unit = {},
    onDetectPublicIPChanged: (Boolean) -> Unit = {},
    onDetailedListViewChanged: (Boolean) -> Unit = {},
    onIpv4ServiceChanged: (String) -> Unit = {},
    onIpv6ServiceChanged: (String) -> Unit = {},
    onIpv4CustomServiceChanged: (String) -> Unit = {},
    onIpv6CustomServiceChanged: (String) -> Unit = {}
) {
    val detailedListView = config.detailedListView
    val widgetConfirmation = config.widgetConfirmation
    val detectPublicIP = config.detectPublicIP
    item(key = "general") {
        HeaderSection(title = stringResource(R.string.title_settings_general), false)
    }
    item(key = "detailed_list_view") {
        PrefSwitch(
            title = stringResource(R.string.title_settings_detailed_list_view),
            description = stringResource(R.string.text_settings_detailed_list_view),
            value = detailedListView,
            onClick = { onDetailedListViewChanged(!detailedListView) }
        )
    }
    item(key = "widget_confirmation") {
        PrefSwitch(
            title = stringResource(R.string.title_settings_widget_confirmation),
            description = stringResource(R.string.text_settings_widget_confirmation_desc),
            value = widgetConfirmation,
            onClick = { onWidgetConfirmationChanged(!widgetConfirmation) }
        )
    }
    item(key = "detect_ip") {
        var askForConfirmation by rememberSaveable { mutableStateOf(false) }
        if (askForConfirmation) {
            DetectIPAlert(onDismiss = { askForConfirmation = false }, onConfirm = {
                onDetectPublicIPChanged(true)
                askForConfirmation = false
            })
        }
        PrefSwitch(
            title = stringResource(R.string.title_settings_detect_ip),
            description = stringResource(R.string.text_settings_detect_ip),
            value = detectPublicIP,
            onClick = {
                if (detectPublicIP) {
                    onDetectPublicIPChanged(false)
                } else {
                    askForConfirmation = true
                }
            }
        )
    }
    if (detectPublicIP) {
        ipvSection(
            config,
            onIpv4ServiceChanged, onIpv4CustomServiceChanged,
            onIpv6ServiceChanged, onIpv6CustomServiceChanged
        )
    }
}

private fun LazyListScope.ipvSection(
    config: AppSettings,
    onIpv4ServiceChanged: (String) -> Unit = {},
    onIpv4CustomServiceChanged: (String) -> Unit = {},
    onIpv6ServiceChanged: (String) -> Unit = {},
    onIpv6CustomServiceChanged: (String) -> Unit = {}
) {
    val ipv4Service = config.ipv4Service
    val ipv4CustomService = config.customIpv4Service
    val ipv6Service = config.ipv6Service
    val ipv6CustomService = config.customIpv6Service
    item(key = "ipv4_service") {
        val resources = LocalContext.current.resources
        val serviceMap = remember(resources) {
            Ipv4ProviderMap.mapValues { entry -> resources.getString(entry.value) }.toPersistentMap()
        }
        PrefMultiSelection(
            title = stringResource(R.string.title_settings_ipv4_lookup_provider),
            value = ipv4Service,
            map = serviceMap,
            onChanged = { onIpv4ServiceChanged(it) })
        if (ipv4Service == PROVIDER_CUSTOM) {
            PrefCustomProviderEditor(
                title = stringResource(R.string.title_settings_custom_ipv4_provider),
                value = ipv4CustomService,
                onChanged = { onIpv4CustomServiceChanged(it) }
            )
        }
    }
    item(key = "ipv6_service") {
        val resources = LocalContext.current.resources
        val serviceMap = remember(resources) {
            Ipv6ProviderMap.mapValues { entry -> resources.getString(entry.value) }.toPersistentMap()
        }
        PrefMultiSelection(
            title = stringResource(R.string.title_settings_ipv6_lookup_provider),
            value = ipv6Service,
            map = serviceMap,
            onChanged = { onIpv6ServiceChanged(it) })
        if (ipv6Service == PROVIDER_CUSTOM) {
            PrefCustomProviderEditor(
                title = stringResource(R.string.title_settings_custom_ipv6_provider),
                value = ipv6CustomService,
                onChanged = { onIpv6CustomServiceChanged(it) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewGeneralSection() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        generalSection(AppSettings(detectPublicIP = true))
    }
}