package com.nltv.chafenqi.view.info.maimai

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.model.user.maimai.UserMaimaiFrameEntry

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InfoMaimaiFramePage(navController: NavController) {
    val model: InfoMaimaiPageViewModel = viewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "底板一览") },
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
            Modifier
                .padding(innerPadding)
        ) {
            model.frameGroups.forEach { group ->
                stickyHeader { MaimaiFrameStickyHeader(area = group.key, size = group.value.size) }
                items(
                    count = group.value.size,
                    key = { index -> group.key + group.value[index].name }
                ) { index ->
                    MaimaiFrameListEntry(entry = group.value[index])
                }
            }
        }
    }
}

@Composable
fun MaimaiFrameStickyHeader(area: String, size: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = area, modifier = Modifier.padding(start = SCREEN_PADDING))
    }
}

@Composable
fun MaimaiFrameListEntry(entry: UserMaimaiFrameEntry) {
    Column(
        Modifier
            .padding(SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = entry.url,
            contentDescription = "${entry.name}姓名框图像",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Text(text = entry.name, fontWeight = FontWeight.Bold)
    }
}