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
import me.impa.knockonports.R
import me.impa.knockonports.constants.POSTPONE_TIME
import me.impa.knockonports.constants.POSTPONE_TIME_CANCEL
import me.impa.knockonports.navigation.AppBarState
import me.impa.knockonports.navigation.AppNavGraph
import me.impa.knockonports.screen.component.main.DeleteSequenceAlert
import me.impa.knockonports.screen.component.main.FocusedSequenceWatcher
import me.impa.knockonports.screen.component.main.IntegrationAlert
import me.impa.knockonports.screen.component.main.ReviewRequestDialog
import me.impa.knockonports.screen.component.main.SequenceCard
import me.impa.knockonports.screen.component.main.UpdateAppBar
import me.impa.knockonports.screen.component.main.UpdateToV2Alert
import me.impa.knockonports.screen.viewmodel.MainViewModel
import me.impa.knockonports.screen.viewmodel.state.main.UiEvent
import me.impa.knockonports.screen.viewmodel.state.main.UiOverlay
import me.impa.knockonports.screen.viewmodel.state.main.UiState
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
    navController.UpdateAppBar(onComposing, viewModel::onEvent)

    val state by viewModel.state.collectAsState()
    LaunchedEffect(state) {
        if (state.editMode) {
            navController.navigate(AppNavGraph.SequenceRoute(state.editSequenceId))
            viewModel.onEvent(UiEvent.ResetEditMode)
        }
    }

    val overlay by viewModel.overlay.collectAsState()
    overlay?.let { ShowOverlay(it, viewModel::onEvent) }

    FocusedSequenceWatcher(navController.currentBackStackEntry?.savedStateHandle, viewModel::onEvent)
    MainScreenContent(
        state,
        modifier = modifier,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalPermissionsApi::class)
private fun checkPermission(permissionState: PermissionState, turnOffRequest: () -> Unit) {
    if (permissionState.status is PermissionStatus.Denied)
        permissionState.launchPermissionRequest()
    turnOffRequest()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreenContent(
    state: UiState,
    modifier: Modifier = Modifier,
    onEvent: (UiEvent) -> Unit = {}
) {
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        !state.disableNotificationRequest
    ) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        grantedNotificationPermission
    }
    // State for the LazyColumn
    val listState = rememberLazyListState()
    val view = LocalView.current
    // State for the reorderable list functionality. Handles drag and drop reordering within the list.
    // The lambda is called when an item is moved.
    val reorderableListState = rememberReorderableLazyListState(listState) { from, to ->
        onEvent(UiEvent.Move(from.index, to.index))
        ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK)
    }
    var highLightSequenceId by rememberSaveable { mutableStateOf<Long?>(null) }
    state.focusedSequenceId?.let {
        highLightSequenceId = it
        LaunchedEffect(it, state.sequences) {
            val index = state.sequences.indexOfFirst { it.id == state.focusedSequenceId }
            if (index != -1) {
                if (listState.layoutInfo.visibleItemsInfo.none { it.key == state.focusedSequenceId })
                    listState.animateScrollToItem(index)
                onEvent(UiEvent.Focus(null))
            }
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.then(Modifier.fillMaxSize()), state = listState
    ) {
        itemsIndexed(state.sequences, key = { index, sequence -> sequence.id ?: 0L }) { index, sequence ->
            // Display each sequence as a card
            SequenceCard(
                sequence = sequence,
                state = reorderableListState,
                isHighLighted = highLightSequenceId == sequence.id,
                onHighLightFinished = { highLightSequenceId = null },
                showSequenceDetails = state.detailedList,
                isShortcutsAvailable = state.areShortcutsAvailable,
                modifier = Modifier.padding(top = if (index == 0) 8.dp else 0.dp),
                onEvent = onEvent,
                onPermissionRequest = if (notificationPermissionState.status is PermissionStatus.Denied
                    && !state.disableNotificationRequest
                ) {
                    { checkPermission(notificationPermissionState, { onEvent(UiEvent.DisableNotificationRequest) }) }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun ShowOverlay(overlay: UiOverlay, onEvent: (UiEvent) -> Unit) {
    val context = LocalContext.current
    when (overlay) {
        is UiOverlay.ConfirmDelete -> DeleteSequenceAlert(
            sequenceName = overlay.name,
            onDismiss = { onEvent(UiEvent.ClearOverlay) },
            onConfirm = { onEvent(UiEvent.ConfirmDelete) })

        is UiOverlay.Automate -> IntegrationAlert(id = overlay.id, onDismiss = { onEvent(UiEvent.ClearOverlay) })
        is UiOverlay.UpdateToV2 -> UpdateToV2Alert(onDismiss = { onEvent(UiEvent.ClearOverlay) })
        is UiOverlay.Review -> ReviewRequestDialog(
            onDismissRequest = { onEvent(UiEvent.PostponeReviewRequest(POSTPONE_TIME_CANCEL)) },
            onDecline = {
                onEvent(UiEvent.DoNotAskForReview)
                onEvent(UiEvent.ShowMessage(R.string.text_review_decline))
            },
            onPostpone = { onEvent(UiEvent.PostponeReviewRequest(POSTPONE_TIME)) },
            onRateNow = {
                onEvent(UiEvent.DoNotAskForReview)
                try {
                    openPlayStore(context)
                } catch (e: Exception) {
                    Timber.e(e)
                    onEvent(UiEvent.ShowError(e.message ?: ""))
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreenContent(
        UiState(sequences = PreviewData.mockSequences),
        onEvent = {},
        modifier = Modifier
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
                isShortcutsAvailable = true,
                showSequenceDetails = false
            )
        }
    }
}

