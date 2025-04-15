/*
 * Copyright (c) 2024-2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package me.impa.knockonports.screen

import android.Manifest
import android.content.Context
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.collections.immutable.ImmutableList
import me.impa.knockonports.R
import me.impa.knockonports.constants.POSTPONE_TIME
import me.impa.knockonports.constants.POSTPONE_TIME_CANCEL
import me.impa.knockonports.data.db.entity.Sequence
import me.impa.knockonports.extension.getShortcutInfo
import me.impa.knockonports.knock.KnockerService
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.navigation.AppNavGraph
import me.impa.knockonports.screen.action.MainViewInterface
import me.impa.knockonports.screen.component.main.DeleteSequenceAlert
import me.impa.knockonports.screen.component.main.FocusedSequenceWatcher
import me.impa.knockonports.screen.component.main.MaybeShowReviewRequestDialog
import me.impa.knockonports.screen.component.main.SequenceCard
import me.impa.knockonports.screen.component.main.UpdateAppBar
import me.impa.knockonports.screen.component.main.UpdateToV2Alert
import me.impa.knockonports.screen.viewmodel.MainViewModel
import me.impa.knockonports.util.openPlayStore
import sh.calvin.reorderable.rememberReorderableLazyListState
import timber.log.Timber

const val FOCUSED_SEQUENCE_ID = "focusedSequenceId"
const val ANIMATION_DURATION = 200

@OptIn(ExperimentalPermissionsApi::class)
private val grantedNotificationPermission = object : PermissionState {
    override val permission: String
        get() = ""
    override val status: PermissionStatus
        get() = PermissionStatus.Granted

    override fun launchPermissionRequest() = Unit
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    onComposing: (AppBarState) -> Unit, navController: NavController,
    modifier: Modifier = Modifier, viewModel: MainViewModel = hiltViewModel()
) {
    navController.UpdateAppBar(onComposing, viewModel::importSequences, viewModel::exportSequences)
    val firstLaunchV2 by viewModel.firstLaunchV2
    if (firstLaunchV2)
      UpdateToV2Alert(onDismiss = viewModel::clearFirstLaunchV2)

        CheckReviewRequest(viewModel)
    val doNotAskAboutNotifications by viewModel.doNotAskAboutNotifications
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        !doNotAskAboutNotifications
    ) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        grantedNotificationPermission
    }
    val focusedSequenceId by viewModel.focusedSequenceId.collectAsState()
    FocusedSequenceWatcher(
        navController.currentBackStackEntry?.savedStateHandle,
        onFocusedSequenceChange = viewModel::setFocusedSequence
    )
    val settings by viewModel.appSettings.collectAsState()
    // Observe sequences from ViewModel
    val sequences by viewModel.sequences.collectAsState()
    val isShortcutsAvailable by viewModel.shortcutsAvailable.collectAsState()
    val context = LocalContext.current
    val actions = object : MainViewInterface {
        override var confirmDelete by rememberSaveable { mutableStateOf<Sequence?>(null) }
        override val onSequenceMove = { from: Int, to: Int -> viewModel.moveSequence(from, to) }
        override val onDragEnded = { viewModel.confirmReorder() }
        override val onDelete = { sequence: Sequence -> viewModel.deleteSequence(sequence) }
        override val onCreateShortcut = { sequence: Sequence ->
            if (isShortcutsAvailable)
                createShortcut(context, viewModel.shortcutManager.value!!, sequence)
        }
        override val onFocusedSequenceChange = { id: Long? -> viewModel.setFocusedSequence(id) }
        override val onEdit = { sequenceId: Long ->
            navController.navigate(AppNavGraph.SequenceRoute(sequenceId))
        }
        override val onDuplicate = { sequenceId: Long -> viewModel.cloneSequence(sequenceId) }
    }
    MainScreenContent(
        sequences,
        focusedSequenceId,
        actions = actions,
        showSequenceDetails = settings.detailedListView,
        onFocusedSequenceChange = viewModel::setFocusedSequence,
        modifier = modifier,
        isShortcutsAvailable = isShortcutsAvailable,
        onKnock = {
            checkPermission(notificationPermissionState, viewModel::setDoNotAskAboutNotificationsFlag)
            KnockerService.startService(context, it)
        }
    )
}

@Composable
private fun CheckReviewRequest(viewModel: MainViewModel) {
    val isInstalledFromPlayStore by viewModel.isInstalledFromPlayStore
    val doNotAskForReview by viewModel.doNotAskForReview
    if (isInstalledFromPlayStore && !doNotAskForReview) {
        val knockCount by viewModel.knockCount
        val askReviewTime by viewModel.askReviewTime
        val context = LocalContext.current
        MaybeShowReviewRequestDialog(
            knockCount, askReviewTime,
            onDismiss = { viewModel.postponeReviewRequest(POSTPONE_TIME_CANCEL) },
            onDecline = {
                viewModel.doNotAskForReview()
                viewModel.sendMessageEvent(R.string.text_review_decline)
            },
            onPostpone = { viewModel.postponeReviewRequest(POSTPONE_TIME) },
            onRateNow = {
                viewModel.doNotAskForReview()
                try {
                    openPlayStore(context)
                } catch (e: Exception) {
                    Timber.e(e)
                    viewModel.sendErrorEvent(e.message ?: "")
                }
            })
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun checkPermission(permissionState: PermissionState, turnOffRequest: () -> Unit) {
    if (permissionState.status is PermissionStatus.Denied)
        permissionState.launchPermissionRequest()
    turnOffRequest()
}

private fun createShortcut(context: Context, shortcutManager: ShortcutManager, sequence: Sequence) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        shortcutManager.requestPinShortcut(sequence.getShortcutInfo(context, false), null)
    }
}

@Composable
fun MainScreenContent(
    sequences: ImmutableList<Sequence>,
    focusedSequenceId: Long?,
    actions: MainViewInterface,
    showSequenceDetails: Boolean,
    isShortcutsAvailable: Boolean,
    modifier: Modifier = Modifier,
    onFocusedSequenceChange: (Long?) -> Unit = { },
    onKnock: (Long) -> Unit = {}
) {
    // State for the LazyColumn
    val listState = rememberLazyListState()
    // Save and restore the LazyColumn's state across configuration changes
    val savedListState = rememberSaveable(saver = LazyListState.Saver) { listState }
    val view = LocalView.current
    // State for the reorderable list functionality. Handles drag and drop reordering within the list.
    // The lambda is called when an item is moved.
    val reorderableListState = rememberReorderableLazyListState(savedListState) { from, to ->
        actions.onSequenceMove(from.index, to.index)
        ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK)
    }
    var highLightSequenceId by rememberSaveable { mutableStateOf<Long?>(null) }
    if (focusedSequenceId != null) {
        highLightSequenceId = focusedSequenceId
        LaunchedEffect(focusedSequenceId, sequences) {
            val index = sequences.indexOfFirst { it.id == focusedSequenceId }
            Timber.d("Focused sequence id: $focusedSequenceId index: $index")
            if (index != -1) {
                if (savedListState.layoutInfo.visibleItemsInfo.none { it.key == focusedSequenceId })
                    savedListState.animateScrollToItem(index)
                onFocusedSequenceChange(null)
            }
        }
    }

    actions.confirmDelete?.let { sequence ->
        Timber.d("Showing delete sequence alert")
        DeleteSequenceAlert(sequence, onDismiss = { actions.confirmDelete = null }, onConfirm = {
            actions.confirmDelete = null
            actions.onDelete(sequence)
        })
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.then(Modifier.fillMaxSize()), state = savedListState
    ) {
        itemsIndexed(sequences, key = { index, sequence -> sequence.id ?: 0L }) { index, sequence ->
            // Display each sequence as a card
            SequenceCard(
                sequence = sequence,
                state = reorderableListState,
                isHighLighted = highLightSequenceId == sequence.id,
                onHighLightFinished = { highLightSequenceId = null },
                actions = actions,
                showSequenceDetails = showSequenceDetails,
                isShortcutsAvailable = isShortcutsAvailable,
                modifier = Modifier.padding(top = if (index == 0) 8.dp else 0.dp),
                onKnock = onKnock
            )
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreenContent(
        PreviewData.mockSequences, null, PreviewData.dummyMainInterface,
        isShortcutsAvailable = true, showSequenceDetails = true
    )
}

@Preview
@Composable
fun PreviewSequenceCard() {
    val listState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(listState) { _, _ -> }
    LazyColumn {
        item {
            SequenceCard(
                PreviewData.mockSequences[0],
                state = reorderableListState,
                actions = PreviewData.dummyMainInterface,
                isShortcutsAvailable = true,
                showSequenceDetails = true
            )
        }
    }
}

@Preview
@Composable
fun PreviewCompactSequenceCard() {
    val listState = rememberLazyListState()
    val reorderableListState = rememberReorderableLazyListState(listState) { _, _ -> }
    LazyColumn {
        item {
            SequenceCard(
                PreviewData.mockSequences[2],
                state = reorderableListState,
                actions = PreviewData.dummyMainInterface,
                isShortcutsAvailable = true,
                showSequenceDetails = false
            )
        }
    }
}

