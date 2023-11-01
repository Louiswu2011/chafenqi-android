package com.nltv.chafenqi.view.home.recent

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.view.AppViewModelProvider
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.HomePageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRecentPage(navController: NavController) {
    val listState = rememberLazyListState()

    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "最近动态") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回上一级")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        ) {
            items(
                count = model.user.maimai.recent.size,
                key = { index -> model.user.maimai.recent[index].timestamp },
                itemContent = { index ->
                    HomeRecentPageEntry(entry = model.user.maimai.recent[index], index, navController)
                }
            )
        }
    }
}

@Composable
fun HomeRecentPageEntry(entry: MaimaiRecentScoreEntry, index: Int, navController: NavController) {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable {
                Log.i("HomeRecentPageEntry", "Jump from index $index")
                navController.navigate(HomeNavItem.Home.route + "/recent/maimai/${index}")
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = model.getAssociatedMaimaiMusic(title = entry.title).id.toMaimaiCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(72.dp)
                .border(
                    border = BorderStroke(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(size = 10.dp))
        )
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(entry.timestamp.toDateString(), fontSize = 14.sp)
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(entry.title, fontSize = 16.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Text(text = "%.4f".format(entry.achievements).plus("%"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}