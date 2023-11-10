package com.nltv.chafenqi.view.info.chunithm

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.toChunithmTrophyType
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmTrophyEntry

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InfoChunithmTrophyPage(navController: NavController) {
    val model: InfoChunithmPageViewModel = viewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "称号一览") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            model.trophyGroups.forEach {
                stickyHeader { ChunithmTrophyStickyHeader(type = it.key, size = it.value.size) }
                items(
                    count = it.value.size,
                    key = { index -> it.value[index].name }
                ) { index ->
                    ChunithmTrophyListEntry(entry = it.value[index])
                }
            }
        }
    }
}

@Composable
fun ChunithmTrophyStickyHeader(type: String, size: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "${type.toChunithmTrophyType()}称号",
            modifier = Modifier.padding(start = SCREEN_PADDING)
        )
    }
}

@Composable
fun ChunithmTrophyListEntry(entry: ChunithmTrophyEntry) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING)
    ) {
        Text(text = entry.name, fontWeight = FontWeight.Bold)
        Text(text = entry.description)
    }
}