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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import me.impa.knockonports.R
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.viewmodel.MainViewModel

class AdvancedSettingsFragment: Fragment() {

    private val mainViewModel: MainViewModel by lazy { ViewModelProviders.of(activity).get(MainViewModel::class.java) }
    private lateinit var delayEdit: TextInputEditText
    private lateinit var timeoutEdit: TextInputEditText
    private lateinit var udpContentEdit: TextInputEditText
    private lateinit var appSpinner: Spinner
    private lateinit var base64CheckBox: CheckBox

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater!!.inflate(R.layout.fragment_sequence_config_advanced, container, false)

        delayEdit = view.findViewById(R.id.edit_sequence_delay)
        timeoutEdit = view.findViewById(R.id.edit_sequence_timeout)
        udpContentEdit = view.findViewById(R.id.edit_udpcontent)
        appSpinner = view.findViewById(R.id.spinner_apps)
        base64CheckBox = view.findViewById(R.id.checkbox_base64)

        mainViewModel.getDirtySequence().observe(this, Observer {
            delayEdit.setText(it?.delay?.toString())
            timeoutEdit.setText(it?.timeout?.toString())
            udpContentEdit.setText(it?.udpContent)
            base64CheckBox.isChecked = it?.base64 == 1
        })

        base64CheckBox.setOnCheckedChangeListener { _, b ->
            mainViewModel.getDirtySequence().value?.base64 = if (b) {
                1
            } else {
                0
            }
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

        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, mainViewModel.getInstalledApps().value ?: listOf())

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appSpinner.adapter = adapter
        //appSpinner.selectedItem = mainViewModel.getInstalledApps().value?.first { it.app == mainViewModel.getDirtySequence().value?.application }
        appSpinner.setSelection(mainViewModel.getInstalledApps().value?.indexOfFirst { it.app == mainViewModel.getDirtySequence().value?.application } ?: 0)
        appSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mainViewModel.getDirtySequence().value?.application = mainViewModel.getInstalledApps().value?.get(p2)?.app
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                mainViewModel.getDirtySequence().value?.application = null
            }

        }

        return view
    }
}