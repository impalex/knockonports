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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.impa.knockonports.R
import me.impa.knockonports.data.DescriptionType
import me.impa.knockonports.data.IcmpType
import me.impa.knockonports.data.ProtocolVersionType
import me.impa.knockonports.databinding.FragmentSequenceConfigAdvancedBinding
import me.impa.knockonports.ext.afterTextChanged
import me.impa.knockonports.viewmodel.MainViewModel

class AdvancedSettingsFragment: Fragment() {

    private var _binding: FragmentSequenceConfigAdvancedBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by lazy { ViewModelProvider(requireActivity()).get(MainViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentSequenceConfigAdvancedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icmpTypeSpinner.run {
            val icmpTypeAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOf(
                    context.getString(R.string.icmp_type_without_headers),
                    context.getString(R.string.icmp_type_with_icmp_header)
            ))
            icmpTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = icmpTypeAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val icmpType = IcmpType.fromOrdinal(position)
                    mainViewModel.getDirtySequence().value?.icmpType = icmpType
                    mainViewModel.getDirtySteps().value?.forEach { it.icmpSizeOffset = icmpType.getOffset() }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }

        binding.ipVersionSpinner.run {
            val ipVersionAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOf(
                    context.getString(R.string.ip_prefer_ipv4),
                    context.getString(R.string.ip_prefer_ipv6),
                    context.getString(R.string.ip_only_ipv4),
                    context.getString(R.string.ip_only_ipv6),
                    context.getString(R.string.ip_both)
            ))
            ipVersionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = ipVersionAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val ipType = ProtocolVersionType.fromOrdinal(position)
                    mainViewModel.getDirtySequence().value?.ipv = ipType
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }

        mainViewModel.getDirtySequence().observe(viewLifecycleOwner, {
            binding.editSequenceDelay.setText(it?.delay?.toString())
            binding.ipVersionSpinner.setSelection(it?.ipv?.ordinal ?: 0)
            binding.icmpTypeSpinner.setSelection(it?.icmpType?.ordinal ?: 1)
            binding.checkboxHideDetails.isChecked = (it?.descriptionType == DescriptionType.HIDE)
            view.jumpDrawablesToCurrentState()
            showAppName(it?.application, it?.applicationName)
        })

        binding.textAppName.setOnClickListener { showAppChooser() }
        binding.imageAppDown.setOnClickListener { showAppChooser() }

        binding.editSequenceDelay.afterTextChanged {
            mainViewModel.getDirtySequence().value?.delay = it.toIntOrNull()
        }

        binding.checkboxHideDetails.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.getDirtySequence().value?.descriptionType = if (isChecked) DescriptionType.HIDE else DescriptionType.DEFAULT
        }
    }

    private fun showAppName(appId: String?, defaultName: String? = null) {
        val app = mainViewModel.getInstalledApps().value?.firstOrNull { it.app == appId }
        binding.textAppName.text = app?.name ?: when {
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