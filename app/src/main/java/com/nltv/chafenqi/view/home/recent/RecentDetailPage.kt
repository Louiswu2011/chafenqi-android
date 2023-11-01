package com.nltv.chafenqi.view.home.recent

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.view.home.HomeNavItem

@Composable
fun RecentDetailPage(
    mode: Int,
    index: Int,
    navHostController: NavHostController
) {
    val model: RecentDetailPageViewModel = viewModel<RecentDetailPageViewModel>().also {
        Log.i("HomeRecentDetailPage", "Showing detail page for mode $mode index $index")
        it.update(mode, index)
    }

    Scaffold { paddingValues ->
        Column (
            Modifier
                .padding(paddingValues)
                .padding(SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AsyncImage(
                    model = model.coverUrl,
                    contentDescription = "歌曲封面",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column (
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = model.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                    Text(
                        text = model.artist,
                        fontSize = 18.sp
                    )
                }
            }
            Card (
                Modifier.fillMaxWidth(),
                RoundedCornerShape(6.dp),
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    Text(text = model.playDateString)
                }
            }
            Row (
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = model.score,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            if (mode == 0) {
                RecentDetailChunithmScoreGrid()

            } else {
                RecentDetailMaimaiScoreGrid()
                if (model.maiHasSync) {
                    RecentDetailMaimaiSyncCard()
                }
            }

            TextButton(onClick = { navHostController.navigate(HomeNavItem.SongList.route + "/maimai/${model.maiMusicEntryIndex}") }) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "跳转到歌曲详情按钮图标", Modifier.size(ButtonDefaults.IconSize))
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "跳转到歌曲详情")
                }
            }
        }
    }
}

@Composable
fun RecentDetailMaimaiScoreGrid() {
    val model: RecentDetailPageViewModel = viewModel()

    Card (
        Modifier.fillMaxWidth()
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row (
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column (
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.height(IntrinsicSize.Max)
                ) {
                    Text(text = "Tap", fontWeight = FontWeight.Bold)
                    Text(text = "Hold", fontWeight = FontWeight.Bold)
                    Text(text = "Slide", fontWeight = FontWeight.Bold)
                    Text(text = "Touch", fontWeight = FontWeight.Bold)
                    Text(text = "Break", fontWeight = FontWeight.Bold)
                }

                repeat(5) { noteType ->
                    Column (
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(text = model.maiJudgeTexts[noteType], fontWeight = FontWeight.Bold)
                        repeat(5) { judgeType ->
                            Text(text = model.maiJudges[judgeType][noteType].ifEmpty { "-" })
                        }
                    }
                }
            }
            
            Text(text = "Combo ${model.maiCombo}")
        }
    }
}

@Composable
fun RecentDetailMaimaiSyncCard() {
    val model: RecentDetailPageViewModel = viewModel()

    Card (
        Modifier.fillMaxWidth()
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(3) { playerIndex ->
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Player ${playerIndex + 2}")
                        Text(text = model.maiMatchingPlayers[playerIndex])
                    }
                }
            }

            Text(text = "Sync ${model.maiSync}")
        }
    }
}

@Composable
fun RecentDetailChunithmScoreGrid() {
    val model: RecentDetailPageViewModel = viewModel()
}