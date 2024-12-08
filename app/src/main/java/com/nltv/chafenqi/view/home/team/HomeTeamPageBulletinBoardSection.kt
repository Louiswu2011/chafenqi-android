package com.nltv.chafenqi.view.home.team

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.model.team.TeamBulletinBoardEntry

@Composable
fun HomeTeamPageBulletinBoardSection() {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            count = state.team.bulletinBoard.size,
            key = { index -> state.team.bulletinBoard[index].id },
        ) { index ->
            HomeTeamPageBulletinBoardEntry(entry = state.team.bulletinBoard[index])
        }
    }
}

@Composable
fun HomeTeamPageBulletinBoardEntry(
    entry: TeamBulletinBoardEntry
) {
    val context = LocalContext.current
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    var member by remember { mutableStateOf(state.team.members.firstOrNull { it.userId == entry.userId }) }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        )
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
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
                    Text(text = entry.content, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}