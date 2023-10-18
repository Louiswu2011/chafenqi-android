package com.nltv.chafenqi.view.songlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.extension.toMaimaiCoverPath
import com.nltv.chafenqi.storage.room.maimai.MaimaiMusicEntry
import com.nltv.chafenqi.view.AppViewModelProvider

@Composable
fun SongListPage(navController: NavController) {
    val model: SongListPageViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val maiSongList by model.maiSongState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(maiSongList.items) { music ->
            MaimaiMusicListEntry(music = music)
        }
    }
}

@Composable
fun MaimaiMusicListEntry(music: MaimaiMusicEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = music.id.toMaimaiCoverPath(),
            contentDescription = "${music.title}的封面",
            modifier = Modifier
                .padding(end = 8.dp)
                .aspectRatio(1f)
                .width(64.dp)
                .clip(RoundedCornerShape(size = 10.dp)),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = music.title, fontWeight = FontWeight.Bold, fontSize = TextUnit(16f, TextUnitType.Sp))
            Text(text = music.level.joinToString(" "))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaimaiMusicListEntryPreview() {
    MaimaiMusicListEntry(music = MaimaiMusicEntry(
        0,
        "2",
        "True Love Song",
        level = listOf("1", "2", "3", "4")
    ))
}