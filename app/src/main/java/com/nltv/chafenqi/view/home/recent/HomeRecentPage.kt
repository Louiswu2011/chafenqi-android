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
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRecentScoreEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRecentPage(navController: NavController) {
    val listState = rememberLazyListState()

    val model: HomeRecentViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "最近动态") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(),
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
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        ) {
            items(
                count = model.getRecentList().size,
                key = { index -> model.getRecentList()[index].timestamp },
                itemContent = { index ->
                    val entry = model.getRecentList()[index]
                    if (entry is MaimaiRecentScoreEntry) {
                        HomeRecentPageEntry(entry, index, navController)
                    } else if (entry is ChunithmRecentScoreEntry) {
                        HomeRecentPageEntry(entry, index, navController)
                    }
                }
            )
        }
    }
}

@Composable
fun HomeRecentPageEntry(entry: MaimaiRecentScoreEntry, index: Int, navController: NavController) {
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
            model = entry.associatedMusicEntry.musicID.toMaimaiCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(72.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp, color = maimaiDifficultyColors[entry.levelIndex]
                    ), shape = RoundedCornerShape(10.dp)
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
                Text(
                    text = "%.4f".format(entry.achievements).plus("%"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun HomeRecentPageEntry(entry: ChunithmRecentScoreEntry, index: Int, navController: NavController) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable {
                Log.i("HomeRecentPageEntry", "Jump from index $index")
                navController.navigate(HomeNavItem.Home.route + "/recent/chunithm/${index}")
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = entry.associatedMusicEntry.musicID.toChunithmCoverPath(),
            contentDescription = "歌曲封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(72.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp, color = chunithmDifficultyColors[entry.levelIndex]
                    ), shape = RoundedCornerShape(10.dp)
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
                Text(text = entry.score.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}