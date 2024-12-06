package com.nltv.chafenqi.view.home.team

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nltv.chafenqi.model.team.TeamBasicInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeTeamPageCourseSection() {
    val model: HomeTeamPageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card (
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                shape = ShapeDefaults.Medium,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HomeTeamCourseItem(1, state.team.info.courseTrack1)
                    HomeTeamCourseItem(2, state.team.info.courseTrack2)
                    HomeTeamCourseItem(3, state.team.info.courseTrack3)
                }
            }
        }

        item {
            HorizontalDivider()
        }
    }

}

@Composable
fun HomeTeamCourseItem(
    index: Int,
    course: TeamBasicInfo.CourseTrack,
) {
    val model: HomeTeamPageViewModel = viewModel()

    var coverPath by rememberSaveable { mutableStateOf(model.getCoverPath(course)) }
    var diffColor by rememberSaveable { mutableLongStateOf(model.getDifficultyColor(course)) }
    var title by rememberSaveable { mutableStateOf(model.getTitle(course)) }
    var artist by rememberSaveable { mutableStateOf(model.getArtist(course)) }
    var difficultyString by rememberSaveable { mutableStateOf(model.getDifficultyString(course).uppercase()) }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = coverPath,
            contentDescription = "歌曲曲绘",
            modifier = Modifier
                .size(72.dp)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color(value = diffColor.toULong())
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
        )

        Column (
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("TRACK $index")
                Text(difficultyString, color = Color(value = diffColor.toULong()))
            }

            Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium)
            Text(artist, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleSmall)
        }
    }
}