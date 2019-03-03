/*
 * Copyright (c) 2018 Alexander Yaburov
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

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import me.impa.knockonports.R
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.viewmodel.MainViewModel

class AdvancedSettingsFragment: Fragment() {

    private val mainViewModel by lazy { ViewModelProviders.of(activity!!).get(MainViewModel::class.java) }
    private val delayEdit by lazy { view!!.findViewById<TextInputEditText>(R.id.edit_sequence_delay) }
    private val udpContentEdit by lazy { view!!.findViewById<TextInputEditText>(R.id.edit_udpcontent) }
    private val base64CheckBox by lazy { view!!.findViewById<CheckBox>(R.id.checkbox_base64) }
    private val appNameText by lazy { view!!.findViewById<TextView>(R.id.text_app_name) }
    private val downArrow by lazy { view!!.findViewById<ImageView>(R.id.image_app_down) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sequence_config_advanced, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.getDirtySequence().observe(this, Observer {
            delayEdit.setText(it?.delay?.toString())
            udpContentEdit.setText(it?.udpContent)
            base64CheckBox.isChecked = it?.base64 == 1
            showAppName(it?.application, it?.applicationName)
        })

        appNameText.setOnClickListener { showAppChooser() }
        downArrow.setOnClickListener { showAppChooser() }

        base64CheckBox.setOnCheckedChangeListener { _, b ->
            mainViewModel.getDirtySequence().value?.base64 = if (b) 1 else 0
        }

        delayEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.delay = it.toIntOrNull()
        }
        udpContentEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.udpContent = it
        }
    }

    private fun showAppName(appId: String?, defaultName: String? = null) {
        val app = mainViewModel.getInstalledApps().value?.firstOrNull { it.app == appId }
        appNameText.text = app?.name ?: when {
            appId.isNullOrEmpty() -> getString(R.string.none)
            defaultName.isNullOrEmpty() -> appId
            else -> defaultName
        }
    }

    private fun showAppChooser() {
        val ft = childFragmentManager.beginTransaction()
        val f = childFragmentManager.findFragmentByTag(AppChooserFragment.FRAGMENT_APP_CHOOSER)
        if (f != null)
            ft.remove(f)
        ft.commit()
        AppChooserFragment().apply {
            onSelected = {
                mainViewModel.getDirtySequence().value?.application = it.app
                mainViewModel.getDirtySequence().value?.applicationName = it.name
                showAppName(it.app)
                dismiss()
            }
        }.show(childFragmentManager, AppChooserFragment.FRAGMENT_APP_CHOOSER)
    }

}