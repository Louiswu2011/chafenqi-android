package com.nltv.chafenqi.view.settings.home

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.settings.SettingsTopBar
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsHomeArrangementPage(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val store = SettingsStore(context)
    val scope = rememberCoroutineScope()
    val list by store.homeArrangement.collectAsStateWithLifecycle(initialValue = "最近动态|Rating分析|排行榜|出勤记录")

    val listState = rememberLazyListState()
    val reorderableListState =
        rememberReorderableLazyListState(listState) { from, to ->
            store.setHomeArrangement(list.split("|").toMutableList().apply {
                add(to.index, removeAt(from.index))
            }.joinToString("|"))
        }

    LaunchedEffect(Unit) {
        if (list.split("|").size < 4) {
            scope.launch {
                store.setHomeArrangement("最近动态|Rating分析|排行榜|出勤记录")
            }
        }
    }

    Scaffold(
        topBar = { SettingsTopBar(titleText = "排序", navController = navController, action = {
            TextButton(
                onClick = {
                    scope.launch {
                        store.setHomeArrangement("最近动态|Rating分析|排行榜|出勤记录")
                    }
                }
            ) {
                Text(text = "重设排序")
            }
        }) },
    ) { paddingValues ->
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                state = listState
            ) {
                items(
                    count = list.split("|").size,
                    key = { list.split("|")[it] }
                ) { index ->
                    ReorderableItem(reorderableListState, key = list.split("|")[index]) { isDragging ->
                        val interactionSource = remember { MutableInteractionSource() }

                        Card (
                            onClick = {},
                            interactionSource = interactionSource,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = list.split("|")[index])
                                IconButton(
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = { view.performHapticFeedback(HapticFeedbackConstants.DRAG_START) },
                                        onDragStopped = { view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END) }
                                    ),
                                    onClick = {}
                                ) {
                                    Icon(imageVector = Icons.Rounded.DragHandle, contentDescription = "Reorder Drag Handle")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
