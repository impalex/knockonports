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

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.textfield.TextInputEditText
import me.impa.knockonports.R
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.ext.validate
import me.impa.knockonports.json.SequenceStep
import me.impa.knockonports.util.HintManager
import me.impa.knockonports.viewadapter.KnockerItemTouchHelper
import me.impa.knockonports.viewadapter.SequenceStepsAdapter
import me.impa.knockonports.viewmodel.MainViewModel

class BasicSettingsFragment : Fragment() {

    private val mainViewModel by lazy { ViewModelProvider(activity!!).get(MainViewModel::class.java) }
    private val nameEdit by lazy { view!!.findViewById<TextInputEditText>(R.id.edit_sequence_name) }
    private val hostEdit by lazy { view!!.findViewById<TextInputEditText>(R.id.edit_sequence_host) }
    private val scrollView by lazy { view!!.findViewById<ScrollView>(R.id.basic_settings_view) }
    private val stepsRecyclerView by lazy { view!!.findViewById<RecyclerView>(R.id.recycler_steps) }
    private val addStepsButton by lazy { view!!.findViewById<Button>(R.id.button_add_step) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sequence_config_basic, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stepsAdapter = SequenceStepsAdapter(context!!)

        mainViewModel.getDirtySequence().observe(viewLifecycleOwner, Observer {
            nameEdit.setText(it?.name)
            hostEdit.setText(it?.host)
        })

        mainViewModel.getDirtySteps().observe(viewLifecycleOwner, Observer {
            stepsAdapter.items = it ?: mutableListOf()
        })

        stepsRecyclerView.layoutManager = LinearLayoutManager(activity)
        stepsRecyclerView.adapter = stepsAdapter
        stepsRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                setDrawable(ContextCompat.getDrawable(context!!, R.drawable.shape_divider)!!)
        })
        val icmpTouchHelper = ItemTouchHelper(KnockerItemTouchHelper(stepsAdapter, ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT))
        icmpTouchHelper.attachToRecyclerView(stepsRecyclerView)
        /* Disable it for now
         stepsAdapter.onStartDrag = {
            icmpTouchHelper.startDrag(it)
        }*/

        addStepsButton.setOnClickListener {
            val lastStepType = stepsAdapter.items.lastOrNull()?.type ?: SequenceStepType.UDP

            val model = SequenceStep(lastStepType, null, null, null, null, ContentEncoding.RAW).apply {
                icmpSizeOffset = mainViewModel.getDirtySequence().value?.icmpType?.offset ?: 0
            }
            stepsAdapter.addItem(model)
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
                stepsRecyclerView.post {
                    stepsRecyclerView.getChildAt(stepsRecyclerView.childCount - 1).requestFocus()
                    HintManager.showHint(context!!, HintManager.Hint.DELETE_ROW)
                }
            }
        }


        nameEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.name = it
        }

        nameEdit.validate {
            if (it.isEmpty()) getString(R.string.error_empty_sequence_name) else null
        }

        hostEdit.afterTextChanged {
            mainViewModel.getDirtySequence().value?.host = it
        }

        hostEdit.validate {
            if (it.isEmpty()) getString(R.string.error_empty_host) else null
        }

    }
}