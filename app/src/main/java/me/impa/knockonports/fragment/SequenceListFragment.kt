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

import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.impa.knockonports.R
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.viewadapter.KnockerItemTouchHelper
import me.impa.knockonports.viewadapter.SequenceAdapter
import me.impa.knockonports.viewmodel.MainViewModel

class SequenceListFragment: androidx.fragment.app.Fragment() {

    private val sequenceAdapter by lazy { SequenceAdapter(context!!) }
    private val mainViewModel by lazy { ViewModelProviders.of(activity!!).get(MainViewModel::class.java) }
    private val twoPaneMode by lazy { resources.getBoolean(R.bool.twoPaneMode) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sequence_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_sequences)

        recycler.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!twoPaneMode || mainViewModel.getDirtySequence().value == null)
                    mainViewModel.getFabVisible().value = dy <= 0
            }
        })

        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        sequenceAdapter.onKnock = {
            mainViewModel.knock(it)
        }

        sequenceAdapter.onDelete = {
            mainViewModel.deleteSequence(it)
        }

        sequenceAdapter.onClick = {
            mainViewModel.getSelectedSequence().value = it
        }

        sequenceAdapter.onCreateShortcut = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val shortcutManager = context?.getSystemService(ShortcutManager::class.java)
                if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported) {
                    shortcutManager.requestPinShortcut(Sequence.getShortcutInfo(context!!, it, false), null)
                }
            }
        }

        sequenceAdapter.onMove = { _, _ ->
            mainViewModel.getPendingDataChanges().value = sequenceAdapter.items.asSequence().filter { it.id != null }.map { it.id!! }.toList()
        }

        recycler.adapter = sequenceAdapter

        val touchHelper = ItemTouchHelper(KnockerItemTouchHelper(sequenceAdapter, ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0))
        touchHelper.attachToRecyclerView(recycler)

        mainViewModel.getSequenceList().observe(this, Observer {
            sequenceAdapter.items = it?.toMutableList() ?: mutableListOf()
        })


    }

}