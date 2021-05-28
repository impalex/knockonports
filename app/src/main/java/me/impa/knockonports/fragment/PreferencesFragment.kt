/*
 * Copyright (c) 2021 Alexander Yaburov
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

package me.impa.knockonports.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.impa.knockonports.BuildConfig
import me.impa.knockonports.R
import me.impa.knockonports.util.*

class PreferencesFragment: PreferenceFragmentCompat(), Logging {

    private val versionPref by lazy { findPreference<Preference>(AppPrefs.KEY_ABOUT_VERSION) }
    private val sourcePref by lazy { findPreference<Preference>(AppPrefs.KEY_SOURCE) }
    private val reportPref by lazy { findPreference<Preference>(AppPrefs.KEY_REPORT_ISSUE) }
    private val contactPref by lazy { findPreference<Preference>(AppPrefs.KEY_CONTACT_AUTHOR) }

    private var verLastClick = 0L
    private var verClickCounter = 0
    private var disableEaster = false

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) =
            addPreferencesFromResource(R.xml.settings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        versionPref?.summary = BuildConfig.VERSION_NAME
        versionPref?.setOnPreferenceClickListener {
            if (!disableEaster) {
                val clickTime = System.currentTimeMillis()
                if (clickTime - verLastClick < 1000)
                    verClickCounter++
                else
                    verClickCounter = 0
                verLastClick = clickTime
                if (verClickCounter == 10) {
                    disableEaster = true
                    requireContext().toast(R.string.lil_easter)
                }
            }
            true
        }
        sourcePref?.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/impalex/knockonports")))
            true
        }
        reportPref?.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/impalex/knockonports/issues")))
            true
        }
        contactPref?.setOnPreferenceClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.author_contact)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            }
            try {
                startActivity(try {
                    Intent.createChooser(emailIntent, it.title)
                } catch (e: Exception) {
                    warn { "Unable to start chooser" }
                    throw e
                })
            } catch (_: Exception) {
                warn { "Unable to start email client" }
            }
            true
        }
    }
}