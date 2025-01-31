package com.nltv.chafenqi.view.home.team

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.extension.toHalfWidth
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTeamPageInfoSection(
    snackbarHostState: SnackbarHostState
) {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val leader by remember {
        mutableStateOf(state.team.info.leaderUserId.let { id -> state.team.members.firstOrNull { it.userId == id } })
    }

    val infoScrollState = rememberScrollState()

    @Composable
    fun RowScope.InfoCard(
        title: String,
        icon: ImageVector,
        value: String,
        onLongClick: () -> Unit = {}
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }

        Card (
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = ShapeDefaults.Small,
            modifier = Modifier.weight(1f)
                .combinedClickable(
                    onClick = { expanded =!expanded },
                    onLongClick = { onLongClick() }
                )
        ) {
            Column (
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(icon, contentDescription = title)
                    Text(text = value, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }

                AnimatedVisibility(expanded) {
                    Text(text = title, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = state.team.info.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        HorizontalDivider()

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoCard(title = "团队代码", icon = Icons.Default.Search, value = if (state.team.info.promotable) state.team.info.teamCode else "已隐藏", onLongClick = {
                if (state.team.info.promotable) {
                    scope.launch {
                        clipboardManager.setText(AnnotatedString(text = state.team.info.teamCode))
                        snackbarHostState.showSnackbar(
                            message = "已复制到剪贴板",
                        )
                    }
                }
            })
            InfoCard(title = "活动天数", icon = Icons.Default.Timelapse, value = state.team.info.activityDays.toString() + "天"  )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoCard(title = "本月积分", icon = Icons.Default.Leaderboard, value = "${state.team.info.currentActivityPoints}P")
            InfoCard(title = "队长", icon = Icons.Default.ManageAccounts, value = leader?.nickname?.toHalfWidth() ?: "")
            InfoCard(title = "团队人数", icon = Icons.Default.People, value = state.team.members.size.toString() + "人")
        }

        OutlinedTextField(
            value = state.team.info.remarks,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            label = { Text(text = "团队介绍") },
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = MaterialTheme.shapes.small,
        )

        HorizontalDivider()
    }
}