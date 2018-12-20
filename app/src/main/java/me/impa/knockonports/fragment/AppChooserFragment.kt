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
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import me.impa.knockonports.R
import me.impa.knockonports.data.AppData
import me.impa.knockonports.viewadapter.AppListAdapter
import me.impa.knockonports.viewmodel.MainViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AppChooserFragment: DialogFragment() {

    var onSelected: ((AppData) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_app_chooser, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appAdapter = AppListAdapter(context!!).apply { onSelected = this@AppChooserFragment.onSelected }
        val mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        val listView = view.findViewById<ListView>(R.id.list_apps)

        listView.adapter = appAdapter

        val apps = mainViewModel.getInstalledApps().value
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_loading)

        if (apps == null) {
            listView.visibility = View.GONE
            doAsync {
                val appList = AppData.loadInstalledApps(activity!!)
                uiThread {
                    mainViewModel.getInstalledApps().value = appList
                    appAdapter.apps = appList
                    progressBar.visibility = View.GONE
                    listView.visibility = View.VISIBLE
                }
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