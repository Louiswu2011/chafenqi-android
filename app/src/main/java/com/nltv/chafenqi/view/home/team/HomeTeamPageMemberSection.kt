package com.nltv.chafenqi.view.home.team

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.model.team.TeamMember
import com.nltv.chafenqi.model.team.TeamPendingMember

@Composable
fun HomeTeamPageMemberList() {
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
            count = state.team.members.size,
            key = { index -> index }
        ) {
            HomeTeamPageMemberEntry(
                member = state.team.members[it],
                onLongClick = {
                    if (state.team.info.leaderUserId == model.userId) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectedTeamMemberUserId = state.team.members[it].userId
                    }
                }
            )
        }
        // TODO: Add pending member section if current user is team leader
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

@Composable
fun HomeTeamPagePendingMemberEntry(
    pendingMember: TeamPendingMember
) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTeamPageMemberEntry(
    member: TeamMember,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val avatarHeight by animateDpAsState(
        targetValue = if (expanded) 72.dp else 64.dp,
        animationSpec = tween(durationMillis = 300),
        label = "avatar height"
    )

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
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    expanded = !expanded
                },
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(avatarHeight)
            ) {
                AsyncImage(
                    model = member.avatar,
                    contentDescription = "头像",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(avatarHeight)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.FillHeight,
                    clipToBounds = true
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = if (expanded) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    AnimatedVisibility(expanded) {
                        Text(text = member.trophy, style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    }
                    Text(text = member.nickname, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    AnimatedVisibility(expanded) {
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
                }
            }

            AnimatedVisibility(expanded) {
                Column (
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    MemberInfoRow(description = "加入时间：", value = member.joinAt.toDateString(context))
                    MemberInfoRow(description = "贡献点数：", value = "${member.activityPoints}P")
                    MemberInfoRow(description = "最后游玩时间：", value = member.lastActivityAt.toDateString(context))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTeamPageMemberManageSheet(
    member: TeamMember,
    onDismissRequest: () -> Unit
) {
    var shouldShowKickConfirmDialog by remember {
        mutableStateOf(false)
    }

    var shouldShowTransferConfirmDialog by remember {
        mutableStateOf(false)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = null
    ) {
        Column (
            modifier = Modifier.navigationBarsPadding()
        ) {
            ListItem(
                headlineContent = { Text(text = "移除该成员") },
                leadingContent = { Icon(Icons.Default.GroupRemove, contentDescription = "移除成员") },
                modifier = Modifier.clickable(
                    onClick = {
                        shouldShowKickConfirmDialog = true
                    }
                )
            )
            ListItem(
                headlineContent = { Text(text = "转让队长") },
                leadingContent = { Icon(Icons.Default.Groups, contentDescription = "转让队长") },
                modifier = Modifier.clickable(
                    onClick = {
                        shouldShowTransferConfirmDialog = true
                    }
                )
            )
        }
    }

    if (shouldShowKickConfirmDialog) {
        HomeTeamPageConfirmDialog(
            icon = Icons.Outlined.Warning,
            title = "确认移除",
            message = "您确定要将此成员从您的团队中移除吗？该操作无法撤销。",
            onConfirm = { shouldShowKickConfirmDialog = false },
            onDismissRequest = { shouldShowKickConfirmDialog = false }
        )
    }

    if (shouldShowTransferConfirmDialog) {
        HomeTeamPageConfirmDialog(
            icon = Icons.Outlined.Warning,
            title = "确认转让",
            message = "您确定将团队队长转让给该成员吗？该操作无法撤销。",
            onConfirm = { shouldShowTransferConfirmDialog = false },
            onDismissRequest = { shouldShowTransferConfirmDialog = false }
        )
    }
}