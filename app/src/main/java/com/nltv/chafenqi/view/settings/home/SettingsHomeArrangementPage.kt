package com.nltv.chafenqi.view.settings.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nltv.chafenqi.storage.SettingsStore
import com.nltv.chafenqi.view.settings.SettingsTopBar
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsHomeArrangementPage(navController: NavController) {
    val context = LocalContext.current
    val store = SettingsStore(context)
    val list by store.homeArrangement.collectAsStateWithLifecycle(initialValue = "最近动态|Rating分析|排行榜")
    val items = list.split("|")

    val listState = rememberLazyListState()
    val reorderableListState =
        rememberReorderableLazyListState(lazyListState = listState) { from, to ->

        }

    Scaffold(
        topBar = { SettingsTopBar(titleText = "排序", navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            state = listState
        ) {
            items(
                count = items.size,
                key = { items[it] }
            ) { index ->
                ReorderableItem(reorderableListState, key = items[index]) {

                }
            }
        }
    }
}