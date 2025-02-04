package com.nltv.chafenqi.view.home.team

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.nltv.chafenqi.extension.toDateString
import com.nltv.chafenqi.model.team.TeamBasicInfo
import com.nltv.chafenqi.model.team.TeamCourseRecord

@Composable
fun HomeTeamPageCourseSection() {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(true) }

    if (
        state.team.info.courseTrack1 == null &&
        state.team.info.courseTrack2 == null &&
        state.team.info.courseTrack3 == null
        ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "队长暂未设定组曲", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = if (expanded) "收起" else "展开",
                                modifier = Modifier.clickable { expanded = !expanded },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.team.info.courseName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Text(
                        text = "组曲生命值：${if (state.team.info.courseHealth > 0) state.team.info.courseHealth else "无限制"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    AnimatedVisibility(expanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HomeTeamCourseItem(1, state.team.info.courseTrack1!!)
                            HomeTeamCourseItem(2, state.team.info.courseTrack2!!)
                            HomeTeamCourseItem(3, state.team.info.courseTrack3!!)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("游玩人数：")
                                    Text(
                                        "${state.team.courseRecords.size}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("通过人数：")
                                    Text(
                                        "${state.team.courseRecords.filter { it.cleared }.size}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }
                        }
                    }

                    HorizontalDivider()
                }
            }

            items(
                count = state.team.courseRecords.sortedByDescending { it.rawScore }.size,
                key = { index -> state.team.courseRecords.sortedByDescending { it.rawScore }[index].id },
            ) { index ->
                val entry = state.team.courseRecords.sortedByDescending { it.rawScore }[index]
                HomeTeamCourseRecordCard(
                    entry = entry,
                    rank = index + 1,
                )
            }
        }
    }

}

@Composable
fun HomeTeamCourseRecordCard(
    entry: TeamCourseRecord,
    rank: Int,
) {
    val context = LocalContext.current
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()
    var member by remember { mutableStateOf(state.team.members.firstOrNull { it.userId == entry.userId }) }

    var showDetail by remember { mutableStateOf(false) }

    @Composable
    fun DetailScoreColumn(
        trackNumber: Int,
        displayScore: String
    ) {
        Column (
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TRACK $trackNumber", style = MaterialTheme.typography.bodySmall)
            Text(displayScore, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
    }

    Card (
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        ),
        onClick = { showDetail = !showDetail },
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = member?.avatar ?: "",
                contentDescription = "成员头像",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(size = 12.dp))
            )

            AnimatedContent(showDetail, label = "Detailed score") {
                when (it) {
                    true -> {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(start = 10.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column (
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        member?.nickname ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        entry.timestamp.toDateString(context),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                HorizontalDivider()
                            }
                            Row (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DetailScoreColumn(1, entry.trackRecords[0].score)
                                DetailScoreColumn(2, entry.trackRecords[1].score)
                                DetailScoreColumn(3, entry.trackRecords[2].score)
                            }
                        }
                    }
                    false -> {
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(start = 10.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column (
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        member?.nickname ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        entry.timestamp.toDateString(context),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                HorizontalDivider()
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("#${rank}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    entry.totalScore(model.mode),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun HomeTeamCourseItem(
    index: Int,
    course: TeamBasicInfo.CourseTrack,
) {
    val model: HomeTeamPageViewModel = viewModel()

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .height(64.dp)
    ) {
        AsyncImage(
            model = model.getCoverPath(course),
            contentDescription = "歌曲曲绘",
            modifier = Modifier
                .size(64.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = model.getDifficultyColor(course)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(size = 10.dp))
        )

        Column (
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxHeight()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("TRACK $index", style = MaterialTheme.typography.bodyMedium)
                Text(model.getDifficultyString(course).uppercase(), color = model.getDifficultyColor(course), style = MaterialTheme.typography.bodyMedium)
            }

            Text(model.getTitle(course), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
            Text(model.getArtist(course), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
        }
    }
}