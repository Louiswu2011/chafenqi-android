package com.nltv.chafenqi.view.home.team

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.nltv.chafenqi.extension.TEAM_BULLETIN_MESSAGE_LENGTH
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.model.team.TeamBulletinBoardEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTeamPageBulletinBoardSection(snackbarHostState: SnackbarHostState) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    var selectedEntryId by remember { mutableStateOf<Int?>(null) }

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        if (state.team.info.pinnedMessageId != null) {
            if (state.team.info.pinnedMessageId!! >= 0) {
                item {
                    HomeTeamPageBulletinBoardEntry(entry = state.team.bulletinBoard.firstOrNull { it.id == state.team.info.pinnedMessageId }
                        ?: return@item, modifier = Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = {
                            selectedEntryId = state.team.bulletinBoard.first { it.id == state.team.info.pinnedMessageId }.id
                        }
                    ))
                }
                item {
                    HorizontalDivider()
                }
            }
        }
        items(
            count = state.team.bulletinBoard.filterNot { it.id == state.team.info.pinnedMessageId }.sortedByDescending { it.timestamp }.size,
            key = { index -> state.team.bulletinBoard.filterNot { it.id == state.team.info.pinnedMessageId }.sortedByDescending { it.timestamp }[index].id },
        ) { index ->
            HomeTeamPageBulletinBoardEntry(
                entry = state.team.bulletinBoard.filterNot { it.id == state.team.info.pinnedMessageId }.sortedByDescending { it.timestamp }[index],
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        selectedEntryId = state.team.bulletinBoard.filterNot { it.id == state.team.info.pinnedMessageId }.sortedByDescending { it.timestamp }[index].id
                    }
                )
            )
        }
    }

    if (selectedEntryId != null) {
        HomeTeamPageBulletinBoardActionSheet(state.team.bulletinBoard.first { it.id == selectedEntryId }, model.userId, state.isTeamAdmin, selectedEntryId!! == state.team.info.pinnedMessageId, snackbarHostState) { selectedEntryId = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeTeamPageBulletinBoardActionSheet(
    selectedEntry: TeamBulletinBoardEntry,
    userId: Long,
    isLeader: Boolean,
    isPinned: Boolean,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit
) {
    val model: HomeTeamPageViewModel = viewModel()

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        ListItem(
            icon = {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = "删除",
                    modifier = Modifier.padding(end = 10.dp),
                    tint = if (selectedEntry.userId == userId) Color.Red else Color.Gray
                )
            },
            modifier = Modifier.clickable(enabled = selectedEntry.userId == userId) {
                model.deleteBulletinBoardEntry(selectedEntry.id, snackbarHostState)
                onDismiss()
            }
        ) {
            Text(text = "删除", color = if (selectedEntry.userId == userId) Color.Black else Color.Gray)
        }
        if (isLeader) {
            ListItem(
                icon = {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = "置顶",
                        modifier = Modifier.padding(end = 10.dp)
                    )
                },
                modifier = Modifier.clickable {
                    if (isPinned) {
                        model.unpinBulletinBoardEntry(snackbarHostState)
                    } else {
                        model.pinBulletinBoardEntry(selectedEntry.id, snackbarHostState)
                    }
                    onDismiss()
                }
            ) {
                Text(text = if (isPinned) "取消置顶" else "置顶")
            }
        }
    }
}

@Composable
fun HomeTeamPageBulletinBoardEntry(
    entry: TeamBulletinBoardEntry,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    var member by remember { mutableStateOf(state.team.members.firstOrNull { it.userId == entry.userId }) }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(vertical = 8.dp)
            .then(modifier),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        )
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = member?.avatar ?: "",
                contentDescription = "成员头像",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(size = 12.dp))
            )

            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = member?.nickname?: "", style = MaterialTheme.typography.bodyMedium)
                    Text(text = entry.timestamp.toDateString(context), style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider()
                Column (
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(text = entry.content, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun BulletinComposeSheet(
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    onSubmit: suspend (String) -> String
) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    var member by remember { mutableStateOf(state.team.members.firstOrNull { it.userId == model.userId }) }
    val scope = rememberCoroutineScope()

    var bulletinContent by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TextField(
            value = bulletinContent,
            onValueChange = { bulletinContent = it },
            supportingText = {
                Text(
                    text = "${bulletinContent.length} / $TEAM_BULLETIN_MESSAGE_LENGTH",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    color = if (bulletinContent.length > TEAM_BULLETIN_MESSAGE_LENGTH) MaterialTheme.colorScheme.error else Color.Unspecified
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            placeholder = {
                Text(text = "在这里输入你的留言...")
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = {
                    bulletinContent = ""
                    onDismiss()
                }) {
                    Text(text = "取消")
                }
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        val result = onSubmit(bulletinContent)

                        if (result.isNotEmpty()) {
                            snackbarHostState.showSnackbar(
                                message = "发布留言失败：$result"
                            )
                        } else {
                            bulletinContent = ""
                            model.updateBulletinBoard()
                            onDismiss()
                        }
                    }
                },
                    enabled = bulletinContent.isNotBlank() && bulletinContent.length <= TEAM_BULLETIN_MESSAGE_LENGTH
                ) {
                    Text(text = "提交")
                }
            }
        }
    }
}