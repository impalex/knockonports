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
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import me.impa.knockonports.R
import me.impa.knockonports.data.AppData
import me.impa.knockonports.data.ContentEncoding
import me.impa.knockonports.data.IcmpType
import me.impa.knockonports.data.SequenceStepType
import me.impa.knockonports.database.KnocksRepository
import me.impa.knockonports.database.converter.SequenceConverters
import me.impa.knockonports.database.entity.Sequence
import me.impa.knockonports.ext.default
import me.impa.knockonports.ext.startSequence
import me.impa.knockonports.json.SequenceData
import me.impa.knockonports.json.SequenceStep
import me.impa.knockonports.widget.KnocksWidget
import org.jetbrains.anko.*
import java.io.File

class MainViewModel(application: Application): AndroidViewModel(application), AnkoLogger {

    private val repository by lazy { KnocksRepository(application) }
    private val sequenceList: LiveData<List<Sequence>> = repository.getSequences()
    private val selectedSequence = MutableLiveData<Sequence?>()
    private val settingsTabIndex = MutableLiveData<Int>()
    private val fabVisible = MutableLiveData<Boolean>().default(true)
    private val pendingOrderChanges: MutableLiveData<List<Long>> = MutableLiveData()
    private val installedApps = MutableLiveData<List<AppData>?>().default(null)
    private val dirtySequence = Transformations.map(selectedSequence) {
        doAsync {
            savePendingData()
        }.get()
        it?.copy()
    }
    private val dirtySteps = Transformations.map(selectedSequence) {
        it?.steps?.onEach { e -> e.icmpSizeOffset = it.icmpType?.offset ?: 0 }?.toMutableList() ?: mutableListOf()
    }
    fun getSequenceList(): LiveData<List<Sequence>> {
        doAsync { savePendingData() }.get()
        return sequenceList
    }

    fun getSelectedSequence(): MutableLiveData<Sequence?> = selectedSequence

    fun getDirtySteps(): LiveData<MutableList<SequenceStep>> = dirtySteps

    fun getDirtySequence(): LiveData<Sequence?> = dirtySequence

    fun getSettingsTabIndex(): MutableLiveData<Int> = settingsTabIndex

    fun getFabVisible(): MutableLiveData<Boolean> = fabVisible

    fun getInstalledApps(): MutableLiveData<List<AppData>?> = installedApps

    fun getPendingDataChanges(): MutableLiveData<List<Long>> = pendingOrderChanges

    fun findSequence(id: Long): Sequence? = doAsyncResult { repository.findSequence(id) }.get()

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
                if (sequence.id == selectedSequence.value?.id) {
                    selectedSequence.value = null
                }
            }
        }
    }

    fun createEmptySequence() {
        selectedSequence.value = Sequence(null, null, null,
                null, 500, null, null, IcmpType.WITHOUT_HEADERS,
                listOf(SequenceStep(SequenceStepType.UDP, null, null, null, null, ContentEncoding.RAW).apply {
                    icmpSizeOffset = IcmpType.WITHOUT_HEADERS.offset
                }))
    }

    fun saveDirtyData() {
        val seq = dirtySequence.value
        seq ?: return
        if (seq.id == null) {
            seq.order = pendingOrderChanges.value?.size ?: sequenceList.value?.size ?: 0
        }
        seq.steps = dirtySteps.value
        info {
            SequenceConverters().sequenceStepToString(seq.steps)
        }

        doAsync {
            repository.saveSequence(seq)
            uiThread {
                updateWidgets()
            }
        }
    }

    fun knock(sequence: Sequence) {
        val seqId = sequence.id ?: return
        val application = getApplication<Application>()
        application.startSequence(seqId)
    }

    private fun savePendingData() {
        val data = pendingOrderChanges.value
        data ?: return
        val changes = mutableListOf<Sequence>()
        data.forEachIndexed { index, l ->
            val seq = sequenceList.value?.firstOrNull { it.id == l }
            if (seq != null && seq.order != index) {
                seq.order = index
                changes.add(seq)
            }
        }
        if (changes.size > 0) {
            repository.updateSequences(changes)
        }
    }

    fun exportData(fileName: String) {
        val application = getApplication<Application>()
        doAsync {
            try {
                info { "Exporting data to $fileName" }

                val data = sequenceList.value?.asSequence()?.map { SequenceData.fromEntity(it) }?.toList()
                        ?: return@doAsync

                File(fileName).writeText(SequenceData.toJson(data))

                uiThread {
                    application.toast(application.resources.getString(R.string.export_success, fileName))
                }
                info { "Export complete" }

            } catch (e: Exception) {
                warn("Unable to export data", e)
                uiThread {
                    application.toast(R.string.error_export)
                }
            }
        }
    }

    fun importData(fileName: String) {
        val application = getApplication<Application>()
        val order = sequenceList.value?.size ?: 0
        doAsync {
            try {
                val raw = File(fileName).readText()
                val data = SequenceData.fromJson(raw)
                data.forEachIndexed { index, sequenceData ->
                    val seq = sequenceData.toEntity()
                    seq.order = order + index
                    repository.saveSequence(seq)
                }
                uiThread {
                    application.toast(application.resources.getString(R.string.import_success, data.size, fileName))
                }
            } catch (e: Exception) {
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