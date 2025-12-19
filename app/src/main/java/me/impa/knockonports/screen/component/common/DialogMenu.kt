/*
 * Copyright (c) 2024-2025 Alexander Yaburov
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

package me.impa.knockonports.screen.component.common

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.impa.knockonports.R
import me.impa.knockonports.extension.debouncedClickable
import timber.log.Timber

private const val ICON_SIZE = 32

@Composable
@Suppress("LongParameterList")
fun <T> DialogMenu(
    label: String,
    itemsSource: DialogItemsSource<T>,
    onItemSelected: (index: Int, item: T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedItemToString: (T) -> String = { it.toString() },
    unsetValueText: String = "",
    drawItem: @Composable (T, Boolean, Boolean, IconData?, () -> Unit) -> Unit =
        @Composable { item: T, selected: Boolean, enabled: Boolean, iconData: IconData?, onClick: () -> Unit ->
            DialogMenuItem(selectedItemToString(item), selected, enabled, iconData, onClick)
        },
    enableFilter: Boolean = false,
    imageProvider: ((T) -> IconData?)? = null
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        // OutlinedTextField to display the selected item or a placeholder
        OutlinedTextField(
            label = { Text(label) },
            enabled = enabled,
            value = when (itemsSource) {
                is DialogItemsSource.ListItems -> itemsSource.items.getOrNull(itemsSource.selectedIndex)
                    ?.let { selectedItemToString(it) } ?: unsetValueText

                is DialogItemsSource.AsyncItems -> unsetValueText
            },
            modifier = Modifier.fillMaxWidth(),
            // Prevent manual editing of the text field
            onValueChange = { },
            trailingIcon = {
                val icon = painterResource(
                    if (expanded) R.drawable.arrow_drop_up_icon
                    else R.drawable.arrow_drop_down_icon
                )
                Icon(icon, contentDescription = null)
            },
            readOnly = true
        )
        // Invisible Surface to handle clicks and expand the dialog
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .debouncedClickable(enabled = enabled, onClick = { expanded = true }),
            color = Color.Transparent
        ) { }
    }

    // Show the DialogItemList when expanded
    if (expanded) {
        DialogItemList(
            onDismissRequest = { expanded = false },
            itemsSource = itemsSource,
            drawItem = drawItem,
            onItemSelected = onItemSelected,
            selectedItemToString = selectedItemToString,
            enableFilter = enableFilter,
            imageProvider = imageProvider
        )
    }
}

@Composable
@Suppress("LongParameterList")
fun <T> DialogItemList(
    onDismissRequest: () -> Unit,
    itemsSource: DialogItemsSource<T>,
    drawItem: @Composable (T, Boolean, Boolean, IconData?, () -> Unit) -> Unit,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String,
    enableFilter: Boolean,
    imageProvider: ((T) -> IconData?)? = null
) {
    var items by remember {
        // Initialize items based on the data source type
        if (itemsSource is DialogItemsSource.ListItems)
            mutableStateOf(itemsSource.items) else mutableStateOf(listOf())
    }
    var isListLoaded by remember {
        mutableStateOf(itemsSource is DialogItemsSource.ListItems)
    }
    var selectedIndex by remember {
        if (itemsSource is DialogItemsSource.ListItems)
            mutableIntStateOf(itemsSource.selectedIndex) else mutableIntStateOf(-1)
    }

    // If the data source is asynchronous, load the items
    // Use LaunchedEffect to load items asynchronously when the composable enters the composition
    if (itemsSource is DialogItemsSource.AsyncItems && !isListLoaded) {
        LaunchedEffect(itemsSource) {
            items = itemsSource.loadItems()
            selectedIndex = itemsSource.selectedIndex()
            isListLoaded = true
        }
    }

    // Create a Dialog window
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(12.dp)) {
            if (!isListLoaded) {
                LoadingIndicator()
            } else {
                Column {
                    // Display the list of items
                    val listState = rememberLazyListState()
                    // Scroll to the selected item if one is selected
                    if (selectedIndex > -1) {
                        LaunchedEffect("ScrollToSelectedIndex") {
                            listState.scrollToItem(selectedIndex)
                        }
                    }
                    var filter by rememberSaveable { mutableStateOf("") }

                    val filteredItems by remember {
                        derivedStateOf {
                            if (filter.isBlank())
                                items else items.filter { selectedItemToString(it).contains(filter.trim(), true) }
                        }
                    }

                    if (enableFilter)
                        OutlinedTextField(
                            value = filter,
                            label = { Text(stringResource(R.string.field_search)) },
                            onValueChange = { filter = it },
                            singleLine = true,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { filter = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        )

                    val coroutineScope = rememberCoroutineScope()
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState
                    ) {
                        itemsIndexed(filteredItems, key = { index, item -> item.hashCode() }) { index, item ->
                            val isSelected = index == selectedIndex
                            var imageData by remember {
                                mutableStateOf<IconData?>(imageProvider?.let { IconData.PlaceHolder })
                            }
                            imageProvider?.let {
                                LaunchedEffect(it) {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            imageData = it(item)
                                        } catch (e: Exception) {
                                            Timber.e(e)
                                        }
                                    }
                                }
                            }
                            // Draw the item with selection and click handling
                            drawItem(item, isSelected, true, imageData) {
                                onItemSelected(index, item)
                                onDismissRequest()
                            }
                            if (index < items.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DialogMenuItem(text: String, selected: Boolean, enabled: Boolean, iconData: IconData?, onClick: () -> Unit) {
    // Set the content color based on the item's state (enabled, selected)
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        selected -> MaterialTheme.colorScheme.primary.copy()
        else -> MaterialTheme.colorScheme.onSurface.copy()
    }

    val iconSize = with(LocalDensity.current) {
        MaterialTheme.typography.titleSmall.fontSize.toDp()
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        // Row to contain the text and handle clicks
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .debouncedClickable(onClick = onClick)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                Spacer(modifier = Modifier.size(iconSize))
            }
            iconData?.let {
                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                    when (it) {
                        is IconData.Image -> Image(
                            bitmap = it.drawable.toBitmap(config = Bitmap.Config.ARGB_8888).asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(ICON_SIZE.dp)
                        )

                        is IconData.Icon -> Icon(
                            it.imageVector, contentDescription = null,
                            modifier = Modifier.size(ICON_SIZE.dp)
                        )

                        IconData.PlaceHolder -> Spacer(modifier = Modifier.size(ICON_SIZE.dp))
                    }
                }
            }


            Text(text = text, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Stable
sealed interface DialogItemsSource<T> {
    @Stable
    data class ListItems<T>(val items: List<T>, val selectedIndex: Int) : DialogItemsSource<T>

    @Stable
    data class AsyncItems<T>(
        val loadItems: suspend () -> List<T>,
        val selectedIndex: suspend () -> Int
    ) : DialogItemsSource<T>
}

@Stable
sealed interface IconData {
    data object PlaceHolder : IconData

    @Stable
    data class Icon(val imageVector: ImageVector) : IconData

    @Stable
    data class Image(val drawable: Drawable) : IconData
}

@Composable
@Preview
fun PreviewDialogMenuItem() {
    DialogMenuItem("Item 1", false, true, null) {}
}

@Composable
@Preview
fun PreviewDialogMenu() {
    DialogMenu(
        label = "Select an item",
        itemsSource = DialogItemsSource.ListItems(listOf("Item1", "Item2"), 1),
        onItemSelected = { index, item -> }
    )
}

@Composable
@Preview
fun PreviewDialogItemList() {
    DialogItemList(
        onDismissRequest = {},
        itemsSource = DialogItemsSource.ListItems(listOf("Item1", "Item2", "Item3", "Item4"), 1),
        drawItem = @Composable { item, selected, enabled, image, onClick ->
            DialogMenuItem(item, selected, enabled, image, onClick)
        },
        selectedItemToString = { it },
        onItemSelected = { _, _ -> },
        enableFilter = false
    )
}
