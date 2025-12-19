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

package me.impa.knockonports.screen.component.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.toPersistentMap
import me.impa.knockonports.R
import me.impa.knockonports.constants.CHECK_PERIOD_STEP
import me.impa.knockonports.constants.MAX_CHECK_PERIOD
import me.impa.knockonports.constants.MAX_IP4_HEADER_SIZE
import me.impa.knockonports.constants.MIN_CHECK_PERIOD
import me.impa.knockonports.constants.MIN_IP4_HEADER_SIZE
import me.impa.knockonports.constants.MS_IN_SECOND
import me.impa.knockonports.data.settings.Ipv4ProviderMap
import me.impa.knockonports.data.settings.Ipv6ProviderMap
import me.impa.knockonports.data.settings.PROVIDER_CUSTOM
import me.impa.knockonports.screen.component.common.HeaderSection
import me.impa.knockonports.screen.component.common.PrefCustomProviderEditor
import me.impa.knockonports.screen.component.common.PrefDescriptionClickable
import me.impa.knockonports.screen.component.common.PrefMultiSelection
import me.impa.knockonports.screen.component.common.PrefStepSlider
import me.impa.knockonports.screen.component.common.PrefSwitch
import me.impa.knockonports.screen.viewmodel.state.settings.GeneralUiState
import me.impa.knockonports.screen.viewmodel.state.settings.UiEvent

fun LazyListScope.generalSection(
    config: GeneralUiState,
    onEvent: (UiEvent) -> Unit = {}
) {
    item(key = "general") {
        HeaderSection(title = stringResource(R.string.title_settings_general), false)
    }
    appLockSection(config, onEvent)
    item(key = "widget_confirmation") {
        PrefSwitch(
            title = stringResource(R.string.title_settings_widget_confirmation),
            description = stringResource(R.string.text_settings_widget_confirmation_desc),
            value = config.widgetConfirmation,
            onClick = { onEvent(UiEvent.SetWidgetConfirmation(!config.widgetConfirmation)) }
        )
    }
    item(key = "detect_ip") {
        PrefSwitch(
            title = stringResource(R.string.title_settings_detect_ip),
            description = stringResource(R.string.text_settings_detect_ip),
            value = config.detectPublicIP,
            onClick = { onEvent(UiEvent.SetIPDetection(!config.detectPublicIP)) }
        )
    }
    if (config.detectPublicIP) {
        ipvSection(config, onEvent)
    }
    item(key = "resource_check_interval") {
        val periodInSeconds = config.resourceCheckPeriod / MS_IN_SECOND
        PrefStepSlider(
            title = stringResource(R.string.title_settings_resource_check_interval),
            description = pluralStringResource(
                R.plurals.text_settings_resource_check_interval,
                periodInSeconds, periodInSeconds
            ),
            value = config.resourceCheckPeriod,
            minValue = MIN_CHECK_PERIOD,
            maxValue = MAX_CHECK_PERIOD,
            steps = (MAX_CHECK_PERIOD - MIN_CHECK_PERIOD) / CHECK_PERIOD_STEP,
            onChanged = { onEvent(UiEvent.SetResourceCheckPeriod(it)) }
        )
    }
    item(key = "enable_custom_ip4_hdr") {
        PrefSwitch(
            title = stringResource(R.string.title_settings_enable_custom_ip_hdr),
            description = stringResource(R.string.text_settings_enable_custom_ip_hdr),
            value = config.customIp4Header,
            onClick = { onEvent(UiEvent.SetCustomIPHeaderSizeEnabled((!config.customIp4Header))) })
    }
    if (config.customIp4Header) {
        item(key = "custom_ip4_hdr") {
            val sizeText = stringResource(R.string.text_settings_ip4_hdr_size, config.ip4HeaderSize)
            PrefStepSlider(
                title = stringResource(R.string.title_settings_ip4_hdr_size),
                description = sizeText,
                value = config.ip4HeaderSize,
                minValue = MIN_IP4_HEADER_SIZE,
                maxValue = MAX_IP4_HEADER_SIZE,
                steps = (MAX_IP4_HEADER_SIZE - MIN_IP4_HEADER_SIZE) / 4,
                onChanged = { onEvent(UiEvent.SetCustomIPHeaderSize(it)) })

        }
    }
}

private fun LazyListScope.appLockSection(
    config: GeneralUiState,
    onEvent: (UiEvent) -> Unit
) {
    item(key = "app_lock") {
        val description = if (config.isBiometricAuthAvailable) {
            stringResource(R.string.text_settings_app_lock_desc)
        } else {
            stringResource(R.string.text_settings_app_lock_not_available_desc)
        }

        if (config.isBiometricAuthAvailable) {
            PrefSwitch(
                title = stringResource(R.string.title_settings_app_lock),
                description = description,
                value = config.isAppLockEnabled,
                onClick = {
                    onEvent(UiEvent.ToggleAuth(!config.isAppLockEnabled))
                }
            )
        } else {
            PrefDescriptionClickable(
                title = stringResource(R.string.title_settings_app_lock),
                subtitle = stringResource(R.string.text_settings_app_lock_not_available_desc),
                onClick = { onEvent(UiEvent.OpenSecuritySettings) }
            )
        }
    }
}

private fun LazyListScope.ipvSection(
    config: GeneralUiState,
    onEvent: (UiEvent) -> Unit
) {
    item(key = "ipv4_service") {
        val resources = LocalResources.current
        val serviceMap = remember(resources) {
            Ipv4ProviderMap.mapValues { entry -> resources.getString(entry.value) }.toPersistentMap()
        }
        PrefMultiSelection(
            title = stringResource(R.string.title_settings_ipv4_lookup_provider),
            value = config.ipv4Service,
            map = serviceMap,
            onChanged = { onEvent(UiEvent.SetIpv4Service(it)) })
        if (config.ipv4Service == PROVIDER_CUSTOM) {
            PrefCustomProviderEditor(
                title = stringResource(R.string.title_settings_custom_ipv4_provider),
                value = config.customIpv4Service,
                onChanged = { onEvent(UiEvent.SetCustomIpv4Service(it)) }
            )
        }
    }
    item(key = "ipv6_service") {
        val resources = LocalResources.current
        val serviceMap = remember(resources) {
            Ipv6ProviderMap.mapValues { entry -> resources.getString(entry.value) }.toPersistentMap()
        }
        PrefMultiSelection(
            title = stringResource(R.string.title_settings_ipv6_lookup_provider),
            value = config.ipv6Service,
            map = serviceMap,
            onChanged = { onEvent(UiEvent.SetIpv6Service(it)) })
        if (config.ipv6Service == PROVIDER_CUSTOM) {
            PrefCustomProviderEditor(
                title = stringResource(R.string.title_settings_custom_ipv6_provider),
                value = config.customIpv6Service,
                onChanged = { onEvent(UiEvent.SetCustomIpv6Service(it)) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewGeneralSection() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        generalSection(
            GeneralUiState(
                isAppLockEnabled = true,
                widgetConfirmation = false,
                detectPublicIP = false,
                ipv4Service = PROVIDER_CUSTOM,
                ipv6Service = PROVIDER_CUSTOM,
                customIpv4Service = "",
                customIpv6Service = "",
                customIp4Header = false,
                ip4HeaderSize = 20,
                resourceCheckPeriod = 5000,
                isBiometricAuthAvailable = true
            )
        )
    }
}