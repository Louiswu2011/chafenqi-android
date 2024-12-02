package com.nltv.chafenqi.view.home.recent

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.model.user.chunithm.UserChunithmRecentScoreEntry
import com.nltv.chafenqi.model.user.maimai.UserMaimaiRecentScoreEntry
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRecentPage(navController: NavController) {
    val model: HomeRecentViewModel = viewModel()

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var currentPageIndex by rememberSaveable { mutableIntStateOf(0) }
    val uiState by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(currentPageIndex) {
        Log.i("HomeRecentPage", "Current Page: ${currentPageIndex + 1}")
        model.updatePage(currentPageIndex)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "最近记录") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                },
                actions = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { currentPageIndex -= 1 }, enabled = currentPageIndex > 0) {
                            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Page")
                        }
                        Text(text = "${currentPageIndex + 1}")
                        IconButton(onClick = { currentPageIndex += 1 }, enabled = if (model.user.mode == 0) currentPageIndex + 1 < model.chuAvailablePage else currentPageIndex + 1 < model.maiAvailablePage) {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Page")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }, elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
                defaultElevation = 5.dp
            )) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "回到顶部")
            }
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
            if (model.user.mode == 0) {
                items(
                    count = uiState.chuRecentList.size,
                    key = { index -> uiState.chuRecentList[index].timestamp },
                    itemContent = { index ->
                        HomeRecentPageEntry(
                            entry = uiState.chuRecentList[index],
                            index = model.chuRecentList.indexOf(uiState.chuRecentList[index]),
                            navController = navController
                        )
                    }
                )
            } else {
                items(
                    count = uiState.maiRecentList.size,
                    key = { index -> uiState.maiRecentList[index].timestamp },
                    itemContent = { index ->
                        HomeRecentPageEntry(
                            entry = uiState.maiRecentList[index],
                            index = model.maiRecentList.indexOf(uiState.maiRecentList[index]),
                            navController = navController
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun HomeRecentPageEntry(entry: UserMaimaiRecentScoreEntry, index: Int, navController: NavController) {
    val context = LocalContext.current

    ElevatedCard (
        onClick = {
            Log.i("HomeRecentPageEntry", "Jump from index $index")
            navController.navigate(HomeNavItem.Home.route + "/recent/maimai/${index}")
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = entry.associatedMusicEntry.musicId.toMaimaiCoverPath(),
                contentDescription = "歌曲封面",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(72.dp)
                    .border(
                        border = BorderStroke(
                            width = 2.dp, color = maimaiDifficultyColors[entry.levelIndex]
                        ), shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(size = 10.dp))
            )
            Column(
                Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(entry.timestamp.toDateString(context), fontSize = 14.sp)
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Text(
                        entry.associatedMusicEntry.title,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                    Text(
                        text = "%.4f".format(entry.achievements).plus("%"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HomeRecentPageEntry(entry: UserChunithmRecentScoreEntry, index: Int, navController: NavController) {
    val context = LocalContext.current

    ElevatedCard (
        onClick = {
            Log.i("HomeRecentPageEntry", "Jump from index $index")
            navController.navigate(HomeNavItem.Home.route + "/recent/chunithm/${index}")
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = entry.associatedMusicEntry.musicId.toChunithmCoverPath(),
                contentDescription = "歌曲封面",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(72.dp)
                    .border(
                        border = BorderStroke(
                            width = 2.dp, color = chunithmDifficultyColors[entry.levelIndex]
                        ), shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(size = 10.dp))
            )
            Column(
                Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(entry.timestamp.toDateString(context), fontSize = 14.sp)
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Text(
                        entry.associatedMusicEntry.title,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                    Text(
                        text = entry.score.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}