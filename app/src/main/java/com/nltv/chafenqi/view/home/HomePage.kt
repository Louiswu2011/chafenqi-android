package com.nltv.chafenqi.view.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nltv.chafenqi.R
import com.nltv.chafenqi.storage.CFQUser
import com.nltv.chafenqi.view.AppViewModelProvider
import com.nltv.chafenqi.view.songlist.SongListPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "主页") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomePageNameplate()
            HomePageRecentBar()
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) {
                    HomePageRecentEntry()
                }
            }
        }
    }
}

@Composable
fun HomePageNameplate() {
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
                    text = "游戏昵称",
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                HomePageNameplateInfoRow(title = "Rating", content = "16318")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomePageNameplateInfoRow(title = "P", content = "14321")
                    HomePageNameplateInfoRow(title = "N", content = "4321")
                }
                HomePageNameplateInfoRow(title = "游玩次数", content = "2471")
                Spacer(modifier = Modifier.size(8.dp))
                HomePageNameplateInfoRow(title = "更新于", content = "10-20 13:20")
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
fun HomePageRecentBar() {
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

            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePageRecentEntry() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.nameplate_salt),
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
                Text("通关时间", fontSize = 14.sp)
                Text(text = "状态", fontWeight = FontWeight.Bold)
            }
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text("曲名", fontSize = 16.sp)
                Text(text = "达成率", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HomePageNameplate()
        HomePageRecentBar()
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                HomePageRecentEntry()
            }
        }
    }
}