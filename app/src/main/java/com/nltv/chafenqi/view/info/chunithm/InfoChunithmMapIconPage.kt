package com.nltv.chafenqi.view.info.chunithm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.model.user.chunithm.UserChunithmMapIconEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoChunithmMapIconPage(navController: NavController) {
    val model: InfoChunithmPageViewModel = viewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "地图头像一览") },
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
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            items(
                count = model.mapIcons.size,
                key = { index -> model.mapIcons[index].url }
            ) { index ->
                ChunithmMapIconListEntry(entry = model.mapIcons[index])
            }
        }
    }
}

@Composable
fun ChunithmMapIconListEntry(entry: UserChunithmMapIconEntry) {
    Row(
        Modifier
            .padding(SCREEN_PADDING)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
    ) {
        AsyncImage(
            model = entry.url,
            contentDescription = "${entry.name}地图头像",
            contentScale = ContentScale.Crop
        )
        Text(text = entry.name)
        if (entry.current) {
            Text(text = "(当前头像)", fontWeight = FontWeight.Bold)
        }
    }
}