package com.nltv.chafenqi.view.songlist

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.CHUNITHM_GENRE_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_LEVEL_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_VERSION_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_GENRE_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_LEVEL_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_VERSION_STRINGS
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.extension.useDebounce
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListPage(navController: NavController) {
    val model: SongListPageViewModel = viewModel()

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    BackHandler(true) {
        if (navController.currentBackStackEntry?.destination?.route != HomeNavItem.SongList.route) {
            navController.navigateUp()
        } else if (listState.firstVisibleItemIndex > 0) {
            coroutineScope.launch { listState.animateScrollToItem(index = 0) }
        }
    }

    SongListLevelFilterDialog()
    SongListConstantFilterDialog()
    SongListGenreFilterDialog()
    SongListVersionFilterDialog()

    Scaffold(
        /*topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "歌曲列表") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },*/
        floatingActionButton = {
            if (!model.isSearchBarActive) {
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "回到顶部")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ) {
            SongListSearchBar(navController)
            SongListFilterChips()
            AnimatedVisibility(visible = !model.isSearchBarActive) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    state = listState
                ) {
                    when (model.user.mode) {
                        0 -> {
                            items(
                                count = uiState.chuMusicList.size,
                                key = { index ->
                                    uiState.chuMusicList[index].hashCode()
                                },
                                itemContent = { index ->
                                    val entry = uiState.chuMusicList[index]
                                    ChunithmMusicListEntry(entry, index, navController)
                                }
                            )
                        }

                        1 -> {
                            items(
                                count = uiState.maiMusicList.size,
                                key = { index ->
                                    uiState.maiMusicList[index].hashCode()
                                },
                                itemContent = { index ->
                                    val entry = uiState.maiMusicList[index]
                                    MaimaiMusicListEntry(entry, index, navController)
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListSearchBar(navController: NavController) {
    val model: SongListPageViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val maiResultListState = rememberLazyListState()
    val chuResultListState = rememberLazyListState()

    var queryText by remember {
        mutableStateOf("")
    }

    queryText.useDebounce(onChange = {
        scope.launch { model.onSearchQueryChange(it) }
    }, delayMillis = 500L)

    val searchBarHorizontalPadding by animateDpAsState(
        targetValue = if (model.isSearchBarActive) 0.dp else 10.dp,
        label = "animated search bar horizontal padding"
    )

    val uiState by model.uiState.collectAsStateWithLifecycle()
    val chuSearchResult = uiState.chuSearchResult
    val maiSearchResult = uiState.maiSearchResult

    SearchBar(
        query = queryText,
        onQueryChange = { newQuery -> queryText = newQuery },
        onSearch = { scope.launch { model.onSearchQueryChange(queryText) } },
        active = model.isSearchBarActive,
        onActiveChange = { activeChange -> model.isSearchBarActive = activeChange },
        placeholder = { Text(text = "输入曲名或作曲家") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索歌曲列表"
            )
        },
        trailingIcon = {
            if (model.isSearchBarActive) {
                IconButton(onClick = {
                    if (model.searchQuery.isNotEmpty()) {
                        model.onSearchQueryChange("")
                    } else {
                        model.isSearchBarActive = false
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "清除或关闭"
                    )
                }
            }
        },
        modifier = Modifier
            .padding(horizontal = searchBarHorizontalPadding)
            .padding(bottom = SCREEN_PADDING)
            .fillMaxWidth()
    ) {
        when (model.user.mode) {
            0 -> {
                when {
                    chuSearchResult.isNotEmpty() -> {
                        LazyColumn(
                            Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            state = chuResultListState
                        ) {
                            items(
                                count = chuSearchResult.size,
                                key = { index ->
                                    chuSearchResult[index].musicID
                                }
                            ) { index ->
                                ChunithmMusicListEntry(
                                    music = chuSearchResult[index],
                                    index = -1,
                                    navController = navController
                                )
                            }
                        }
                    }

                    model.searchQuery.isNotEmpty() -> {
                        SongListSearchEmptyState()
                    }
                }
            }

            1 -> {
                when {
                    maiSearchResult.isNotEmpty() -> {
                        LazyColumn(
                            Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            state = maiResultListState
                        ) {
                            items(
                                count = maiSearchResult.size,
                                key = { index ->
                                    maiSearchResult[index].musicID
                                }
                            ) { index ->
                                MaimaiMusicListEntry(
                                    music = maiSearchResult[index],
                                    index = -1,
                                    navController = navController
                                )
                            }
                        }
                    }

                    model.searchQuery.isNotEmpty() -> {
                        SongListSearchEmptyState()
                    }
                }
            }
        }
    }
}

@Composable
fun SongListFilterChips() {
    val scrollState = rememberScrollState()
    val model: SongListPageViewModel = viewModel()
    val levelStrings = if (model.user.mode == 0) CHUNITHM_LEVEL_STRINGS else MAIMAI_LEVEL_STRINGS
    val genreStrings = if (model.user.mode == 0) CHUNITHM_GENRE_STRINGS else MAIMAI_GENRE_STRINGS
    val versionStrings =
        if (model.user.mode == 0) CHUNITHM_VERSION_STRINGS else MAIMAI_VERSION_STRINGS
    val filterLevelList =
        if (model.user.mode == 0) model.filterChunithmLevelList else model.filterMaimaiLevelList
    val filterGenreList =
        if (model.user.mode == 0) model.filterChunithmGenreList else model.filterMaimaiGenreList
    val filterVersionList =
        if (model.user.mode == 0) model.filterChunithmVersionList else model.filterMaimaiVersionList

    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.padding(start = 0.dp))
        FilterChip(
            selected = model.filterFavorite,
            onClick = { 
                model.filterFavorite = !model.filterFavorite
                model.update()
            },
            label = { Text(text = "喜爱歌曲") },
            leadingIcon = { Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Fav") }
        )
        FilterChip(
            selected = model.filterPlayed,
            onClick = {
                model.filterPlayed = !model.filterPlayed
                model.update()
            },
            label = {
                Text(
                    text = "只显示已游玩"
                )
            }
        )
        if (model.filterLevel && filterLevelList.any { it }) {
            InputChip(
                selected = model.filterLevel,
                onClick = {
                    model.filterLevel = false
                    model.update()
                },
                label = {
                    Text(text = "Lv " + filterLevelList.foldIndexed("") { index: Int, acc: String, b: Boolean ->
                        acc + if (b) {
                            levelStrings[index] + ", "
                        } else {
                            ""
                        }
                    }.removeSuffix(", "))
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "清除等级条件")
                }
            )
        } else {
            SuggestionChip(
                onClick = { model.showFilterLevelDialog = true },
                label = { Text(text = "等级") }
            )
        }
        if (model.filterConstant) {
            InputChip(
                selected = model.filterConstant,
                onClick = {
                    model.filterConstant = false
                    model.update()
                },
                label = {
                    Text(
                        text = "定数 " + if (model.filterConstantUpperBound == model.filterConstantLowerBound) {
                            String.format("%.1f", model.filterConstantUpperBound)
                        } else {
                            "${
                                String.format(
                                    "%.1f",
                                    model.filterConstantLowerBound
                                )
                            }-${String.format("%.1f", model.filterConstantUpperBound)}"
                        }
                    )
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "清除定数条件")
                }
            )
        } else {
            SuggestionChip(
                onClick = { model.showFilterConstantDialog = true },
                label = { Text(text = "定数") }
            )
        }
        if (model.filterGenre) {
            InputChip(
                selected = model.filterGenre,
                onClick = {
                    model.filterGenre = false
                    model.update()
                },
                label = {
                    Text(
                        text = filterGenreList.foldIndexed("") { index: Int, acc: String, b: Boolean ->
                            acc + if (b) {
                                genreStrings[index] + ", "
                            } else {
                                ""
                            }
                        }.removeSuffix(", ")
                    )
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "清除分类条件")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Category, contentDescription = "分类")
                }
            )
        } else {
            SuggestionChip(
                onClick = { model.showFilterGenreDialog = true },
                label = { Text(text = "分类") }
            )
        }
        if (model.filterVersion) {
            InputChip(
                selected = model.filterVersion,
                onClick = {
                    model.filterVersion = false
                    model.update()
                },
                label = {
                    Text(
                        text = filterVersionList.foldIndexed("") { index: Int, acc: String, b: Boolean ->
                            acc + if (b) {
                                versionStrings[index] + ", "
                            } else {
                                ""
                            }
                        }.removeSuffix(", ")
                    )
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "清除版本条件")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.VideogameAsset, contentDescription = "版本")
                }
            )
        } else {
            SuggestionChip(
                onClick = { model.showFilterVersionDialog = true },
                label = { Text(text = "版本") }
            )
        }
        Spacer(modifier = Modifier.padding(end = 0.dp))
    }
}

