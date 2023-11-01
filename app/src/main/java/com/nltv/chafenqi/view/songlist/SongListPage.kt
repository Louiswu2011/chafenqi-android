package com.nltv.chafenqi.view.songlist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.`object`.CFQPersistentData
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.view.AppViewModelProvider
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListPage(navController: NavController) {
    val model: SongListPageViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var isSearchBarActive by remember {
        mutableStateOf(false)
    }
    val searchBarHorizontalPadding by animateDpAsState(
        targetValue = if (isSearchBarActive) 0.dp else 8.dp,
        label = "animated search bar horizontal padding"
    )
    var searchInput by remember {
        mutableStateOf("")
    }

    BackHandler(true) {
        if (navController.currentBackStackEntry?.destination?.route != HomeNavItem.SongList.route) {
            navController.navigateUp()
        } else if (listState.firstVisibleItemIndex > 0) {
            coroutineScope.launch { listState.animateScrollToItem(index = 0) }
        }
    }

//    SearchBar(
//        query = searchInput,
//        onQueryChange = { newQuery -> searchInput = newQuery },
//        onSearch = {  },
//        active = isSearchBarActive,
//        onActiveChange = { activeChange -> isSearchBarActive = activeChange },
//        placeholder = { Text(text = "输入曲名或作曲家") },
//        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "搜索歌曲列表") },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = searchBarHorizontalPadding)
//    ) {
//
//    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "歌曲列表") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            state = listState
        ) {
            items(
                count = CFQPersistentData.Maimai.musicList.size,
                key = { index ->
                    CFQPersistentData.Maimai.musicList[index].id
                },
                itemContent = {index ->
                    MaimaiMusicListEntry(music = CFQPersistentData.Maimai.musicList[index], index, navController)
                }
            )
        }
    }
}

@Composable
fun MaimaiMusicListEntry(music: MaimaiMusicEntry, index: Int, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate(HomeNavItem.SongList.route + "/maimai/$index")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = music.id.toMaimaiCoverPath(),
            contentDescription = "${music.title}的封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .aspectRatio(1f)
                .width(64.dp)
                .clip(RoundedCornerShape(size = 10.dp)),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = music.title, fontWeight = FontWeight.Bold, fontSize = TextUnit(16f, TextUnitType.Sp))
            Text(text = music.level.joinToString(" "))
        }
    }
}