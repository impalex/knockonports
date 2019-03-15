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
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.ext.expandTo
import me.impa.knockonports.fragment.*
import me.impa.knockonports.viewmodel.MainViewModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_EXPORT = 2000
private const val REQUEST_IMPORT = 3000
private const val FRAGMENT_SEQ_LIST = "SEQUENCE_LIST"
private const val FRAGMENT_SEQ_CFG = "SEQUENCE_CONFIG"
private const val STATE_EXPORT_FILENAME = "STATE_EXPORT_FILENAME"
private const val STATE_EXPORT_DIR = "STATE_EXPORT_DIR"
private const val STATE_IMPORT_DIR = "STATE_IMPORT_DIR"
const val EXTRA_SEQ_ID = "EXTRA_SEQ_ID"
private const val EXPAND_DURATION = 300L

class MainActivity : AppCompatActivity(), AnkoLogger {

    private var menu: Menu? = null
    private val twoPaneMode by lazy { resources.getBoolean(R.bool.twoPaneMode) }
    private var fragmentExport: FileChooserFragment? = null
    private var fragmentImport: FileChooserFragment? = null
    private var fragmentRateApp: RateAppFragment? = null
    private val isInstalledFromPlayStore by lazy {
        arrayOf("com.android.vending", "com.google.android.feedback").contains(packageManager.getInstallerPackageName(packageName))
    }

    private val mainViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RateAppFragment.checkFirstLaunch(this)

        installShortcutWatcher()

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            mainViewModel.createEmptySequence()
        }
        mainViewModel.getSelectedSequence().observe(this, Observer {
            if (it == null) {
                menu?.setGroupVisible(R.id.group_settings, false)
                if (!twoPaneMode) {
                    menu?.setGroupVisible(R.id.group_main, true)
                    menu?.setGroupVisible(R.id.group_rate, isInstalledFromPlayStore)
                }
            } else {
                if (!twoPaneMode) {
                    menu?.setGroupVisible(R.id.group_main, false)
                    menu?.setGroupVisible(R.id.group_rate, false)
                }
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

        if (isInstalledFromPlayStore && RateAppFragment.isTimeToAskForReview(this))
            askForReview()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> mainViewModel.getSelectedSequence().value = null
            R.id.action_done -> {
                mainViewModel.saveDirtyData()
                mainViewModel.getSelectedSequence().value = null
            }
            R.id.action_new_sequence -> mainViewModel.createEmptySequence()
            R.id.action_export -> exportData()
            R.id.action_import -> importData()
            R.id.action_rate -> RateAppFragment.openPlayMarket(this)
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
        if (fragmentExport != null) {
            outState.putString(STATE_EXPORT_DIR, fragmentExport?.currentDir)
            outState.putString(STATE_EXPORT_FILENAME, fragmentExport?.fileName)
        }
        if (fragmentImport != null) {
            outState.putString(STATE_IMPORT_DIR, fragmentImport?.currentDir)
        }
    }

    override fun onResume() {
        super.onResume()
        (application as App).currentActivity = this
    }

    override fun onPause() {
        (application as App).currentActivity = null
        super.onPause()
    }

    override fun onDestroy() {
        (application as App).currentActivity = null
        super.onDestroy()
    }

    private fun importData(importDir: String? = null) {
        if (!checkPermissions(REQUEST_IMPORT, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return
        }

        fragmentImport = fileChooser {
            title = this@MainActivity.getString(R.string.title_import)
            currentDir = importDir
            showSaveButton = false
            showFileNameEdit = false
            onDismiss = { fragmentImport = null }
            onSelected = { mainViewModel.importData(it) }
        }

    }

    private fun exportData(fileName: String? = null, exportDir: String? = null) {
        if (!checkPermissions(REQUEST_EXPORT, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return
        }

        fragmentExport = fileChooser {
            title = this@MainActivity.getString(R.string.title_export)
            dirsOnly = true
            this.fileName = fileName ?: this@MainActivity.getString(R.string.export_file_template,
                    SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US).format(Date()))
            currentDir = exportDir
            onDismiss = { fragmentExport = null }
            onSelected = { mainViewModel.exportData(it) }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        val selectedSequenceState = mainViewModel.getSelectedSequence().value == null
        menu.setGroupVisible(R.id.group_settings, !selectedSequenceState)
        menu.setGroupVisible(R.id.group_main, selectedSequenceState)
        menu.setGroupVisible(R.id.group_rate, selectedSequenceState && isInstalledFromPlayStore)
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
            when (requestCode) {
                REQUEST_EXPORT -> exportData()
                REQUEST_IMPORT -> importData()
            }
        }
    }

    private fun installShortcutWatcher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mainViewModel.getSequenceList().observe(this, Observer { sequences ->
                if (sequences == null)
                    return@Observer
                val shortcutManager = getSystemService(ShortcutManager::class.java)
                if (sequences.count() == 0) {
                    shortcutManager.removeAllDynamicShortcuts()
                } else {
                    shortcutManager.dynamicShortcuts = sequences.filter { !it.name.isNullOrBlank() }
                            .take(shortcutManager.maxShortcutCountPerActivity).map { Sequence.getShortcutInfo(this, it) }
                }
                // Let's check pinned shortcuts
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && shortcutManager.isRequestPinShortcutSupported) {
                    val pinnedShortcuts = shortcutManager.pinnedShortcuts
                    val enableShortcuts = mutableListOf<String>()
                    val disableShortcuts = mutableListOf<String>()
                    val updateShortcuts = mutableListOf<ShortcutInfo>()
                    for (s in pinnedShortcuts) {
                        val id = s?.intent?.getLongExtra(EXTRA_SEQ_ID, 0) ?: continue
                        val shortcut = sequences.firstOrNull { it.id == id }
                        if (shortcut != null) {
                            if (!s.isEnabled && !shortcut.name.isNullOrEmpty())
                                enableShortcuts.add(s.id)
                            if (shortcut.name != s.shortLabel) {
                                if (shortcut.name.isNullOrEmpty())
                                    disableShortcuts.add(s.id)
                                else if (s.isPinned)
                                    updateShortcuts.add(Sequence.getShortcutInfo(this, shortcut, false))
                            }
                        }
                        else {
                            if (s.isEnabled)
                                disableShortcuts.add(s.id)
                        }
                    }
                    if (updateShortcuts.any())
                        shortcutManager.updateShortcuts(updateShortcuts)
                    if (enableShortcuts.any())
                        shortcutManager.enableShortcuts(enableShortcuts)
                    if (disableShortcuts.any())
                        shortcutManager.disableShortcuts(disableShortcuts)
                }
            })
        }
    }

    private fun askForReview() {
        fragmentRateApp = RateAppFragment().apply {
            onDismiss = { fragmentRateApp = null }
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag(RateAppFragment.FRAGMENT_ASK_REVIEW)
            if (prev != null)
                ft.remove(prev)
            ft.commit()
            show(supportFragmentManager, RateAppFragment.FRAGMENT_ASK_REVIEW)
        }
    }

}
