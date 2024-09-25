package com.nltv.chafenqi.view.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.R
import com.nltv.chafenqi.storage.SettingsStore

val nameplateChunithmTopColor = Color(red = 254, green = 241, blue = 65)
val nameplateChunithmBottomColor = Color(red = 243, green = 200, blue = 48)

val nameplateMaimaiTopColor = Color(red = 167, green = 243, blue = 254)
val nameplateMaimaiBottomColor = Color(red = 93, green = 166, blue = 247)

val nameplateThemedChuniColors = listOf(
    Color(red = 192, green = 230, blue = 249),
    Color(red = 219, green = 226, blue = 250),
    Color(red = 240, green = 223, blue = 246),
    Color(red = 248, green = 211, blue = 238),
    Color(red = 245, green = 178, blue = 225)
)

val nameplateThemedMaiColors = listOf(
    Color(red = 235, green = 182, blue = 85),
    Color(red = 235, green = 187, blue = 87),
    Color(red = 236, green = 196, blue = 90),
    Color(red = 235, green = 200, blue = 89),
    Color(red = 242, green = 225, blue = 68)
)

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
    val store = SettingsStore(LocalContext.current)
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()
    var showEmptyDataAlert by remember {
        mutableStateOf(false)
    }
    val homeUseThemedColor by store.homeUseThemedColor.collectAsStateWithLifecycle(initialValue = true)

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
            modifier = Modifier.background(if (homeUseThemedColor) themedBrush else brush)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = if (homeUseThemedColor) nameplateThemedMaimaiAvatarResource else R.drawable.nameplate_salt),
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
                Button(
                    onClick = {
                        if (!model.user.isPremium) {
                            navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem")
                            return@Button
                        }
                        if (uiState.canOpenMaimaiInfo) {
                            navController.navigate(HomeNavItem.Home.route + "/info")
                        } else {
                            showEmptyDataAlert = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
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
    val store = SettingsStore(LocalContext.current)
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()
    var showEmptyDataAlert by remember {
        mutableStateOf(false)
    }
    val homeUseThemedColor by store.homeUseThemedColor.collectAsStateWithLifecycle(initialValue = true)

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
            modifier = Modifier.background(if (homeUseThemedColor) themedBrush else brush)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = if (homeUseThemedColor) nameplateThemedChunithmAvatarResource else R.drawable.nameplate_penguin),
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
                Button(
                    onClick = {
                        if (!model.user.isPremium) {
                            navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem")
                            return@Button
                        }

                        if (uiState.canOpenChunithmInfo) {
                            navController.navigate(HomeNavItem.Home.route + "/info")
                        } else {
                            showEmptyDataAlert = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
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
                    content = uiState.rating + " (${uiState.chuMaxRating})"
                )
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
        Text(text = title, modifier = Modifier.padding(end = 8.dp), color = Color.Black)
        Text(text = content, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}