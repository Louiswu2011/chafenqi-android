package com.nltv.chafenqi.view.home.team

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.model.team.TeamMember
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPage(navController: NavController) {
    val model: HomeTeamPageViewModel = viewModel()

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        model.tabs.size
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "团队") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                model.tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = tab.title) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex == index) tab.iconSelected else tab.icon,
                                contentDescription = tab.title
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                when (index) {
                    0 -> { HomeTeamPageMemberList(list = listOf(TeamMember.sample)) }
                    1 -> {}
                    2 -> {}
                    3 -> {}
                }
            }
        }
    }
}

@Composable
fun HomeTeamPageMemberList(
    list: List<TeamMember>
) {
    LazyColumn {
        items(
            count = list.size,
            key = { index -> list[index].id }
        ) {
            HomeTeamPageMemberEntry(member = list[it])
        }
    }
}

@Composable
fun HomeTeamPageMemberEntry(
    member: TeamMember
) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(10.dp),
        shape = ShapeDefaults.Medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
               .padding(10.dp)
               .padding(vertical = 2.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = member.trophy)
                    Text(text = member.nickname, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                }
                AsyncImage(
                    model = member.avatar,
                    contentDescription = "头像",
                    modifier = Modifier.weight(0.2f),
                    contentScale = ContentScale.FillWidth,
                    clipToBounds = true
                )
            }

            Text(text = "加入时间：${member.joinAt}")
            Text(text = "贡献点数：${member.activityPoints}")
            Text(text = "游戏局数：${member.playCount}")
            Text(text = "最后游玩时间：${member.lastActivityAt}")
        }
    }
}

@Preview
@Composable
fun HomeTeamPageMemberEntryPreview() {
    HomeTeamPageMemberEntry(
        member = TeamMember(
            id = 1,
            userId = 1L,
            nickname = "LOUISE/",
            joinAt = Clock.System.now().epochSeconds,
            avatar = "https://new.chunithm-net.com/chuni-mobile/html/mobile/img/f9ed64ced3d22730.png",
            trophy = "What's Up? Pop!",
            rating = "17.03",
            activityPoints = 80240L,
            playCount = 215,
            lastActivityAt = Clock.System.now().epochSeconds,
        )
    )
}