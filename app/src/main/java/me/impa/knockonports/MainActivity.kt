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

package me.impa.knockonports

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.android.synthetic.main.activity_main.*
import me.impa.knockonports.ext.textInputEditText
import me.impa.knockonports.ext.textInputLayout
import me.impa.knockonports.fragment.SequenceConfigFragment
import me.impa.knockonports.fragment.SequenceListFragment
import me.impa.knockonports.viewmodel.MainViewModel
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_EXPORT = 2000
private const val REQUEST_IMPORT = 3000
private const val FRAGMENT_SEQ_LIST = "SEQUENCE_LIST"

class MainActivity : AppCompatActivity(), AnkoLogger {

    private var menu: Menu? = null

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            mainViewModel.createEmptySequence()
    }
        mainViewModel.getSelectedSequence().observe(this, Observer {
            if (it == null) {
                menu?.setGroupVisible(R.id.group_settings, false)
                menu?.setGroupVisible(R.id.group_main, true)
            } else {
                menu?.setGroupVisible(R.id.group_main, false)
                menu?.setGroupVisible(R.id.group_settings, true)
            }
        })

        if (savedInstanceState != null) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.executePendingTransactions()
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment != null)
                supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SequenceListFragment(), FRAGMENT_SEQ_LIST).commit()

        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.findFragmentByTag(FRAGMENT_SEQ_LIST)?.isVisible) {
                true -> {
                    mainViewModel.getSelectedSequence().value = null
                    mainViewModel.getFabVisible().value = true
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setTitle(R.string.app_name)
                }
                else -> {
                    mainViewModel.getFabVisible().value = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.title = null
                }
            }
        }

        mainViewModel.getFabVisible().observe(this, Observer {
            if (it == false) {
                fab.hide()
            } else {
                fab.show()
            }
        })

        mainViewModel.getDirtySequence().observe(this, Observer {
            if (it == null) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                mainViewModel.getSettingsTabIndex().value = 0
            } else {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, SequenceConfigFragment())
                        .addToBackStack(null)
                        .commit()
            }
        })

    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> mainViewModel.getSelectedSequence().value = null
            R.id.action_done -> {
                mainViewModel.saveDirtyData()
                mainViewModel.getSelectedSequence().value = null
            }
            R.id.action_new_sequence -> mainViewModel.createEmptySequence()
            R.id.action_export -> exportData()
            R.id.action_import -> importData()
        }
        return true
    }

    private fun importData() {
        if (!checkPermissions(REQUEST_IMPORT, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return
        }

        val dir = System.getenv("EXTERNAL_STORAGE")
        ChooserDialog()
                .with(this)
                //.withFilterRegex(false, true, "*")
                .withStartFile(dir)
                .withResources(R.string.title_import, R.string.action_import, R.string.button_cancel)
                .withChosenListener { _, file ->
                    mainViewModel.importData(file)
                }
                .build()
                .show()
    }

    private fun exportData() {
        if (!checkPermissions(REQUEST_EXPORT, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return
        }

        lateinit var dialog: DialogInterface
        lateinit var fileEdit: TextInputEditText

        dialog = alert {
            customView {
                verticalLayout {
                    padding = dip(16)
                    textView(R.string.title_export) {
                        textSize = 24f
                    }.lparams{
                        bottomMargin = dip(16)
                    }
                    textInputLayout {
                        hint = context.getString(R.string.title_file_name)
                        fileEdit = textInputEditText {
                            this.setText(getString(R.string.export_file_template, SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US).format(Date())))
                        }
                    }
                    linearLayout {
                        topPadding = dip(24)
                        orientation = LinearLayout.HORIZONTAL
                        horizontalGravity = Gravity.END

                        textView(R.string.button_cancel) {
                            textSize = 14f
                            textColor = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
                            isAllCaps = true
                            typeface = Typeface.DEFAULT_BOLD
                        }.lparams{
                            rightMargin = dip(16)
                        }.setOnClickListener {
                            dialog.dismiss()
                        }
                        textView(R.string.button_save) {
                            textSize = 14f
                            isAllCaps = true
                            typeface = Typeface.DEFAULT_BOLD
                            textColor = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
                        }.setOnClickListener {
                            dialog.dismiss()
                            exportDataStageTwo(fileEdit.text.toString())
                        }

                    }
                }
            }
        }.show()

    }

    private fun exportDataStageTwo(fileName: String) {
        if (!checkPermissions(REQUEST_EXPORT, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return
        }

        val dir = System.getenv("EXTERNAL_STORAGE")
        ChooserDialog()
                .with(this)
                .withFilter(true, true)
                .withStartFile(dir)
                .withResources(R.string.title_export, R.string.button_save, R.string.button_cancel)
                .withChosenListener { path, _ ->
                    mainViewModel.exportData("$path/$fileName")
                }
                .build()
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        val selectedSequenceState = mainViewModel.getSelectedSequence().value == null
        menu.setGroupVisible(R.id.group_settings, !selectedSequenceState)
        menu.setGroupVisible(R.id.group_main, selectedSequenceState)
        return true
    }

    private fun checkPermissions(action: Int, vararg perms: String): Boolean {
        val reqPermissions = mutableListOf<String>()
        perms.forEach {
            val permission = ContextCompat.checkSelfPermission(this, it)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                warn { "No $it permission!" }
                reqPermissions.add(it)
            }
        }

        if (reqPermissions.size > 0) {
            info { "Requesting permissions: ${reqPermissions.joinToString()}" }
            ActivityCompat.requestPermissions(this, reqPermissions.toTypedArray(), action)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissions.size == grantResults.size && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            when(requestCode) {
                REQUEST_EXPORT -> exportData()
                REQUEST_IMPORT -> importData()
            }
        }
    }

}
