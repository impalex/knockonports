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

package me.impa.knockonports.data.settings

import android.content.Context
import android.content.pm.ShortcutManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import me.impa.knockonports.util.isInstalledFromPlayStore
import javax.inject.Inject

interface DeviceState {
    val areShortcutsAvailable: Boolean
    val isPlayStoreInstallation: Boolean
}

class DeviceStateImpl @Inject constructor(@ApplicationContext private val context: Context) : DeviceState {

    override val areShortcutsAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            context.getSystemService(ShortcutManager::class.java)?.isRequestPinShortcutSupported == true

    override val isPlayStoreInstallation = isInstalledFromPlayStore(context.packageManager)

}