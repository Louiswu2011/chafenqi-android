package com.nltv.chafenqi.view.home

import androidx.compose.animation.Crossfade
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.R
import com.nltv.chafenqi.extension.nameplateChunithmBottomColor
import com.nltv.chafenqi.extension.nameplateChunithmTopColor
import com.nltv.chafenqi.extension.nameplateMaimaiBottomColor
import com.nltv.chafenqi.extension.nameplateMaimaiTopColor
import com.nltv.chafenqi.extension.nameplateThemedChuniColors
import com.nltv.chafenqi.extension.nameplateThemedMaiColors
import me.zhanghai.compose.preference.LocalPreferenceFlow



val nameplateThemedChunithmAvatarResource = R.drawable.nameplate_penguin
val nameplateThemedMaimaiAvatarResource = R.drawable.nameplate_otohime

@Composable
fun HomePageNameplateSection(navController: NavController) {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()

    Box {
        Crossfade(targetState = uiState.mode, label = "home nameplate crossfade") {
            when (it) {
                0 -> {
                    HomePageChunithmNameplate(navController)
                }

                1 -> {
                    HomePageMaimaiNameplate(navController)
                }
            }
        }
    }
}

@Composable
fun HomePageMaimaiNameplate(navController: NavController) {
    val settings by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()
    var showEmptyDataAlert by remember {
        mutableStateOf(false)
    }

    val brush = Brush.verticalGradient(listOf(nameplateMaimaiTopColor, nameplateMaimaiBottomColor))
    val themedBrush = Brush.linearGradient(colors = nameplateThemedMaiColors, start = Offset.Zero, end = Offset.Infinite)

    if (showEmptyDataAlert) {
        EmptyDataAlert(onDismissRequest = { showEmptyDataAlert = false }) {
            showEmptyDataAlert = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = ShapeDefaults.Medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier.background(if (settings.get<Boolean>("homeUseThemedColor") == true) themedBrush else brush)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = if (settings.get<Boolean>("homeUseThemedColor") == true) nameplateThemedMaimaiAvatarResource else R.drawable.nameplate_salt),
                    contentDescription = "名牌纱露朵形象",
                    Modifier.size(128.dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(end = 12.dp, top = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        if (!model.user.isPremium) {
                            navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem")
                            return@TextButton
                        }
                        if (uiState.canOpenMaimaiInfo) {
                            navController.navigate(HomeNavItem.Home.route + "/info")
                        } else {
                            showEmptyDataAlert = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "user info Icon",
                        Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "玩家信息")
                }
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = uiState.nickname,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.Black
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
fun HomePageChunithmNameplate(navController: NavController) {
    val settings by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()
    var showEmptyDataAlert by remember {
        mutableStateOf(false)
    }

    val brush =
        Brush.verticalGradient(listOf(nameplateChunithmTopColor, nameplateChunithmBottomColor))
    val themedBrush = Brush.linearGradient(colors = nameplateThemedChuniColors, start = Offset.Zero, end = Offset.Infinite)

    if (showEmptyDataAlert) {
        EmptyDataAlert(onDismissRequest = { showEmptyDataAlert = false }) {
            showEmptyDataAlert = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = ShapeDefaults.Medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier.background(if (settings.get<Boolean>("homeUseThemedColor") == true) themedBrush else brush)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = if (settings.get<Boolean>("homeUseThemedColor") == true) nameplateThemedChunithmAvatarResource else R.drawable.nameplate_penguin),
                    contentDescription = "名牌中二企鹅形象",
                    Modifier.size(128.dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(end = 12.dp, top = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        if (!model.user.isPremium) {
                            navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem")
                            return@TextButton
                        }
                        if (uiState.canOpenChunithmInfo) {
                            navController.navigate(HomeNavItem.Home.route + "/info")
                        } else {
                            showEmptyDataAlert = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "user info Icon",
                        Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "玩家信息")
                }
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = uiState.nickname,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = Color.Black
                )
                HomePageNameplateInfoRow(
                    title = "Rating",
                    content = uiState.rating
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomePageNameplateInfoRow(title = "B", content = uiState.chuBestRating)
                    HomePageNameplateInfoRow(title = "N", content = uiState.chuNewRating)
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
        Text(text = title, modifier = Modifier.padding(end = 8.dp), color = Color.Black)
        Text(text = content, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}