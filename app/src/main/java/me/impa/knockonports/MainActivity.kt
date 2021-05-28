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
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.impa.knockonports.data.ActiveFragmentType
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.databinding.ActivityMainBinding
import me.impa.knockonports.fragment.*
import me.impa.knockonports.util.AppPrefs
import me.impa.knockonports.util.Logging
import me.impa.knockonports.util.info
import me.impa.knockonports.util.warn
import me.impa.knockonports.viewmodel.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_EXPORT = 2000
private const val REQUEST_IMPORT = 3000
private const val FRAGMENT_SEQ_LIST = "SEQUENCE_LIST"
private const val FRAGMENT_SEQ_CFG = "SEQUENCE_CONFIG"
private const val FRAGMENT_LOG = "LOG"
private const val FRAGMENT_PREF = "PREF"
private const val STATE_EXPORT_FILENAME = "STATE_EXPORT_FILENAME"
private const val STATE_EXPORT_DIR = "STATE_EXPORT_DIR"
private const val STATE_IMPORT_DIR = "STATE_IMPORT_DIR"
const val EXTRA_SEQ_ID = "EXTRA_SEQ_ID"
const val EXTRA_ASK_CONFIRMATION = "EXTRA_ASK_CONFIRMATION"
const val EXTRA_IS_WIDGET = "EXTRA_IS_WIDGET"

class MainActivity : AppCompatActivity(), Logging {

    private var menu: Menu? = null
    private val twoPaneMode by lazy { resources.getBoolean(R.bool.twoPaneMode) }
    private var fragmentExport: FileChooserFragment? = null
    private var fragmentImport: FileChooserFragment? = null
    private var fragmentRateApp: RateAppFragment? = null
    private val isInstalledFromPlayStore by lazy {
        arrayOf("com.android.vending", "com.google.android.feedback").contains(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                packageManager.getInstallSourceInfo(packageName).installingPackageName
            else
                @Suppress("DEPRECATION")
                packageManager.getInstallerPackageName(packageName)
        )
    }
    private val currentTheme by lazy { AppPrefs.getCurrentTheme(this) }

