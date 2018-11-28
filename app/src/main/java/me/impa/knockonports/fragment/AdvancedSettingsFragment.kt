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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
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

    private val mainViewModel by lazy { ViewModelProviders.of(activity).get(MainViewModel::class.java) }
    private lateinit var delayEdit: TextInputEditText
    private lateinit var timeoutEdit: TextInputEditText
    private lateinit var udpContentEdit: TextInputEditText
    private lateinit var base64CheckBox: CheckBox
    private lateinit var downArrow: ImageView
    private lateinit var appNameText: TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater!!.inflate(R.layout.fragment_sequence_config_advanced, container, false)

        delayEdit = view.findViewById(R.id.edit_sequence_delay)
        timeoutEdit = view.findViewById(R.id.edit_sequence_timeout)
        udpContentEdit = view.findViewById(R.id.edit_udpcontent)
        base64CheckBox = view.findViewById(R.id.checkbox_base64)
        appNameText = view.findViewById(R.id.text_app_name)
        downArrow = view.findViewById(R.id.image_app_down)

        mainViewModel.getDirtySequence().observe(this, Observer {
            delayEdit.setText(it?.delay?.toString())
            timeoutEdit.setText(it?.timeout?.toString())
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
        timeoutEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.timeout = it.toIntOrNull()
        }
        udpContentEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.udpContent = it
        }

        return view
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
        /*
        val apps = mainViewModel.getInstalledApps().value
        if (apps == null) {
            // not loaded yet
            loadingDialog?.dismiss()
            //loadingDialog = indeterminateProgressDialog(R.string.loading_apps, R.string.please_wait)
            //loadingDialog = ProgressDialog(this)
            doAsync {
                val appList = AppData.loadInstalledApps(context)
                uiThread {
                    mainViewModel.getInstalledApps().value = appList
                    loadingDialog?.dismiss()
                    loadingDialog = null
                    showAppChooser()
                }
            }
            return
        }
        activity.selector(title = getString(R.string.installed_apps), items = apps.map { it.name }, onClick = { _, i ->
            mainViewModel.getDirtySequence().value?.application = apps[i].app
            mainViewModel.getDirtySequence().value?.applicationName = apps[i].name
            showAppName(apps[i].app)
        })*/
    }

}