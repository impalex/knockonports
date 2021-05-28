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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import me.impa.knockonports.R
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.databinding.FragmentSequenceConfigBasicBinding
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.ext.validate
import me.impa.knockonports.json.SequenceStep
import me.impa.knockonports.util.HintManager
import me.impa.knockonports.viewadapter.KnockerItemTouchHelper
import me.impa.knockonports.viewadapter.SequenceStepsAdapter
import me.impa.knockonports.viewmodel.MainViewModel

class BasicSettingsFragment : Fragment() {

    private var _binding: FragmentSequenceConfigBasicBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by lazy { ViewModelProvider(requireActivity()).get(MainViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSequenceConfigBasicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stepsAdapter = SequenceStepsAdapter(requireContext())

        mainViewModel.getDirtySequence().observe(viewLifecycleOwner, {
            binding.editSequenceName.setText(it?.name)
            binding.editSequenceHost.setText(it?.host)
        })

        mainViewModel.getDirtySteps().observe(viewLifecycleOwner, {
            stepsAdapter.items = it ?: mutableListOf()
        })

        binding.recyclerSteps.layoutManager = LinearLayoutManager(activity)
        binding.recyclerSteps.adapter = stepsAdapter
        binding.recyclerSteps.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.shape_divider)!!)
        })
        val icmpTouchHelper = ItemTouchHelper(KnockerItemTouchHelper(stepsAdapter, ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT))
        icmpTouchHelper.attachToRecyclerView(binding.recyclerSteps)
        /* Disable it for now
         stepsAdapter.onStartDrag = {
            icmpTouchHelper.startDrag(it)
        }*/

        binding.buttonAddStep.setOnClickListener {
            val lastStepType = stepsAdapter.items.lastOrNull()?.type ?: SequenceStepType.UDP

            val model = SequenceStep(lastStepType, null, null, null, null, ContentEncoding.RAW).apply {
                icmpSizeOffset = mainViewModel.getDirtySequence().value?.icmpType?.getOffset() ?: 0
            }
            stepsAdapter.addItem(model)
            binding.basicSettingsView.post {
                binding.basicSettingsView.fullScroll(View.FOCUS_DOWN)
                binding.recyclerSteps.post {
                    binding.recyclerSteps.getChildAt(binding.recyclerSteps.childCount - 1).requestFocus()
                    HintManager.showHint(requireContext(), HintManager.Hint.DELETE_ROW)
                }
            }
        }


        binding.editSequenceName.afterTextChanged {
            mainViewModel.getDirtySequence().value?.name = it
        }

        binding.editSequenceName.validate {
            if (it.isEmpty()) getString(R.string.error_empty_sequence_name) else null
        }

        binding.editSequenceHost.afterTextChanged {
            mainViewModel.getDirtySequence().value?.host = it
        }

        binding.editSequenceHost.validate {
            if (it.isEmpty()) getString(R.string.error_empty_host) else null
        }

    }
}