    private val mainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppPrefs.init(this)
        AppPrefs.registerRestartHandler {
            recreate()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        when(currentTheme) {
            AppPrefs.THEME_DARK -> setTheme(R.style.AppTheme_Dark)
            else -> setTheme(R.style.AppTheme)
        }

        AppPrefs.checkFirstLaunch(this)

        installShortcutWatcher()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener {
            mainViewModel.createEmptySequence()
        }
        mainViewModel.getSelectedSequence().observe(this, {
            checkMenuGroups(noSelectedSeq = it == null)
            /*
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
            }*/
        })

        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        if (savedInstanceState != null) {
            supportFragmentManager.executePendingTransactions()
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment != null)
                supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        mainViewModel.getFabVisible().observe(this, {
            if (it == false) {
                binding.fab.hide()
            } else {
                binding.fab.show()
            }
        })

        mainViewModel.getDirtySequence().observe(this, {
            if (it == null) {
                if (twoPaneMode) {
                    mainViewModel.setFabVisible(true)
                } else {
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                mainViewModel.setSettingsTabIndex(0)
            } else {
                if (twoPaneMode) {
                    mainViewModel.setFabVisible(false)
                } else {
                    supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.fragment_container, SequenceConfigFragment())
                            .addToBackStack(FRAGMENT_SEQ_CFG)
                            .commit()
                }
            }
        })

        mainViewModel.getActiveFragment().observe(this, {
            val isLogVisible = it == ActiveFragmentType.LOG
            val isPrefVisible = it == ActiveFragmentType.PREFERENCES
            checkMenuGroups(logVisibility = isLogVisible, prefsVisibility = isPrefVisible)
            if (isLogVisible)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LogFragment())
                        .addToBackStack(FRAGMENT_LOG)
                        .commit()
            if (isPrefVisible)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PreferencesFragment())
                        .addToBackStack(FRAGMENT_PREF)
                        .commit()
        })

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SequenceListFragment(), FRAGMENT_SEQ_LIST).commit()
        if (!twoPaneMode) {

            supportFragmentManager.addOnBackStackChangedListener {
                when (supportFragmentManager.findFragmentByTag(FRAGMENT_SEQ_LIST)?.isVisible) {
                    true -> {
                        mainViewModel.setSelectedSequence(null)
                        mainViewModel.setFabVisible(true)
                        mainViewModel.setActiveFragment(ActiveFragmentType.MAIN)
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setTitle(R.string.app_name)
                    }
                    else -> {
                        mainViewModel.setFabVisible(false)
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.title = null
                    }
                }
            }

        }
        if (isInstalledFromPlayStore && RateAppFragment.isTimeToAskForReview(this))
            askForReview()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainViewModel.setSelectedSequence(null)
            R.id.action_done -> {
                mainViewModel.saveDirtyData()
                mainViewModel.setSelectedSequence(null)
            }
            R.id.action_new_sequence -> mainViewModel.createEmptySequence()
            R.id.action_export -> exportData()
            R.id.action_import -> importData()
            R.id.action_rate -> RateAppFragment.openPlayMarket(this)
            R.id.action_log -> mainViewModel.setActiveFragment(ActiveFragmentType.LOG)
            R.id.action_preferences -> mainViewModel.setActiveFragment(ActiveFragmentType.PREFERENCES)
            R.id.action_clear_log -> clearLogData()
        }
        return true
    }

    override fun onBackPressed() {
        when {
            mainViewModel.getActiveFragment().value != ActiveFragmentType.MAIN -> {
                mainViewModel.setActiveFragment(ActiveFragmentType.MAIN)
                super.onBackPressed()
            }
            twoPaneMode && mainViewModel.getSelectedSequence().value != null -> mainViewModel.setSelectedSequence(null)
            else -> super.onBackPressed()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val exName = savedInstanceState.getString(STATE_EXPORT_FILENAME)
        val exDir = savedInstanceState.getString(STATE_EXPORT_DIR)
        val impDir = savedInstanceState.getString(STATE_IMPORT_DIR)
        if (exName != null) {
            exportData(exName, exDir)
        } else if (impDir != null) {
            importData(impDir)
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

    private fun importData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            importDataSaf()
        else
            importData(null)
    }

    private val importActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val (contentUri, fileName) = parseIntentForUri(it.data) ?: return@registerForActivityResult
            mainViewModel.importSequences(fileName ?: "<?>") {
                application.contentResolver.openInputStream(contentUri)?.use { stream ->
                    stream.reader().use { reader -> reader.readText() }
                }!!
            }
        }
    }

    private fun importDataSaf() {
        val openDocIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        importActivityResultLauncher.launch(openDocIntent)
    }

    private fun importData(importDir: String?) {
        if (!checkPermissions(REQUEST_IMPORT, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return
        }

        fragmentImport = fileChooser {
            title = this@MainActivity.getString(R.string.title_import)
            currentDir = importDir
            showSaveButton = false
            showFileNameEdit = false
            onDismiss = { fragmentImport = null }
            onSelected = { mainViewModel.importSequences(it) { File(it).readText() } }
        }
    }

    private fun exportData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            exportDataSaf()
        else
            exportData(null, null)
    }

    private val exportActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val (contentUri, fileName) = parseIntentForUri(it.data) ?: return@registerForActivityResult
            mainViewModel.exportSequences(fileName ?: "<?>") { data ->
                application.contentResolver.openOutputStream(contentUri)?.use { stream ->
                    stream.writer().use { writer ->
                        writer.write(data)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun exportDataSaf() {
        val saveDocIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, getDefaultFileName())
        }
        exportActivityResultLauncher.launch(saveDocIntent)
    }

    private fun exportData(fileName: String?, exportDir: String?) {
        if (!checkPermissions(REQUEST_EXPORT, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return
        }
        fragmentExport = fileChooser {
            title = this@MainActivity.getString(R.string.title_export)
            dirsOnly = true
            this.fileName = fileName ?: getDefaultFileName()
            currentDir = exportDir
            onDismiss = { fragmentExport = null }
            onSelected = { mainViewModel.exportSequences(it) { data -> File(it).writeText(data) } }

        }
    }

    private fun getDefaultFileName() = getString(R.string.export_file_template,
        SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US).format(Date()))

    private fun parseIntentForUri(intent: Intent?): Pair<Uri, String?>? {
        val contentUri = intent?.data ?: return null
        val fileName = contentResolver.query(contentUri, null, null, null, null)
            .use { cursor ->
                cursor?.run {
                    val nameIndex = getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    moveToFirst()
                    getString(nameIndex)
                }
            }
        return Pair(contentUri, fileName)
    }

    private fun clearLogData() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.text_confirm_clear_log)
                .setTitle(R.string.title_clear_log)
                .setPositiveButton(R.string.action_yes) { _, _ ->
                    mainViewModel.clearLog()
                }
                .setNegativeButton(R.string.action_no) { dialog, _ ->
                    dialog.dismiss()
                }
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        checkMenuGroups()
        /*
        val selectedSequenceState = mainViewModel.getSelectedSequence().value == null
        menu.setGroupVisible(R.id.group_settings, !selectedSequenceState)
        menu.setGroupVisible(R.id.group_main, selectedSequenceState)
        menu.setGroupVisible(R.id.group_rate, selectedSequenceState && isInstalledFromPlayStore)*/
        return true
    }

    private fun checkMenuGroups(noSelectedSeq: Boolean = mainViewModel.getSelectedSequence().value == null,
                                logVisibility: Boolean = mainViewModel.getActiveFragment().value == ActiveFragmentType.LOG,
                                prefsVisibility: Boolean = mainViewModel.getActiveFragment().value == ActiveFragmentType.PREFERENCES) {
        menu?.setGroupVisible(R.id.group_settings, !noSelectedSeq && !logVisibility && !prefsVisibility)
        menu?.setGroupVisible(R.id.group_main, (noSelectedSeq || twoPaneMode) && !logVisibility && !prefsVisibility)
        menu?.setGroupVisible(R.id.group_rate, (noSelectedSeq || twoPaneMode) && !logVisibility && !prefsVisibility && isInstalledFromPlayStore)
        menu?.setGroupVisible(R.id.group_log, logVisibility)
    }

    private fun checkPermissions(action: Int, vararg perms: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.size == grantResults.size && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Delayed coz of android 6 bug - https://issuetracker.google.com/issues/37067655
            Handler(Looper.getMainLooper()).postDelayed({
                when (requestCode) {
                    REQUEST_EXPORT -> exportData()
                    REQUEST_IMPORT -> importData()
                }
            }, 100)
        }
    }

    private fun installShortcutWatcher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mainViewModel.getSequenceList().observe(this, Observer { sequences ->
                if (sequences == null)
                    return@Observer
                val shortcutManager = getSystemService(ShortcutManager::class.java) ?: return@Observer
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
