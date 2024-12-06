package com.nltv.chafenqi.view.home.team

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.model.team.TeamMember
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPage(navController: NavController) {
    val coroutinesScope = rememberCoroutineScope()
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        model.tabs.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.targetPage) {
        selectedTabIndex = pagerState.targetPage
    }

    LaunchedEffect(Unit) {
        model.refresh()
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
                    0 -> { HomeTeamPageMemberList(list = state.team.members) }
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
    val haptics = LocalHapticFeedback.current
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    var selectedTeamMemberUserId by rememberSaveable {
        mutableLongStateOf(-1L)
    }

    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = list.size,
            key = { index -> index }
        ) {
            HomeTeamPageMemberEntry(
                member = list[it],
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    selectedTeamMemberUserId = list[it].userId
                }
            )
        }
    }

    if (selectedTeamMemberUserId != -1L) {
        val member = state.team.members.firstOrNull { it.userId == selectedTeamMemberUserId }
        if (member != null) {
            HomeTeamPageMemberManageSheet(
                member = member,
            ) {
                selectedTeamMemberUserId = -1L
            }
        } else {
            selectedTeamMemberUserId = -1L
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTeamPageMemberEntry(
    member: TeamMember,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current

    @Composable
    fun MemberInfoRow(
        description: String,
        value: String
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = description, style = MaterialTheme.typography.bodySmall)
            Text(text = value, style = MaterialTheme.typography.bodySmall)
        }
    }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    onLongClick()
                },
                onLongClickLabel = "管理成员"
            ),
        shape = ShapeDefaults.Medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .padding(vertical = 2.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = member.trophy, style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    Text(text = member.nickname, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Rating", style = MaterialTheme.typography.titleSmall)
                            Text(member.rating, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }

                        Row (
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("游玩次数", style = MaterialTheme.typography.titleSmall)
                            Text("${member.playCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
                AsyncImage(
                    model = member.avatar,
                    contentDescription = "头像",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(75.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.FillHeight,
                    clipToBounds = true
                )
            }

            MemberInfoRow(description = "加入时间：", value = member.joinAt.toDateString(context))
            MemberInfoRow(description = "贡献点数：", value = "${member.activityPoints}")
            MemberInfoRow(description = "最后游玩时间：", value = member.lastActivityAt.toDateString(context))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPageMemberManageSheet(
    member: TeamMember,
    onDismissRequest: () -> Unit
) {
    var shouldShowConfirmDialog by rememberSaveable {
        mutableStateOf(false)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        ListItem(
            headlineContent = { Text(text = "移除该成员") },
            leadingContent = { Icon(Icons.Default.GroupRemove, contentDescription = "移除成员") },
            modifier = Modifier.clickable(
                onClick = {
                    shouldShowConfirmDialog = true
                }
            )
        )
    }

    if (shouldShowConfirmDialog) {
        HomeTeamPageConfirmDialog(
            icon = Icons.Outlined.Warning,
            title = "确认移除",
            message = "您确定要将此成员从您的团队中移除吗？该操作无法撤销。",
            onConfirm = { shouldShowConfirmDialog = false },
            onDismissRequest = { shouldShowConfirmDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPageConfirmDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = { Icon(imageVector = icon, contentDescription = title) },
        title = { Text(text = title) },
        text = { Text(text = message) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("取消")
            }
        }
    )
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
        ),
        onLongClick = {}
    )
}