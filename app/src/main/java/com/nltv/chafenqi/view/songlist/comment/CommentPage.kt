package com.nltv.chafenqi.view.songlist.comment

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.data.Comment
import com.nltv.chafenqi.view.songlist.SongDetailViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CommentPage(
    mode: Int,
    index: Int,
    navController: NavController
) {
    val model: CommentPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.loading,
        onRefresh = {
            model.update(mode, index)
        }
    )

    LaunchedEffect(Unit) {
        model.update(mode, index)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "评论") },
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
                actions = {
                    IconButton(onClick = { model.showCommentSheet = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Post Comment")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Box (
            modifier = Modifier.pullRefresh(pullRefreshState)
        ) {
            if (model.showCommentSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        model.showCommentSheet = false
                        model.replyComment = null
                    },
                    sheetState = sheetState
                ) {
                    CommentComposeSheet()
                }
            }
            AnimatedContent(targetState = state.loading, label = "Comment Loading") {
                when (it) {
                    true -> {
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    false -> {
                        if (state.comments.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "暂无评论，点击右上角加号发布第一条评论吧")
                            }
                        } else {
                            LazyColumn(
                                contentPadding = paddingValues,
                                state = lazyListState,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                items(
                                    count = state.comments.size,
                                    key = { index -> state.comments[index].id },
                                    itemContent = { index ->
                                        CommentColumn(comment = state.comments[index])
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PullRefreshIndicator(refreshing = state.loading, state = pullRefreshState)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentColumn(comment: Comment) {
    val model: CommentPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    val replyPrompt = if (comment.replyId != -1) {
        val username = state.comments.find { it.id == comment.replyId }?.username ?: ""
        if (username.isEmpty()) "" else "回复${username}："
    } else ""
    var showMenu by remember {
        mutableStateOf(false)
    }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .combinedClickable {
                showMenu = true
            }
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = comment.username, fontWeight = FontWeight.Bold)
                Text(text = comment.dateString, style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "$replyPrompt${comment.content}")
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text(text = "回复") },
                onClick = {
                    model.replyComment = comment
                    model.showCommentSheet = true
                    showMenu = false
                },
                leadingIcon = {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Reply, contentDescription = "Reply")
                }
            )
            if (model.user.username == comment.username) {
                DropdownMenuItem(
                    text = { Text(text = "删除") },
                    onClick = {
                        model.deleteComment(comment.id)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                )
            }
        }
    }
}

@Composable
fun CommentComposeSheet() {
    val model: CommentPageViewModel = viewModel()
    var commentContent by remember {
        mutableStateOf("")
    }

    fun reset() {
        model.showCommentSheet = false
        model.replyComment = null
        commentContent = ""
    }

    Column (
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TextField(
            value = commentContent,
            onValueChange = { commentContent = it },
            supportingText = {
                Text(text = "将以${model.user.username}的身份发布，请文明发言")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            placeholder = {
                if (model.replyComment != null) {
                    Text(text = "回复${model.replyComment?.username}：")
                } else {
                    Text(text = "在这里输入你的评论...")
                }
            }
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = {
                    reset()
                }) {
                    Text(text = "取消")
                }
                Button(onClick = {
                    model.submitComment(replyId = model.replyComment?.replyId ?: -1, content = commentContent)
                    reset()
                }) {
                    Text(text = "提交")
                }
            }
        }
    }
}