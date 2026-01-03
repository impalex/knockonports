/*
 * Copyright (c) 2025 Alexander Yaburov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.impa.knockonports.service.shortcut

import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import me.impa.knockonports.constants.EXTRA_SEQ_ID
import me.impa.knockonports.constants.INVALID_SEQ_ID
import me.impa.knockonports.data.KnocksRepository
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.di.DefaultDispatcher
import me.impa.knockonports.di.IoDispatcher
import me.impa.knockonports.extension.getShortcutInfo
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observes changes in the list of sequences and updates dynamic and pinned shortcuts accordingly.
 *
 * This class is responsible for ensuring that the system's shortcuts (both dynamic and pinned)
 * reflect the current state of user-defined sequences.  It handles adding, removing, enabling,
 * disabling, and updating shortcuts based on changes to the sequence data.  It gracefully
 * handles situations where the ShortcutManager is unavailable or the device's API level is
 * insufficient.
 *
 * @property context The application context.
 * @property repository A [me.impa.knockonports.data.KnocksRepository] instance, used to retrieve the list of sequences.
 * @property defaultDispatcher The [CoroutineDispatcher] to use for background operations.
 */
@Singleton
class ShortcutWatcher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: KnocksRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DefaultLifecycleObserver {
    private val shortcutManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            context.getSystemService(ShortcutManager::class.java)
        else
            null
    }

    private var scope: CoroutineScope? = null

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            scope = CoroutineScope(defaultDispatcher + SupervisorJob()).apply {
                launch {
                    shortcutManager?.let { manager ->
                        repository.getSequences().distinctUntilChanged().flowOn(ioDispatcher).collect {
                            validateShortcuts(manager, it)
                        }
                    }
                }
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        scope?.cancel()
        scope = null
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun validateShortcuts(manager: ShortcutManager, sequences: List<Sequence>) {
        Timber.d("Validating shortcuts (${sequences.size})")
        if (sequences.isEmpty()) {
            manager.removeAllDynamicShortcuts()
        } else {
            manager.dynamicShortcuts = sequences.filter { !it.name.isNullOrBlank() }
                .take(manager.maxShortcutCountPerActivity).map { it.getShortcutInfo(context) }
        }
        Timber.d("Shortcuts validated, count: ${manager.dynamicShortcuts.size}")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !manager.isRequestPinShortcutSupported)
            return

        Timber.d("Validating pinned shortcuts")

        val pinnedShortcuts = manager.pinnedShortcuts
        val enableShortcuts = mutableListOf<String>()
        val disableShortcuts = mutableListOf<String>()
        val updateShortcuts = mutableListOf<ShortcutInfo>()

        for (shortcut in pinnedShortcuts) {
            when (val action = checkShortcut(shortcut, sequences)) {
                is Action.Enable -> enableShortcuts.add(shortcut.id)
                is Action.Disable -> disableShortcuts.add(shortcut.id)
                is Action.Update -> updateShortcuts.add(action.info)
                is Action.Skip -> Unit
            }
        }
        updateShortcuts.takeIf { it.isNotEmpty() }?.let { manager.updateShortcuts(it) }
        enableShortcuts.takeIf { it.isNotEmpty() }?.let { manager.enableShortcuts(it) }
        disableShortcuts.takeIf { it.isNotEmpty() }?.let { manager.disableShortcuts(it) }
    }

    @Suppress("ReturnCount")
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun checkShortcut(shortcut: ShortcutInfo, sequences: List<Sequence>): Action {
        val id = shortcut.intent?.getLongExtra(EXTRA_SEQ_ID, INVALID_SEQ_ID) ?: return Action.Skip
        val shortcutSequence = sequences.find { it.id == id }
        if (shortcutSequence != null) {
            if (!shortcut.isEnabled && !shortcutSequence.name.isNullOrEmpty())
                return Action.Enable
            if (shortcutSequence.name != shortcut.shortLabel) {
                return if (shortcutSequence.name.isNullOrEmpty())
                    Action.Disable
                else
                    Action.Update(shortcutSequence.getShortcutInfo(context, isAuto = false))
            }
        } else {
            if (shortcut.isEnabled) return Action.Disable
        }

        return Action.Skip
    }


    private sealed interface Action {
        object Enable : Action
        object Disable : Action
        class Update(val info: ShortcutInfo) : Action
        object Skip : Action
    }
}