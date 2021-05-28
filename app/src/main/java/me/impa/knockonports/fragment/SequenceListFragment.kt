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
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.impa.knockonports.R
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.databinding.FragmentSequenceListBinding
import me.impa.knockonports.ext.expandTo
import me.impa.knockonports.viewadapter.KnockerItemTouchHelper
import me.impa.knockonports.viewadapter.SequenceAdapter
import me.impa.knockonports.viewmodel.MainViewModel

private const val EXPAND_DURATION = 300L

class SequenceListFragment: Fragment() {

    private var _binding: FragmentSequenceListBinding? = null
    private val binding get() = _binding!!

    private val sequenceAdapter by lazy { SequenceAdapter(requireContext()) }
    private val mainViewModel by lazy { ViewModelProvider(requireActivity()).get(MainViewModel::class.java) }
    private val twoPaneMode by lazy { resources.getBoolean(R.bool.twoPaneMode) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSequenceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = binding.recyclerViewSequences

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!twoPaneMode || mainViewModel.getDirtySequence().value == null)
                    mainViewModel.setFabVisible(dy <= 0)
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
            mainViewModel.setSelectedSequence(it)
        }

        sequenceAdapter.onCreateShortcut = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val shortcutManager = context?.getSystemService(ShortcutManager::class.java)
                if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported) {
                    shortcutManager.requestPinShortcut(Sequence.getShortcutInfo(requireContext(), it, false), null)
                }
            }
        }

        sequenceAdapter.onMove = { _, _ ->
            mainViewModel.setPendingDataChanges(sequenceAdapter.items.asSequence().filter { it.id != null }.map { it.id!! }.toList())
        }

        recycler.adapter = sequenceAdapter

        val touchHelper = ItemTouchHelper(KnockerItemTouchHelper(sequenceAdapter, ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0))
        touchHelper.attachToRecyclerView(recycler)

        mainViewModel.getSequenceList().observe(viewLifecycleOwner, {
            sequenceAdapter.items = it?.toMutableList() ?: mutableListOf()
        })

        if (twoPaneMode) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_seq_config, SequenceConfigFragment())
                    .commit()
            val detailsFrame = binding.fragmentSeqConfig
            mainViewModel.getDirtySequence().observe(viewLifecycleOwner, {
                if (it==null) {
                    detailsFrame?.expandTo(0f, EXPAND_DURATION)
                    recycler.expandTo(1f, EXPAND_DURATION)
                } else {
                    if (it.id == null) {
                        detailsFrame?.expandTo(1f, EXPAND_DURATION)
                        recycler.expandTo(0f, EXPAND_DURATION)
                    } else {
                        detailsFrame?.expandTo(2f, EXPAND_DURATION)
                        recycler.expandTo(1f, EXPAND_DURATION)
                    }
                }
            })
        }


    }

}