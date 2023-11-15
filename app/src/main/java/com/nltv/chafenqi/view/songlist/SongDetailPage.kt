package com.nltv.chafenqi.view.songlist

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING

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
    val scrollState = rememberScrollState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "歌曲详情") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
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
                        fontSize = 18.sp
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
                    model.constants.forEach {
                        Text(text = it, fontSize = 16.sp)
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
                    if (!model.checkBilibili(context)) {
                        Toast.makeText(
                            context,
                            "无法打开B站客户端，请检查权限或是否已安装B站",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@TextButton
                    }
                    try {
                        uriHandler.openUri(
                            Uri.parse("bilibili://search")
                                .buildUpon()
                                .appendQueryParameter("keyword", "${model.title} $gameName")
                                .build()
                                .toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "无法打开B站客户端，请检查权限或是否已安装B站",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                // modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
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
                    MaimaiDifficultyCard(info = it)
                }
            } else if (mode == 0) {
                model.chuDiffInfos.onEach {
                    ChunithmDifficultyCard(info = it)
                }
            }
        }
    }
}

@Composable
fun MaimaiDifficultyCard(info: MaimaiDifficultyInfo) {
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
fun ChunithmDifficultyCard(info: ChunithmDifficultyInfo) {
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