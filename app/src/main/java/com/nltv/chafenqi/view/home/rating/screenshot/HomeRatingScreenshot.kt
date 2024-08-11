package com.nltv.chafenqi.view.home.rating.screenshot

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.storage.datastore.user.chunithm.ChunithmRatingEntry
import com.nltv.chafenqi.storage.datastore.user.maimai.MaimaiBestScoreEntry
import com.nltv.chafenqi.view.home.rating.HomeRatingChunithmEntry
import com.nltv.chafenqi.view.home.rating.HomeRatingMaimaiEntry

data class HomeRatingScreenshotMaimaiData(
    val pastList: List<MaimaiBestScoreEntry>,
    val newList: List<MaimaiBestScoreEntry>,
    val rating: String,
    val pastRating: String,
    val newRating: String,
    val navController: NavController
)

data class HomeRatingScreenshotChunithmData(
    val bestList: List<ChunithmRatingEntry>,
    val recentList: List<ChunithmRatingEntry>,
    val rating: String,
    val bestRating: String,
    val recentRating: String,
    val navController: NavController
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRatingScreenshotButton(
    activity: Activity,
    mode: Int,
    maiData: HomeRatingScreenshotMaimaiData,
    chuData: HomeRatingScreenshotChunithmData
) {
    val model = viewModel<HomeRatingScreenshotViewModel>()
    val context = LocalContext.current
    var screenshot by remember { mutableStateOf<ImageBitmap?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Rating列表") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (mode == 0) {
                HomeRatingScreenshotChunithmList(chuData = chuData)
            } else if (mode == 1) {
                HomeRatingScreenshotMaimaiList(maiData = maiData)
            }
        }
    }
}

@Composable
fun HomeRatingScreenshotMaimaiList(maiData: HomeRatingScreenshotMaimaiData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = maiData.rating, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(text = "Past ${maiData.pastRating} / New ${maiData.newRating}")
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "旧曲 B35", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        maiData.pastList.indices.forEach { index ->
            HomeRatingMaimaiEntry(
                entry = maiData.pastList[index],
                index = index,
                navController = maiData.navController
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "新曲 B15", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        maiData.newList.indices.forEach { index ->
            HomeRatingMaimaiEntry(
                entry = maiData.newList[index],
                index = index,
                navController = maiData.navController
            )
        }
    }
}

@Composable
fun HomeRatingScreenshotChunithmList(chuData: HomeRatingScreenshotChunithmData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = chuData.rating, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(text = "Best ${chuData.bestRating} / Recent ${chuData.recentRating}")
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "最佳成绩 B30", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        chuData.bestList.indices.forEach { index ->
            HomeRatingChunithmEntry(
                entry = chuData.bestList[index],
                index = index,
                navController = chuData.navController
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "最近成绩 R10", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        chuData.recentList.indices.forEach { index ->
            HomeRatingChunithmEntry(
                entry = chuData.recentList[index],
                index = index,
                navController = chuData.navController
            )
        }
    }
}

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}