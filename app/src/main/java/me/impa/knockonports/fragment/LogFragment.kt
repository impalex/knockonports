/*
 * Copyright (c) 2020 Alexander Yaburov
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.impa.knockonports.R
import me.impa.knockonports.databinding.FragmentLogBinding
import me.impa.knockonports.viewadapter.LogEntryAdapter
import me.impa.knockonports.viewmodel.MainViewModel

class LogFragment: Fragment() {

    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by lazy { ViewModelProvider(requireActivity()).get(MainViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LogEntryAdapter(requireContext())

        val recycler = binding.recyclerLogEntries
        recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.shape_divider)!!)
        })
        recycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.getLog().collectLatest { adapter.submitData(it) }
        }
    }
}