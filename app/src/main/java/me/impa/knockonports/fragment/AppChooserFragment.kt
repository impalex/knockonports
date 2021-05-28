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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import me.impa.knockonports.R
import me.impa.knockonports.data.AppData
import me.impa.knockonports.databinding.FragmentAppChooserBinding
import me.impa.knockonports.viewadapter.AppListAdapter
import me.impa.knockonports.viewmodel.MainViewModel

class AppChooserFragment: DialogFragment() {

    private var _binding: FragmentAppChooserBinding? = null
    private val binding get() = _binding!!

    var onSelected: ((AppData) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppChooserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appAdapter = AppListAdapter(requireContext()).apply { onSelected = this@AppChooserFragment.onSelected }
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val listView = binding.listApps

        listView.adapter = appAdapter

        val apps = mainViewModel.getInstalledApps().value
        val progressBar = binding.progressLoading

        if (apps == null) {
            listView.visibility = View.GONE
            CoroutineScope(Dispatchers.Main).launch {
                val appList = withContext(Dispatchers.Default) {
                    AppData.loadInstalledApps(requireActivity())
                }
                mainViewModel.setInstalledApps(appList)
                appAdapter.apps = appList
                progressBar.visibility = View.GONE
                listView.visibility = View.VISIBLE
            }
        } else {
            progressBar.visibility = View.GONE
            appAdapter.apps = apps
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            super.onCreateDialog(savedInstanceState).apply { window?.setTitle(getString(R.string.installed_apps)) }

    companion object {
        const val FRAGMENT_APP_CHOOSER = "APP_CHOOSER"
    }

}