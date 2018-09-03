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

package me.impa.knockonports.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import me.impa.knockonports.R
import me.impa.knockonports.data.AppData
import me.impa.knockonports.database.KnocksRepository
import me.impa.knockonports.database.entity.Port
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.json.SequenceData
import me.impa.knockonports.service.Knocker
import me.impa.knockonports.widget.KnocksWidget
import org.jetbrains.anko.*
import java.io.File

class MainViewModel(application: Application): AndroidViewModel(application), AnkoLogger {

    private val repository: KnocksRepository = KnocksRepository(application)
    private val sequenceList: LiveData<List<Sequence>>
    private val selectedSequence: MutableLiveData<Sequence?>
    private val previousSequence: MutableLiveData<Sequence?>
    private val dirtySequence: LiveData<Sequence?>
    private val dirtyPorts: LiveData<MutableList<Port>?>
    private val settingsTabIndex: MutableLiveData<Int>
    private val fabVisible: MutableLiveData<Boolean>
    private val pendingOrderChanges: MutableLiveData<List<Long>>
    private val installedApps: MutableLiveData<List<AppData>> by lazy {
        val apps = MutableLiveData<List<AppData>>()
        val packages = application.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        apps.value = sequenceOf(AppData("", application.resources.getString(R.string.none)))
                .plus(packages
                        .filter { application.packageManager.getLaunchIntentForPackage(it.packageName) != null }
                        .map { AppData(it.packageName, application.packageManager.getApplicationLabel(it).toString()) }
                        .sortedBy { it.name })
                .toList()
        apps
    }

    init {
        sequenceList = repository.getSequenceList()
        selectedSequence = MutableLiveData()
        previousSequence = MutableLiveData()
        settingsTabIndex = MutableLiveData()
        fabVisible = MutableLiveData()
        pendingOrderChanges = MutableLiveData()
        fabVisible.value = true
        dirtySequence = Transformations.map(selectedSequence) {
            doAsync {
                savePendingData()
            }.get()
            it?.copy()
        }
        dirtyPorts = Transformations.map(selectedSequence) {
            if (it == null) {
                null
            } else {
                if (it.id == null) {
                    mutableListOf()
                } else {
                    doAsyncResult {
                        repository.getPortList(it.id ?: -1)
                    }.get().toMutableList()
                }
            }
        }

    }

    fun getSequenceList(): LiveData<List<Sequence>> {
        doAsync { savePendingData() }.get()
        return sequenceList
    }

    fun getSelectedSequence(): MutableLiveData<Sequence?> = selectedSequence

    fun getPreviousSequence(): MutableLiveData<Sequence?> = previousSequence

    fun getDirtyPorts(): LiveData<MutableList<Port>?> = dirtyPorts

    fun getDirtySequence(): LiveData<Sequence?> = dirtySequence

    fun getSettingsTabIndex(): MutableLiveData<Int> = settingsTabIndex

    fun getFabVisible(): MutableLiveData<Boolean> = fabVisible

    fun getInstalledApps(): LiveData<List<AppData>> = installedApps

    fun getPendingDataChanges(): MutableLiveData<List<Long>> = pendingOrderChanges

    override fun onCleared() {
        super.onCleared()
        doAsync { savePendingData() }.get()
    }

    fun deleteSequence(sequence: Sequence) {
        sequence.id ?: return
        doAsync { savePendingData() }.get()
        doAsync {
            repository.deleteSequence(sequence)
            uiThread {
                updateWidgets()
            }
        }
    }

    fun createEmptySequence() {
        selectedSequence.value = Sequence(null, null, null, null,
                null, null, null, null, 0, null)
    }

    fun saveDirtyData() {
        val seq = dirtySequence.value
        seq ?: return
        if (seq.id == null) {
            seq.order = pendingOrderChanges.value?.size ?: sequenceList.value?.size ?: 0
        }
        doAsync {
            repository.saveSequence(seq, dirtyPorts.value?.toList())
            uiThread {
                updateWidgets()
            }
        }
    }

    fun knock(sequence: Sequence) {
        val seqId = sequence.id ?: return
        val application = getApplication<Application>()
        doAsync {
            Knocker(application, sequence, repository.getPortList(seqId)).execute()
        }
    }

    private fun savePendingData() {
        val data = pendingOrderChanges.value
        data ?: return
        val changes = mutableListOf<Sequence>()
        data.forEachIndexed{ index, l ->
            val seq = sequenceList.value?.firstOrNull { it.id == l }
            if (seq != null && seq.order != index) {
                seq.order = index
                changes.add(seq)
            }
        }
        if (changes.size>0) {
            repository.updateSequences(changes)
        }
    }

    fun exportData(fileName: String) {
        val application = getApplication<Application>()
        doAsync {
            try {
                info { "Exporting data to $fileName" }

                val data = sequenceList.value?.map { SequenceData.fromEntity(it, repository.getPortList(it.id!!)) }?.toList() ?: return@doAsync

                File(fileName).writeText(JSON.stringify(SequenceData.serializer().list, data))

                uiThread {
                    application.toast(application.resources.getString(R.string.export_success, fileName))
                }
                info { "Export complete" }

            }
            catch (e: Exception) {
                warn("Unable to export data", e)
                uiThread {
                    application.toast(R.string.error_export)
                }
            }
        }
    }

    fun importData(file: File) {
        val application = getApplication<Application>()
        val order = sequenceList.value?.size ?: 0
        doAsync {
            try {
                val raw = file.readText()
                val data = JSON.parse(SequenceData.serializer().list, raw)
                data.forEachIndexed { index, sequenceData ->
                    val seq = sequenceData.toEntity()
                    seq.order = order + index
                    val ports = sequenceData.ports.map { it.toEntity() }.toList()
                    repository.saveSequence(seq, ports)
                }
                uiThread {
                    application.toast(application.resources.getString(R.string.import_success, data.size, file.absolutePath))
                }
            }
            catch (e: Exception) {
                warn("Unable to import data", e)
                uiThread {
                    application.toast(R.string.error_import)
                }
            }
        }
    }

    private fun updateWidgets() {
        val application = getApplication<Application>()
        val intent = Intent(application, KnocksWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val widgetManager = AppWidgetManager.getInstance(application)
        val ids = widgetManager.getAppWidgetIds(ComponentName(application, KnocksWidget::class.java))
        widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        application.sendBroadcast(intent)
    }

}