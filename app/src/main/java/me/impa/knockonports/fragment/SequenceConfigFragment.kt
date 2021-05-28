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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import me.impa.knockonports.R
import me.impa.knockonports.databinding.FragmentSequenceConfigBinding
import me.impa.knockonports.viewadapter.SettingsPagerAdapter
import me.impa.knockonports.viewmodel.MainViewModel

class SequenceConfigFragment: Fragment() {

    private var _binding: FragmentSequenceConfigBinding? = null
    private val binding get() = _binding!!
    private val titleList by lazy { arrayOf(requireActivity().getString(R.string.title_settings), requireActivity().getString(R.string.title_advanced)) }

    private val mainViewModel by lazy { ViewModelProvider(requireActivity()).get(MainViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSequenceConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.visibility = if (mainViewModel.getDirtySequence().value == null) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
        val adapter = SettingsPagerAdapter(this,
                arrayOf(BasicSettingsFragment(), AdvancedSettingsFragment()))

        binding.viewpagerSettings.adapter = adapter


        val tabLayout = binding.tabsSettings
        TabLayoutMediator(tabLayout, binding.viewpagerSettings) { tab, pos ->
            tab.text = titleList[pos]
        }.attach()

        val tabIdx = mainViewModel.getSettingsTabIndex().value
        if (tabIdx != null)
            tabLayout.getTabAt(tabIdx)?.select()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mainViewModel.setSettingsTabIndex(tabLayout.selectedTabPosition)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        mainViewModel.getDirtySequence().observe(viewLifecycleOwner, {
            view.visibility = if (it == null) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        })
    }

}