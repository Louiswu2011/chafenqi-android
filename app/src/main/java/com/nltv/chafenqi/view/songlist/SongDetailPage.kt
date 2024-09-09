package com.nltv.chafenqi.view.songlist

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.data.Comment
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.songlist.comment.CommentCard
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailPage(
    mode: Int,
    index: Int,
    navController: NavController
) {
    val model: SongDetailViewModel = viewModel()
    val uriHandler = LocalUriHandler.current
    val state by model.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        scope.launch {
            model.update(mode, index)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "歌曲详情") },
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
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(SCREEN_PADDING)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AsyncImage(
                    model = model.coverUrl,
                    contentDescription = "歌曲封面",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column (
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { model.toggleLoved(state.loved) },
                            enabled = !state.syncing
                        ) {
                            Icon(
                                imageVector = if (state.loved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Fav Button",
                                tint = if (state.loved) Color.Red else Color.Gray
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = model.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = model.artist,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    model.constants.forEachIndexed { index, string ->
                        Text(text = string, fontSize = 16.sp, color = model.difficultyColors[index])
                    }
                }
                Text(text = "BPM: ${model.bpm}", fontSize = 16.sp)
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(text = model.version)
            }
            TextButton(
                onClick = {
                    val gameName = if (mode == 0) "中二节奏" else "maimai"
                    try {
                        uriHandler.openUri(
                            Uri.parse("bilibili://search")
                                .buildUpon()
                                .appendQueryParameter("keyword", "${model.title} $gameName")
                                .build()
                                .toString()
                        )
                    } catch (e: Exception) {
                        scope.launch { snackbarHostState.showSnackbar("无法打开B站客户端，请检查权限或是否已安装B站") }
                    }
                },
                // modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "搜索谱面确认",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "在Bilibili搜索谱面确认")
                }
            }
            if (mode == 0) {
                ChunithmChartCard()
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "游玩记录",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (mode == 1) {
                model.maiDiffInfos.onEach {
                    MaimaiDifficultyCard(info = it, navController)
                }
            } else if (mode == 0) {
                model.chuDiffInfos.onEach {
                    ChunithmDifficultyCard(info = it, navController)
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "评论",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(
                    onClick = {
                        val game = if (mode == 0) "chunithm" else "maimai"
                        navController.navigate(HomeNavItem.SongList.route + "/${game}/${index}/comment")
                    }
                ) {
                    Text(text = "更多")
                }
            }
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items (
                    count = minOf(3, state.comments.size),
                    key = { index -> state.comments[index].id },
                    itemContent = { index ->
                        CommentCard(comment = state.comments[index])
                    }
                )
            }
        }
    }
}

@Composable
fun MaimaiDifficultyCard(info: MaimaiDifficultyInfo, navController: NavController) {
    val model: SongDetailViewModel = viewModel()

    Card(
        Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = info.color),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = info.difficultyName)
                Row {
                    Text(text = if (info.bestEntry == null) "暂未游玩" else info.bestScore)
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "查看详情",
                        modifier = Modifier.clickable {
                            navController.navigate(HomeNavItem.SongList.route + "/maimai/stats/${model.index}/${info.levelIndex}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChunithmDifficultyCard(info: ChunithmDifficultyInfo, navController: NavController) {
    val model: SongDetailViewModel = viewModel()

    Card(
        Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = info.color),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = info.difficultyName)
                Row {
                    Text(text = if (info.bestEntry == null) "暂未游玩" else info.bestScore)
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "查看详情",
                        modifier = Modifier.clickable {
                            navController.navigate(HomeNavItem.SongList.route + "/chunithm/stats/${model.index}/${info.levelIndex}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChunithmChartCard() {
    val model: SongDetailViewModel = viewModel()
    val context = LocalContext.current
    val state by model.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val diff by rememberSaveable {
        mutableIntStateOf(3)
    }

    LaunchedEffect(key1 = diff) {
        model.updateChartUrls(diff)
    }

    if (state.chartExpanded) {
        ModalBottomSheet(
            onDismissRequest = { model.toggleExpand() },
            sheetState = sheetState
        ) {
            ChunithmExpandedChartCard()
        }
    }

    Column {
        Row(
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Text(
                text = "谱面预览",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(10.dp))
                .background(Color.Gray.copy(alpha = 0.2F))
                .clickable {
                    model.toggleExpand()
                }
        ) {
            state.chartUrls.forEachIndexed { index, url ->
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentDescription = "Chunithm Chart Layer",
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}

@Composable
fun ChunithmExpandedChartCard() {
    val scrollState = rememberScrollState()

    val model: SongDetailViewModel = viewModel()
    val context = LocalContext.current
    val state by model.uiState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .horizontalScroll(state = scrollState)
            .fillMaxSize()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            state.chartUrls.forEachIndexed { index, url ->
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentDescription = "Chunithm Chart Layer",
                    onSuccess = {
                        model.updateChartImage(index, it.result.drawable)
                    }
                )
            }
        }
    }
}