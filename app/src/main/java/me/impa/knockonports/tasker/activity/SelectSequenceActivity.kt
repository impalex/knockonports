/*
 * Copyright (c) 2019 Alexander Yaburov
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

package me.impa.knockonports.tasker.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity
import me.impa.knockonports.R
import me.impa.knockonports.databinding.TaskerActionConfigureBinding
import me.impa.knockonports.tasker.bundle.KnockerBundleValues
import me.impa.knockonports.util.AppPrefs
import me.impa.knockonports.util.Logging
import me.impa.knockonports.util.error
import me.impa.knockonports.viewmodel.MainViewModel
import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class SelectSequenceActivity : AbstractAppCompatPluginActivity(), Logging {

    private lateinit var binding: TaskerActionConfigureBinding

    private val mainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    private var idsList: List<Long>? = null
    private var seqId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TaskerActionConfigureBinding.inflate(layoutInflater)

        when(AppPrefs.getCurrentTheme(this)) {
            AppPrefs.THEME_DARK -> setTheme(R.style.AppTheme_Tasker_Dark)
            else -> setTheme(R.style.AppTheme_Tasker)
        }

        setContentView(binding.root)

        val appLabel = try {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(callingPackage!!, 0))
        } catch (e: PackageManager.NameNotFoundException) {
            error("Calling package couldn't be found", e)
            null
        }

        if (appLabel != null)
            title = appLabel

        supportActionBar?.setSubtitle(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainViewModel.getSequenceList().observe(this, { sequences ->
            val sortedList = sequences?.sortedBy { it.name }
            idsList = sortedList?.map { it.id!! }
            binding.knockOnSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortedList?.map { it.name }?.toMutableList()
                    ?: mutableListOf()).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            selectSequence(seqId)
        })
    }

    private fun selectSequence(id: Long) {
        val idx = idsList?.indexOf(id) ?: -1
        if (idx >= 0)
            binding.knockOnSpinner.setSelection(idx)
    }

    override fun getResultBlurb(bundle: Bundle): String {
        val sequenceId = KnockerBundleValues.getSequenceId(bundle)
        val sequence = mainViewModel.findSequence(sequenceId)

        return getString(R.string.tasker_action_blurb, sequence?.name
                ?: getString(R.string.unknown_sequence)).take(R.integer.com_twofortyfouram_locale_sdk_client_maximum_blurb_length)
    }

    override fun isBundleValid(bundle: Bundle) = KnockerBundleValues.isBundleValid(bundle)

    override fun getResultBundle(): Bundle? {
        val selection = binding.knockOnSpinner.selectedItemPosition
        return if (selection >= 0 && selection < idsList?.count() ?: 0 && idsList != null) KnockerBundleValues.generateBundle(this, idsList!![selection]) else null
    }

    override fun onPostCreateWithPreviousResult(bundle: Bundle, prevBlurb: String) {
        seqId = KnockerBundleValues.getSequenceId(bundle)
        selectSequence(seqId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tasker, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    finish()
                    super.onOptionsItemSelected(item)
                }
                R.id.tasker_cancel -> {
                    mIsCancelled = true
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

}