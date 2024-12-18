package com.nltv.chafenqi.view.home.team

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.extension.TEAM_CODE_LENGTH
import com.nltv.chafenqi.extension.TEAM_NAME_LENGTH
import com.nltv.chafenqi.extension.TEAM_REMARKS_LENGTH
import com.nltv.chafenqi.extension.TEAM_STYLE_LENGTH
import com.nltv.chafenqi.model.team.TeamCreatePayload
import com.nltv.chafenqi.networking.CFQTeamServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamIntroductionPage(navController: NavController) {
    val model: HomeTeamPageViewModel = viewModel()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { model.introTabs.size }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.targetPage) {
        selectedTabIndex = pagerState.targetPage
    }

    Scaffold (
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
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                model.introTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex == index) tab.iconSelected else tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        text = { Text(text = tab.title) }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                when (index) {
                    0 -> { HomeTeamIntroductionPageSearchSection() }
                    1 -> { HomeTeamIntroductionPageCreateSection(snackbarHostState) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamIntroductionPageSearchSection() {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    var teamCodeInput by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = teamCodeInput,
            onValueChange = {
                if (it.length <= TEAM_CODE_LENGTH) {
                    teamCodeInput = it
                }
            },
            label = { Text(text = "团队代码") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "搜索")
            },
            trailingIcon = {
                IconButton(
                    onClick = { teamCodeInput = "" },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "清除")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    println("Search for team code: $teamCodeInput")
                }
            ),
            supportingText = {
                Text(
                    text = "${teamCodeInput.length} / $TEAM_CODE_LENGTH",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Crossfade(teamCodeInput.isEmpty(), label = "Search result fade") {
            when (it) {
                true -> {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "输入团队代码来搜索想加入的团队...")
                    }
                }
                false -> {}
            }
        }
    }
}

@Composable
fun HomeTeamIntroductionPageCreateSection(snackbarHostState: SnackbarHostState) {
    val model: HomeTeamPageViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var shouldShowForm by remember { mutableStateOf(false) }
    var teamName by remember { mutableStateOf("") }
    var teamStyle by remember { mutableStateOf("") }
    var teamRemarks by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }
    var promotable by remember { mutableStateOf(true) }

    @Composable
    fun TeamIntroduction() {
        @Composable
        fun TeamIntroductionRow(
            title: String,
            message: String,
            icon: ImageVector
        ) {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(320.dp)
            ) {
                Icon(imageVector = icon, contentDescription = title)
                Column (
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = message)
                }
            }
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("团队功能介绍", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                TeamIntroductionRow(
                    title = "成员一览",
                    message = "通过团队代码邀请好友加入团队，并在App内查看成员的Rating和游玩次数等信息。",
                    icon = Icons.Filled.People
                )
                TeamIntroductionRow(
                    title = "留言板",
                    message = "使用队内的留言板给队友留言。留言对所有人可见。",
                    icon = Icons.AutoMirrored.Filled.Chat
                )
                TeamIntroductionRow(
                    title = "月间排行",
                    message = "通过在机台上游玩并上传成绩来为团队积攒点数，并参加月间的团队点数排行榜。",
                    icon = Icons.Filled.Leaderboard
                )
                TeamIntroductionRow(
                    title = "组曲挑战",
                    message = "队长可以挑选三首歌曲作为团队的组曲挑战。成员在机台按顺序游玩后上传成绩，即可同步到App内，并参加队内排行榜。",
                    icon = Icons.Filled.Ballot
                )
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("注：创建团队需要订阅会员", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                Button(
                    onClick = {
                        shouldShowForm = true
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "创建团队")
                }
            }
        }
    }

    @Composable
    fun TeamCreateForm() {
        val teamCreateTermsText = buildAnnotatedString {
            val linkStyle = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )

            append("我同意并接受查分器NEW的团队功能")

            withLink(LinkAnnotation.Url(url = "https://google.com/policy")) {
                withStyle(style = linkStyle) {
                    append("使用条款")
                }
            }
        }

        Column (
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Form Body
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { if (teamName.length <= TEAM_NAME_LENGTH) teamName = it },
                    label = { Text("团队名称") },
                    supportingText = {
                        Text(
                            text = "${teamName.length} / $TEAM_NAME_LENGTH",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teamStyle,
                    onValueChange = { if (teamStyle.length <= TEAM_STYLE_LENGTH) teamStyle = it },
                    label = { Text("团队方针") },
                    supportingText = {
                        Text(
                            text = "${teamStyle.length} / $TEAM_STYLE_LENGTH",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    placeholder = { Text("例如：自由加入、活跃者优先等") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teamRemarks,
                    onValueChange = { if (teamRemarks.length <= TEAM_REMARKS_LENGTH) teamRemarks = it },
                    label = { Text("团队介绍") },
                    supportingText = {
                        Text(
                            text = "${teamRemarks.length} / $TEAM_REMARKS_LENGTH",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row (
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = promotable,
                        onCheckedChange = { promotable = it }
                    )

                    Text("使团队可被搜索（稍后可变更）")
                }

                Row (
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it }
                    )

                    Text(text = teamCreateTermsText)
                }

            }

            // Action Buttons
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        teamName = ""
                        teamRemarks = ""
                        teamStyle = ""
                        promotable = true
                        agreedToTerms = false
                        shouldShowForm = false
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("取消")
                }

                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val errorText = CFQTeamServer.createTeam(
                                authToken = model.token,
                                payload = TeamCreatePayload(
                                    game = model.mode,
                                    displayName = teamName,
                                    style = teamStyle,
                                    remarks = teamRemarks,
                                    promotable = promotable
                                )
                            )
                            if (errorText.isNotEmpty()) {
                                snackbarHostState.showSnackbar("创建团队失败: $errorText")
                                return@launch
                            }

                            model.refresh()
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    enabled = teamName.isNotBlank() && teamRemarks.isNotBlank() && agreedToTerms,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("创建")
                }
            }
        }
    }

    Crossfade(shouldShowForm, label = "Form fade") {
        when (it) {
            true -> TeamCreateForm()
            false -> TeamIntroduction()
        }
    }

}