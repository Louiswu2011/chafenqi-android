package com.nltv.chafenqi.view.home.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.extension.toHalfWidth
import com.nltv.chafenqi.model.team.TeamActivity

@Composable
fun HomeTeamPageActivitySection() {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(
            count = state.team.activities.sortedByDescending { it.timestamp }.size,
            key = { index -> state.team.activities.sortedByDescending { it.timestamp }[index].id },
        ) { index ->
            HomeTeamPageActivityCard(activity = state.team.activities.sortedByDescending { it.timestamp }[index])
        }
    }
}

@Composable
fun HomeTeamPageActivityCard(activity: TeamActivity) {
    val context = LocalContext.current
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    var member by remember { mutableStateOf(state.team.members.firstOrNull { it.userId == activity.userId }) }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = member?.avatar ?: "",
                contentDescription = "成员头像",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(size = 12.dp))
            )

            Column {
                Text(
                    buildAnnotatedString {
                        val style = SpanStyle(
                            fontWeight = FontWeight.Bold
                        )

                        withStyle(style) {
                            append(member?.nickname?.toHalfWidth() ?: "")
                            append(" ")
                        }

                        append(activity.activity)
                    },
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = activity.timestamp.toDateString(context),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}