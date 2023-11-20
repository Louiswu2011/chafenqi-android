package com.nltv.chafenqi.view.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.SCREEN_PADDING
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAcknowledgePage(navController: NavController) {
    val model = viewModel<SettingsPageViewModel>()
    val scope = rememberCoroutineScope()
    val uiState by model.uiState.collectAsState()

    LaunchedEffect(Unit) {
        scope.launch { model.updateSponsorList() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "鸣谢") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            contentPadding = PaddingValues(SCREEN_PADDING)
        ) {
            item {
                Column {
                    Text(
                        text = "制作人员",
                        Modifier.padding(vertical = SCREEN_PADDING),
                        color = MaterialTheme.colorScheme.primary
                    )
                    DEVELOPERS.forEach { developer ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = developer.name)
                            Text(text = developer.contribution)
                        }
                    }
                }
            }
            item {
                Text(
                    text = "爱发电贡献名单 (排名不分先后)",
                    Modifier.padding(vertical = SCREEN_PADDING),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (uiState.sponsorList.isNotEmpty()) {
                items(
                    count = uiState.sponsorList.size,
                    key = { index -> uiState.sponsorList[index] }
                ) { index ->
                    Text(text = uiState.sponsorList[index])
                }
            } else {
                item {
                    Text(text = "加载中")
                }
            }
        }
    }
}