@Composable
fun SongListSearchEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "无搜索结果",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "试试调整搜索关键词",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MaimaiMusicListEntry(music: MaimaiMusicEntry, index: Int, navController: NavController) {
    val model: SongListPageViewModel = viewModel()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(vertical = 8.dp)
            .clickable {
                if (index == -1 || model.filterEnabled) {
                    Log.i("SongList", "filter enabled: ${model.filterEnabled}")
                    val listIndex = model.origMaiMusicList.indexOf(music)
                    navController.navigate(HomeNavItem.SongList.route + "/maimai/$listIndex")
                    return@clickable
                }
                navController.navigate(HomeNavItem.SongList.route + "/maimai/$index")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = music.musicID.toMaimaiCoverPath(),
            contentDescription = "${music.title}的封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(size = 10.dp)),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = music.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = music.basicInfo.artist,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            MaimaiLevelBadgeRow(musicEntry = music)
        }
    }
}

@Composable
fun ChunithmMusicListEntry(music: ChunithmMusicEntry, index: Int, navController: NavController) {
    val model: SongListPageViewModel = viewModel()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(vertical = 8.dp)
            .clickable {
                if (index == -1 || model.filterEnabled) {
                    val listIndex = model.origChuMusicList.indexOf(music)
                    navController.navigate(HomeNavItem.SongList.route + "/chunithm/$listIndex")
                    return@clickable
                }
                navController.navigate(HomeNavItem.SongList.route + "/chunithm/$index")
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = music.musicID.toChunithmCoverPath(),
            contentDescription = "${music.title}的封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(size = 10.dp)),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = music.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = music.artist,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ChunithmLevelBadgeRow(musicEntry = music)
        }
    }
}

@Composable
fun MaimaiLevelBadgeRow(musicEntry: MaimaiMusicEntry) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        musicEntry.charts.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color = maimaiDifficultyColors[index]),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = musicEntry.level[index],
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ChunithmLevelBadgeRow(musicEntry: ChunithmMusicEntry) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        musicEntry.charts.indexedList.forEachIndexed { index, entry ->
            if (entry.enabled) {
                Box(
                    modifier = Modifier
                        .size(width = 30.dp, height = 18.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color = chunithmDifficultyColors[index]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.level,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}