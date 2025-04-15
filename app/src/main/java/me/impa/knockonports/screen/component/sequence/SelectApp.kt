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

package me.impa.knockonports.screen.component.sequence

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import me.impa.knockonports.R
import me.impa.knockonports.screen.component.common.DialogItemsSource
import me.impa.knockonports.screen.component.common.DialogMenu
import me.impa.knockonports.util.AppData
import timber.log.Timber

@Composable
fun SelectApp(appPackage: String?, appName: String?, onUpdate: (String?, String?) -> Unit) {
    val context = LocalContext.current
    var appList by remember { mutableStateOf<List<AppData>?>(null) }
    DialogMenu(
        label = stringResource(R.string.field_launch_app),
        itemsSource = DialogItemsSource.AsyncItems(
            loadItems = {
                if (appList == null) {
                    appList = AppData.loadInstalledApps(context)
                }
                appList ?: listOf<AppData>()
            },
            selectedIndex = {
                appList?.indexOfFirst { it.app == appPackage } ?: -1
            }),
        selectedItemToString = { item -> item.name },
        unsetValueText = appName ?: stringResource(R.string.list_app_none),
        onItemSelected = { _, item ->
            Timber.d("Selected application: [${item.app}] - ${item.name}")
            if (item.app == "") {
                onUpdate(null, null)
            } else {
                onUpdate(item.app, item.name)
            }
        }
    )
}