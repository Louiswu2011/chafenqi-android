package com.nltv.chafenqi.view.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.R
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.storage.room.user.maimai.MaimaiRecentScoreEntry
import com.nltv.chafenqi.view.AppViewModelProvider
import com.nltv.chafenqi.view.songlist.SongListPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val scrollState = rememberScrollState()

    val maiRecents by model.userMaiRecentState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "主页") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(HomeNavItem.Home.route + "/settings") }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomePageNameplate()
            HomePageRecentBar(navController)
            Column(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                maiRecents.data.take(3).onEach { entry ->
                    HomePageRecentEntry(entry = entry)
                }
            }
        }
    }
}

@Composable
fun HomePageNameplate() {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = ShapeDefaults.Medium,
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation()
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nameplate_salt),
                    contentDescription = "名牌纱露朵形象",
                    Modifier.size(128.dp)
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = model.userMaimaiInfo.nickname,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                HomePageNameplateInfoRow(title = "Rating", content = "${model.userMaimaiInfo.rating}")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomePageNameplateInfoRow(title = "P", content = "-")
                    HomePageNameplateInfoRow(title = "N", content = "-")
                }
                HomePageNameplateInfoRow(title = "游玩次数", content = "${model.userMaimaiInfo.playCount}")
                Spacer(modifier = Modifier.size(8.dp))
                HomePageNameplateInfoRow(title = "更新于", content = "-")
            }
        }
    }
}

@Composable
fun HomePageNameplateInfoRow(title: String, content: String) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = title, modifier = Modifier.padding(end = 8.dp))
        Text(text = content, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HomePageRecentBar(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(text = "最近动态", fontWeight = FontWeight.Bold, fontSize = TextUnit(16f, TextUnitType.Sp))
        Text(
            text = "显示全部",
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.clickable {
                navController.navigate(HomeNavItem.Home.route + "/recent")
            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun HomePageRecentEntry(entry: MaimaiRecentScoreEntry) {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = model.getAssociatedMaimaiMusic(title = entry.title).id.toMaimaiCoverPath(),
            contentDescription = "最近动态歌曲封面",
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
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween
            ) {
                Text(entry.timestamp.toDateString(), fontSize = 14.sp)
                Text(text = "状态", fontWeight = FontWeight.Bold)
            }
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(entry.title, fontSize = 16.sp)
                Text(text = "%.4f".format(entry.achievements).plus("%"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun HomePageRatingBar(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(text = "Rating分析", fontWeight = FontWeight.Bold, fontSize = TextUnit(16f, TextUnitType.Sp))
        Text(
            text = "显示全部",
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.clickable {
                navController.navigate(HomeNavItem.Home.route + "/rating")
            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        HomePageNameplate()
        // HomePageRecentBar()
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                HomePageRecentEntry(MaimaiRecentScoreEntry())
            }
        }
    }
}