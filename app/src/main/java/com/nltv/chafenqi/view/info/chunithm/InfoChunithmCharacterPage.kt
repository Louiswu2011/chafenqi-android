package com.nltv.chafenqi.view.info.chunithm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.model.user.chunithm.UserChunithmCharacterEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoChunithmCharacterPage(navController: NavController) {
    val model: InfoChunithmPageViewModel = viewModel()

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
            modifier = Modifier
                .padding(innerPadding)
        ) {
            items(
                count = model.characters.size,
                key = { index -> model.characters[index].url }
            ) { index ->
                ChunithmCharacterListEntry(entry = model.characters[index])
            }
        }
    }
}

@Composable
fun ChunithmCharacterListEntry(entry: UserChunithmCharacterEntry) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING)
            .height(64.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AsyncImage(
            model = entry.url,
            contentDescription = "${entry.name}角色头像",
            modifier = Modifier.fillMaxHeight()
        )
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.name,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "Lv ${entry.rank.uppercase()}")
            }
            LinearProgressIndicator(
                progress = { entry.exp.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}