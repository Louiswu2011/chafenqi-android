package com.nltv.chafenqi.view.songlist

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.data.ChunithmMusicStat
import com.nltv.chafenqi.extension.RATE_COLORS_CHUNITHM
import com.nltv.chafenqi.extension.RATE_STRINGS_CHUNITHM
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailPage(
    mode: Int,
    index: Int,
    navController: NavController
) {
    val model: SongDetailViewModel = viewModel<SongDetailViewModel>().also {
        it.update(mode, index)
    }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember {
        SnackbarHostState()
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
            horizontalAlignment = Alignment.CenterHorizontally
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
        }
    }
}

@Composable
fun MaimaiDifficultyCard(info: MaimaiDifficultyInfo, navController: NavController) {
    val model: SongDetailViewModel = viewModel()

    var isExpanded by remember {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, label = "Icon expansion"
    )

    Card(
        Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
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
                    if (info.hasRecentEntry) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "查看详情",
                            modifier = Modifier.clickable {
                                navController.navigate(HomeNavItem.SongList.route + "/maimai/${model.index}/${info.levelIndex}")
                            }
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "展开按钮图标",
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                            .rotate(rotationState)
                    )
                }
            }
            AnimatedVisibility(visible = isExpanded) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "定数：${info.constant}")
                    Text(text = "谱师：${info.charter}")
                }
            }
        }
    }
}

@Composable
fun ChunithmDifficultyCard(info: ChunithmDifficultyInfo, navController: NavController) {
    val model: SongDetailViewModel = viewModel()

    var isExpanded by remember {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, label = "Icon expansion"
    )

    Card(
        Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
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
                    if (info.hasRecentEntry) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "查看详情",
                            modifier = Modifier.clickable {
                                navController.navigate(HomeNavItem.SongList.route + "/chunithm/${model.index}/${info.levelIndex}")
                            }
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "展开按钮图标",
                        modifier = Modifier
                            .clickable {
                                isExpanded = !isExpanded
                            }
                            .rotate(rotationState)
                    )
                }
            }
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "定数：${info.constant}")
                        Text(text = "谱师：${info.charter}")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { navController.navigate(HomeNavItem.SongList.route + "/chunithm/stats/${model.index}/${info.levelIndex}") }) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = "leaderboard icon",
                                Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = "排行榜和统计信息")
                        }
                    }
                }
            }
        }
    }
}