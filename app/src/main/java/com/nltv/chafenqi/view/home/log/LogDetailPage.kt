package com.nltv.chafenqi.view.home.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.recent.HomeRecentPageEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDetailPage(navController: NavController, mode: Int, index: Int) {
    val model: LogDetailPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(mode, index) {
        model.update(mode, index)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "记录详情") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
        ) {
            item {
                LogDetailInfoColumn()
            }

            item {
                Text("游玩记录", modifier = Modifier.padding(top = 20.dp))
            }

            if (mode == 0) {
                items(
                    count = uiState.chunithmEntries.size,
                    key = { index -> uiState.chunithmEntries[index].timestamp }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        HomeRecentPageEntry(
                            entry = uiState.chunithmEntries[it],
                            index = CFQUser.chunithm.recent.indexOf(uiState.chunithmEntries[it]),
                            navController = navController
                        )
                    }
                }
            } else if (mode == 1) {
                items(
                    count = uiState.maimaiEntries.size,
                    key = { index -> uiState.maimaiEntries[index].timestamp }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        HomeRecentPageEntry(
                            entry = uiState.maimaiEntries[it],
                            index = CFQUser.maimai.recent.indexOf(uiState.maimaiEntries[it]),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogDetailInfoColumn() {
    val model: LogDetailPageViewModel = viewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = "Rating")
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = uiState.currentRating, style = MaterialTheme.typography.bodyLarge)
                if (uiState.currentRating != "无数据") {
                    Text(
                        text = "${uiState.ratingGainIndicator}${uiState.ratingGain}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = "总游玩曲目数")
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = uiState.currentPlayCount, style = MaterialTheme.typography.bodyLarge)
                if (uiState.currentPlayCount != "无数据") {
                    Text(
                        text = "${uiState.playCountGainIndicator}${uiState.playCountGain}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}