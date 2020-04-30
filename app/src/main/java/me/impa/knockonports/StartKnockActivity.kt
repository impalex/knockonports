/*
 * Copyright (c) 2019 Alexander Yaburov
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

package me.impa.knockonports

import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.viewmodel.MainViewModel

class StartKnockActivity : AppCompatActivity() {

    private val mainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val seqId = intent.getLongExtra(EXTRA_SEQ_ID, 0)
        val seq = mainViewModel.findSequence(seqId)
        if (seq != null) {
            mainViewModel.knock(seq)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortcutManager = getSystemService(ShortcutManager::class.java)
                shortcutManager?.reportShortcutUsed(Sequence.shortcutId(seqId))
            }
        }
        finish()
    }
}