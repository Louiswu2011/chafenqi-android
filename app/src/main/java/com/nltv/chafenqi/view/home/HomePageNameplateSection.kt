package com.nltv.chafenqi.view.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.R
import com.nltv.chafenqi.view.AppViewModelProvider

val nameplateChunithmTopColor = Color(red = 254, green = 241, blue = 65)
val nameplateChunithmBottomColor = Color(red = 243, green = 200, blue = 48)

val nameplateMaimaiTopColor = Color(red = 167, green = 243, blue = 254)
val nameplateMaimaiBottomColor = Color(red = 93, green = 166, blue = 247)

@Composable
fun HomePageNameplate() {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by model.uiState.collectAsState()

    Box {
        Crossfade(targetState = uiState.mode, label = "home nameplate crossfade") {
            when (it) {
                0 -> {
                    HomePageChunithmNameplate()
                }
                1 -> {
                    HomePageMaimaiNameplate()
                }
            }
        }
    }
}

@Composable
fun HomePageMaimaiNameplate() {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by model.uiState.collectAsState()

    val brush = Brush.verticalGradient(listOf(nameplateMaimaiTopColor, nameplateMaimaiBottomColor))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = ShapeDefaults.Medium,
        elevation = CardDefaults.cardElevation()
    ) {
        Box (
            modifier = Modifier.background(brush)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nameplate_salt),
                    contentDescription = "名牌纱露朵形象",
                    Modifier.size(128.dp)
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = uiState.nickname,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                HomePageNameplateInfoRow(title = "Rating", content = uiState.rating)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomePageNameplateInfoRow(title = "P", content = uiState.maiPastRating)
                    HomePageNameplateInfoRow(title = "N", content = uiState.maiNewRating)
                }
                HomePageNameplateInfoRow(title = "游玩次数", content = uiState.playCount)
                Spacer(modifier = Modifier.size(8.dp))
                HomePageNameplateInfoRow(title = "更新于", content = uiState.nameplateUpdateTime)
            }
        }
    }
}

@Composable
fun HomePageChunithmNameplate() {
    val model: HomePageViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by model.uiState.collectAsState()

    val brush = Brush.verticalGradient(listOf(nameplateChunithmTopColor, nameplateChunithmBottomColor))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = ShapeDefaults.Medium,
        elevation = CardDefaults.cardElevation()
    ) {
        Box (
            modifier = Modifier.background(brush)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nameplate_penguin),
                    contentDescription = "名牌中二企鹅形象",
                    Modifier.size(128.dp)
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = uiState.nickname,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                HomePageNameplateInfoRow(title = "Rating", content = uiState.rating + " (${uiState.chuMaxRating})")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomePageNameplateInfoRow(title = "B", content = uiState.chuBestRating)
                    HomePageNameplateInfoRow(title = "R", content = uiState.chuRecentRating)
                }
                HomePageNameplateInfoRow(title = "游玩次数", content = uiState.playCount)
                Spacer(modifier = Modifier.size(8.dp))
                HomePageNameplateInfoRow(title = "更新于", content = uiState.nameplateUpdateTime)
            }
        }
    }
}

@Composable
fun HomePageNameplateInfoRow(title: String, content: String) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = title, modifier = Modifier.padding(end = 8.dp))
        Text(text = content, fontWeight = FontWeight.Bold)
    }
}