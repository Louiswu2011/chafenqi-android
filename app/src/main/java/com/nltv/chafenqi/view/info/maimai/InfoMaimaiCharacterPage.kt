package com.nltv.chafenqi.view.info.maimai

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.model.user.maimai.UserMaimaiCharacterEntry

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InfoMaimaiCharacterPage(navController: NavController) {
    val model: InfoMaimaiPageViewModel = viewModel()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "角色一览") },
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
            model.characterGroups.forEach {
                stickyHeader { MaimaiCharacterStickyHeader(area = it.key, size = it.value.size) }
                items(
                    count = it.value.size,
                    key = { index -> it.key + it.value[index].name }
                ) { index ->
                    MaimaiCharacterListEntry(entry = it.value[index])
                }
            }
        }
    }
}

@Composable
fun MaimaiCharacterStickyHeader(area: String, size: Int) {
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
fun MaimaiCharacterListEntry(entry: UserMaimaiCharacterEntry) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = entry.url,
                contentDescription = "${entry.name}角色头像",
                modifier = Modifier.size(72.dp)
            )
            Text(text = entry.name, fontWeight = FontWeight.Bold)
        }
        Text(text = entry.level)
    }
}