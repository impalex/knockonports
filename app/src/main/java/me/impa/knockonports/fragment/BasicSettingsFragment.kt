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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import com.github.stephenvinouze.advancedrecyclerview.gesture.extensions.enableGestures
import me.impa.knockonports.R
import me.impa.knockonports.database.entity.Port
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.viewadapter.PortAdapter
import me.impa.knockonports.viewmodel.MainViewModel

class BasicSettingsFragment : Fragment() {

    private val mainViewModel: MainViewModel by lazy { ViewModelProviders.of(activity).get(MainViewModel::class.java) }
    private lateinit var nameEdit: TextInputEditText
    private lateinit var hostEdit: TextInputEditText
    private lateinit var portAdapter: PortAdapter
    private lateinit var addPortButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_sequence_config_basic, container, false)

        nameEdit = view.findViewById(R.id.edit_sequence_name)
        hostEdit = view.findViewById(R.id.edit_sequence_host)
        addPortButton = view.findViewById(R.id.button_add_port)
        scrollView = view.findViewById(R.id.basic_settings_view)

        mainViewModel.getDirtySequence().observe(this, Observer {
            nameEdit.setText(it?.name)
            hostEdit.setText(it?.host)
        })

        nameEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.name = it
        }

        hostEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.host = it
        }

        recyclerView = view.findViewById(R.id.recycler_ports)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        portAdapter = PortAdapter(activity)
        recyclerView.adapter = portAdapter
        recyclerView.enableGestures(dragDirections = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                swipeDirections = 0)

        mainViewModel.getDirtyPorts().observe(this, Observer {
            portAdapter.items = it ?: mutableListOf()
        })
        addPortButton.setOnClickListener{
            val model = Port(null, 0, null, Port.PORT_TYPE_UDP)
            portAdapter.addItem(model, portAdapter.itemCount)
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
                recyclerView.getChildAt(portAdapter.itemCount - 1).requestFocus()
            }
        }

        return view
    }

}