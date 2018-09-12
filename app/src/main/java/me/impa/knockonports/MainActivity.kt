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
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import me.impa.knockonports.ext.*
import me.impa.knockonports.fragment.SequenceConfigFragment
import me.impa.knockonports.fragment.SequenceListFragment
import me.impa.knockonports.viewmodel.MainViewModel
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_EXPORT = 2000
private const val REQUEST_IMPORT = 3000
private const val FRAGMENT_SEQ_LIST = "SEQUENCE_LIST"
private const val FRAGMENT_SEQ_CFG = "SEQUENCE_CONFIG"
private const val STATE_EXPORT_FILENAME = "STATE_EXPORT_FILENAME"
private const val STATE_EXPORT_DIR = "STATE_EXPORT_DIR"
private const val STATE_IMPORT_DIR = "STATE_IMPORT_DIR"
private const val EXPAND_DURATION = 300L

class MainActivity : AppCompatActivity(), AnkoLogger {

    private var menu: Menu? = null
    private val twoPaneMode by lazy { resources.getBoolean(R.bool.twoPaneMode) }
    private var dialogExportFileName: DialogInterface? = null
    private var editExportFileName: TextInputEditText? = null
    private var exportFileName: String? = null
    private var dialogExportChooser: ChooserDialog? = null
    private var dialogImportChooser: ChooserDialog? = null


    private val mainViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            mainViewModel.createEmptySequence()
        }
        mainViewModel.getSelectedSequence().observe(this, Observer {
            if (it == null) {
                menu?.setGroupVisible(R.id.group_settings, false)
                if (!twoPaneMode)
                    menu?.setGroupVisible(R.id.group_main, true)
            } else {
                if (!twoPaneMode)
                    menu?.setGroupVisible(R.id.group_main, false)
                menu?.setGroupVisible(R.id.group_settings, true)
            }
        })

        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        if (!twoPaneMode && savedInstanceState != null) {
            supportFragmentManager.executePendingTransactions()
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment != null)
                supportFragmentManager.beginTransaction().remove(fragment).commit()
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
                if (twoPaneMode) {
                    fragment_seq_config?.expandTo(0f, EXPAND_DURATION)
                    scroll_seq_list?.expandTo(1f, EXPAND_DURATION)
                    mainViewModel.getFabVisible().value = true
                } else {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                mainViewModel.getSettingsTabIndex().value = 0
            } else {
                if (twoPaneMode) {
                    if (it.id == null) {
                        fragment_seq_config?.expandTo(1f, EXPAND_DURATION)
                        scroll_seq_list?.expandTo(0f, EXPAND_DURATION)
                    } else {
                        fragment_seq_config?.expandTo(2f, EXPAND_DURATION)
                        scroll_seq_list?.expandTo(1f, EXPAND_DURATION)
                    }
                    mainViewModel.getFabVisible().value = false
                } else {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.fragment_container, SequenceConfigFragment())
                            .addToBackStack(FRAGMENT_SEQ_CFG)
                            .commit()
                }
            }
        })

        if (!twoPaneMode) {
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

        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_seq_list, SequenceListFragment())
                    .replace(R.id.fragment_seq_config, SequenceConfigFragment())
                    .commit()
        }

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

    override fun onBackPressed() {
        if (twoPaneMode && mainViewModel.getSelectedSequence().value != null) {
            mainViewModel.getSelectedSequence().value = null
        } else
            super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            val exName = savedInstanceState.getString(STATE_EXPORT_FILENAME)
            val exDir = savedInstanceState.getString(STATE_EXPORT_DIR)
            val impDir = savedInstanceState.getString(STATE_IMPORT_DIR)
            if (exName != null) {
                exportData(exName, exDir)
            } else if (impDir != null) {
                importData(impDir)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (dialogExportFileName != null) {
            outState.putString(STATE_EXPORT_FILENAME, editExportFileName?.text?.toString())
        }
        if (dialogExportChooser != null) {
            outState.putString(STATE_EXPORT_DIR, dialogExportChooser?.getCurrentDir()?.absolutePath)
            outState.putString(STATE_EXPORT_FILENAME, exportFileName)
        }
        if (dialogImportChooser != null) {
            outState.putString(STATE_IMPORT_DIR, dialogImportChooser?.getCurrentDir()?.absolutePath)
        }
    }

    override fun onStop() {
        super.onStop()
        dialogExportFileName?.dismiss()
        dialogExportFileName = null
        dialogExportChooser?.getAlertDialog()?.dismiss()
        dialogExportChooser = null
        dialogImportChooser?.getAlertDialog()?.dismiss()
        dialogImportChooser = null
    }

    private fun importData(importDir: String? = null) {
        if (!checkPermissions(REQUEST_IMPORT, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return
        }

        dialogImportChooser = ChooserDialog()
                .with(this)
                .withStartFile(importDir ?: Environment.getExternalStorageDirectory().absolutePath)
                .withResources(R.string.title_import, R.string.action_import, R.string.button_cancel)
                .withChosenListener { _, file ->
                    dialogImportChooser = null
                    mainViewModel.importData(file)
                }
                .withNegativeButtonListener { _, _ ->
                    dialogImportChooser = null
                }
                .build()
                .show()
        dialogImportChooser?.getAlertDialog()?.setOnDismissListener {
            dialogImportChooser = null
        }
    }

    private fun exportData(fileName: String? = null, exportDir: String? = null) {
        if (!checkPermissions(REQUEST_EXPORT, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return
        }

        if (exportDir == null || fileName == null) {
            showExportFileNameDialog(fileName ?: getString(R.string.export_file_template,
                    SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US).format(Date())))
        } else {
            showExportDirDialog(fileName, exportDir)
        }
    }

    private fun showExportFileNameDialog(fileName: String) {
        dialogExportFileName = alert {
            customView {
                verticalLayout {
                    padding = dip(16)
                    textView(R.string.title_export) {
                        textSize = 24f
                    }.lparams {
                        bottomMargin = dip(16)
                    }
                    textInputLayout {
                        hint = context.getString(R.string.title_file_name)
                        editExportFileName = textInputEditText {
                            this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                            this.setText(fileName)
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
                        }.lparams {
                            rightMargin = dip(16)
                        }.setOnClickListener {
                            dialogExportFileName?.dismiss()
                            dialogExportFileName = null
                        }
                        textView(R.string.button_save) {
                            textSize = 14f
                            isAllCaps = true
                            typeface = Typeface.DEFAULT_BOLD
                            textColor = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
                        }.setOnClickListener {
                            dialogExportFileName?.dismiss()
                            dialogExportFileName = null
                            val name = editExportFileName?.text?.toString()
                            if (name != null) {
                                exportData(name, Environment.getExternalStorageDirectory().absolutePath)
                            }
                        }

                    }
                }
            }
            onCancelled {
                dialogExportFileName = null
            }
        }.show()

    }

    private fun showExportDirDialog(fileName: String, exportDir: String?) {
        exportFileName = fileName
        dialogExportChooser = ChooserDialog()
                .with(this)
                .withFilter(true, true)
                .withStartFile(exportDir)
                .withResources(R.string.title_export, R.string.button_save, R.string.button_cancel)
                .withChosenListener { path, _ ->
                    dialogExportChooser = null
                    mainViewModel.exportData("$path/$fileName")
                }.withNegativeButtonListener { _, _ ->
                    dialogExportChooser = null
                }
                .build()
                .show()
        dialogExportChooser?.getAlertDialog()?.setOnDismissListener {
            dialogExportChooser = null
        }
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
