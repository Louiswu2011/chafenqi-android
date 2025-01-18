package com.nltv.chafenqi.view.home.team.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.IconSource
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.info.InfoDialog
import com.maxkeppeler.sheets.info.models.InfoBody
import com.maxkeppeler.sheets.info.models.InfoSelection
import com.maxkeppeler.sheets.input.InputDialog
import com.maxkeppeler.sheets.input.models.InputHeader
import com.maxkeppeler.sheets.input.models.InputSelection
import com.maxkeppeler.sheets.input.models.InputTextField
import com.michaelflisar.composepreferences.core.PreferenceDivider
import com.michaelflisar.composepreferences.core.PreferenceInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSectionHeader
import com.michaelflisar.composepreferences.core.hierarchy.PreferenceRootScope
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.extension.toChunithmCoverPath
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamUpdateCoursePayload
import com.nltv.chafenqi.networking.CFQTeamServer
import com.nltv.chafenqi.storage.songlist.chunithm.ChunithmMusicEntry
import com.nltv.chafenqi.storage.songlist.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.team.HomeTeamPageViewModel
import com.nltv.chafenqi.view.songlist.SongListSearchEmptyState
import com.nltv.chafenqi.view.songlist.chunithmDifficultyColors
import com.nltv.chafenqi.view.songlist.chunithmDifficultyTitles
import com.nltv.chafenqi.view.songlist.maimaiDifficultyColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamSettingsCoursePage(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold (
        topBar = {
            LargeTopAppBar(
                title = { Text("组曲设置") },
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        PreferenceScreen (
            modifier = Modifier.padding(paddingValues)
        ) {
            TeamCourseSettings(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceRootScope.TeamCourseSettings(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val model: HomeTeamPageViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry?.let { navController.getBackStackEntry(HomeNavItem.Home.route + "/team/settings") }
            ?: LocalViewModelStoreOwner.current!!
    )
    val courseModel: HomeTeamCourseSettingViewModel = viewModel()

    val editCourseNameUseCase = rememberUseCaseState()
    val editCourseNameInputs = listOf(
        InputTextField(
            header = InputHeader(
                title = "新组曲名称"
            ),
            key = "newCourseName"
        )
    )

    val confirmUpdateCourseUseCase = rememberUseCaseState()

    InputDialog(
        state = editCourseNameUseCase,
        selection = InputSelection(
            input = editCourseNameInputs,
            onPositiveClick = { result ->
                val newName = result.getString("newCourseName")
                if (newName != null) {
                    courseModel.courseName = newName
                }
            }
        )
    )

    InfoDialog(
        state = confirmUpdateCourseUseCase,
        header = Header.Default(
            title = "更新组曲",
            icon = IconSource(imageVector = Icons.Default.Refresh)
        ),
        body = InfoBody.Default(
            bodyText = "确定要更新组曲吗？组曲信息每7天只能修改一次，且将会清空当前组曲排行榜。",
        ),
        selection = InfoSelection(
            onPositiveClick = {
                courseModel.upload(snackbarHostState)
            }
        )
    )

    LaunchedEffect(Unit) {
        courseModel.refresh()
    }

    PreferenceSectionHeader(
        title = { Text("基本信息") }
    )

    PreferenceButton(
        title = { Text("组曲名称") },
        subtitle = { Text(if (courseModel.courseName.isEmpty()) "暂未设置" else courseModel.courseName) },
        icon = { Icon(Icons.Default.Ballot, contentDescription = "组曲名称") },
        onClick = { editCourseNameUseCase.show() }
    )

    PreferenceList(
        style = PreferenceList.Style.Spinner,
        value = courseModel.courseLife,
        onValueChange = { courseModel.courseLife = it },
        items = listOf(1, 10, 50, 100, 200, 0),
        itemTextProvider = {
            if (it == 0) "无限制" else "$it"
        },
        title = { Text("生命值") },
        subtitle = { Text(if (courseModel.courseTrack1 == null) "暂未设置" else "${courseModel.courseLife}") },
        icon = { Icon(Icons.Default.HeartBroken, contentDescription = "生命值") }
    )

    PreferenceDivider()

    PreferenceSectionHeader(
        title = { Text("歌曲配置") }
    )

    PreferenceButton(
        title = { Text("TRACK 1") },
        subtitle = {
            Text(
                buildAnnotatedString {
                    if (courseModel.courseTrack1 == null) {
                        append("暂未设定")
                    } else {
                        append(model.getTitle(courseModel.courseTrack1!!))
                        append(" ")
                        withStyle(style = SpanStyle(color = model.getDifficultyColor(courseModel.courseTrack1!!))) {
                            append(chunithmDifficultyTitles[courseModel.courseTrack1!!.levelIndex])
                        }
                    }
                    toAnnotatedString()
                }
            )
        },
        onClick = {
            courseModel.currentSelectedMusicSlot = 0
        }
    )
    PreferenceButton(
        title = { Text("TRACK 2") },
        subtitle = {
            Text(
                buildAnnotatedString {
                    if (courseModel.courseTrack2 == null) {
                        append("暂未设定")
                    } else {
                        append(model.getTitle(courseModel.courseTrack2!!))
                        append(" ")
                        withStyle(style = SpanStyle(color = model.getDifficultyColor(courseModel.courseTrack2!!))) {
                            append(chunithmDifficultyTitles[courseModel.courseTrack2!!.levelIndex])
                        }
                    }
                    toAnnotatedString()
                }
            )
        },
        onClick = {
            courseModel.currentSelectedMusicSlot = 1
        }
    )
    PreferenceButton(
        title = { Text("TRACK 3") },
        subtitle = {
            Text(
                buildAnnotatedString {
                    if (courseModel.courseTrack3 == null) {
                        append("暂未设定")
                    } else {
                        append(model.getTitle(courseModel.courseTrack3!!))
                        append(" ")
                        withStyle(style = SpanStyle(color = model.getDifficultyColor(courseModel.courseTrack3!!))) {
                            append(chunithmDifficultyTitles[courseModel.courseTrack3!!.levelIndex])
                        }
                    }
                    toAnnotatedString()
                }
            )
        },
        onClick = {
            courseModel.currentSelectedMusicSlot = 2
        }
    )

    PreferenceDivider()

    PreferenceButton(
        title = { Text("更新组曲") },
        subtitle = { Text("组曲更新后将会重置当前排行榜") },
        icon = { Icon(Icons.Default.Refresh, contentDescription = "更新组曲") },
        onClick = { confirmUpdateCourseUseCase.show() }
    )

    PreferenceInfo(
        title = { Text("组曲每7天只能修改一次") },
        icon = { Icon(Icons.Default.Info, contentDescription = "组曲相关信息") }
    )

    if (courseModel.currentSelectedMusicSlot != null) {
        HomeTeamCourseSettingMusicSelectionSheet(
            mode = model.mode,
            onDismiss = { courseModel.currentSelectedMusicSlot = null },
        ) { musicId, diff ->
            when (courseModel.currentSelectedMusicSlot!!) {
                0 -> courseModel.courseTrack1 = TeamBasicInfo.CourseTrack(musicId.toLong(), diff)
                1 -> courseModel.courseTrack2 = TeamBasicInfo.CourseTrack(musicId.toLong(), diff)
                2 -> courseModel.courseTrack3 = TeamBasicInfo.CourseTrack(musicId.toLong(), diff)
            }
            courseModel.currentSelectedMusicSlot = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamCourseSettingMusicSelectionSheet(
    mode: Int,
    onDismiss: () -> Unit,
    onSubmit: (Int, Int) -> Unit
) {
    val model: HomeTeamCourseSettingViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var searchText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val chuLazyListState = rememberLazyListState()
    val maiLazyListState = rememberLazyListState()

    val searchBarHorizontalPadding by animateDpAsState(
        targetValue = if (model.isSearchBarActive) 0.dp else 10.dp,
        label = "animated search bar horizontal padding"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchText,
                        onQueryChange = { searchText = it },
                        expanded = model.isSearchBarActive,
                        onExpandedChange = { model.isSearchBarActive = it },
                        onSearch = { model.search(mode, searchText) },
                        placeholder = { Text("搜索曲名或曲师") },
                        trailingIcon = {
                            if (model.isSearchBarActive) {
                                IconButton(onClick = {
                                    searchText = ""
                                    model.search(mode, searchText)
                                    model.isSearchBarActive = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "关闭"
                                    )
                                }
                            }
                        },
                    )
                },
                expanded = model.isSearchBarActive,
                onExpandedChange = { model.isSearchBarActive = it },
                modifier = Modifier
                    .padding(horizontal = searchBarHorizontalPadding)
                    .padding(bottom = SCREEN_PADDING)
                    .fillMaxWidth(),
            ) {
                when (mode) {
                    0 -> {
                        if (state.chuSearchResult.isNotEmpty()) {
                            LazyColumn (
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                state = chuLazyListState
                            ) {
                                items(
                                    count = state.chuSearchResult.size,
                                    key = { index ->
                                        state.chuSearchResult[index].musicId
                                    }
                                ) { index ->
                                    ChunithmMusicSearchListEntry(
                                        music = state.chuSearchResult[index]
                                    ) { diff ->
                                        onSubmit(state.chuSearchResult[index].musicId, diff)
                                    }
                                }
                            }
                        } else {
                            SongListSearchEmptyState()
                        }
                    }
                    1 -> {
                        if (state.maiSearchResult.isNotEmpty()) {
                            LazyColumn (
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                state = maiLazyListState
                            ) {
                                items(
                                    count = state.maiSearchResult.size,
                                    key = { index ->
                                        state.maiSearchResult[index].musicId
                                    }
                                ) { index ->
                                    MaimaiMusicSearchListEntry(
                                        music = state.maiSearchResult[index]
                                    ) { diff ->
                                        onSubmit(state.maiSearchResult[index].musicId, diff)
                                    }
                                }
                            }
                        } else {
                            SongListSearchEmptyState()
                        }
                    }
                }
            }
            AnimatedVisibility(!model.isSearchBarActive) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    state = listState
                ) {
                    when (mode) {
                        0 -> {
                            items(
                                count = state.chuMusic.size,
                                key = { index ->
                                    state.chuMusic[index].musicId
                                },
                                itemContent = { index ->
                                    val entry = state.chuMusic[index]
                                    ChunithmMusicSearchListEntry(entry) { diff ->
                                        onSubmit(entry.musicId, diff)
                                    }
                                }
                            )
                        }

                        1 -> {
                            items(
                                count = state.maiMusic.size,
                                key = { index ->
                                    state.maiMusic[index].musicId
                                },
                                itemContent = { index ->
                                    val entry = state.maiMusic[index]
                                    MaimaiMusicSearchListEntry(entry) { diff ->
                                        onSubmit(entry.musicId, diff)
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun ChunithmMusicSearchListEntry(
    music: ChunithmMusicEntry,
    onSelectDifficulty: (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = music.musicId.toChunithmCoverPath(),
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
                ChunithmSearchLevelBadgeRow(musicEntry = music) { diff ->
                    onSelectDifficulty(diff)
                }
            }
        }
    }
}

@Composable
fun MaimaiMusicSearchListEntry(
    music: MaimaiMusicEntry,
    onSelectDifficulty: (Int) -> Unit
) {
    ElevatedCard (
        modifier = Modifier
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = music.coverId.toMaimaiCoverPath(),
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
                MaimaiSearchLevelBadgeRow(musicEntry = music) { diff ->
                    onSelectDifficulty(diff)
                }
            }
        }
    }
}

@Composable
fun MaimaiSearchLevelBadgeRow(
    musicEntry: MaimaiMusicEntry,
    onSelectDifficulty: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        musicEntry.charts.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color = maimaiDifficultyColors[index])
                    .clickable {
                        onSelectDifficulty(index)
                    },
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
fun ChunithmSearchLevelBadgeRow(
    musicEntry: ChunithmMusicEntry,
    onSelectDifficulty: (Int) -> Unit
) {
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
                        .background(color = chunithmDifficultyColors[index])
                        .clickable {
                            onSelectDifficulty(index)
                        },
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