package com.nltv.chafenqi.view.info.maimai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.model.user.maimai.UserMaimaiCharacterEntry
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem

@Composable
fun InfoMaimaiPage(navController: NavController) {
    val isEmpty = CFQUser.maimai.isExtraEmpty
    val scrollState = rememberScrollState()

    if (!isEmpty) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            InfoMaimaiCollectionArea()
            InfoMaimaiTeamArea()
            InfoMaimaiDetailButtons(navController)
        }
    }
}

@Composable
fun InfoMaimaiCollectionArea() {
    val model: InfoMaimaiPageViewModel = viewModel()

    Box(
        Modifier.padding(SCREEN_PADDING)
    ) {
        AsyncImage(
            model = model.currentNameplate.url,
            contentDescription = "当前名牌",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            AsyncImage(
                model = model.currentLeader.url,
                contentDescription = "当前人物",
                modifier = Modifier.fillMaxWidth(0.5f),
                contentScale = ContentScale.Crop
            )
            Card(
                shape = RoundedCornerShape(5.dp)
            ) {
                Column(
                    Modifier
                        .padding(5.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Rating")
                        Text(text = "${model.info.lastOrNull()?.rating}", fontWeight = FontWeight.Bold)
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "游玩次数")
                        Text(text = "${model.info.lastOrNull()?.playCount}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoMaimaiTeamArea() {
    val model: InfoMaimaiPageViewModel = viewModel()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING)
    ) {
        model.currentTeam.forEach { entry ->
            InfoMaimaiCharacterCapsule(entry)
        }
    }
}

@Composable
fun InfoMaimaiDetailButtons(navController: NavController) {
    Column(
        Modifier
            .padding(SCREEN_PADDING)
            .fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
        ) {
            Button(
                onClick = { navController.navigate(HomeNavItem.Home.route + "/info/maimai/trophy") },
                Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "称号一览按钮图标",
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "称号一览")
            }
            Button(
                onClick = { navController.navigate(HomeNavItem.Home.route + "/info/maimai/character") },
                Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "角色一览按钮图标",
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "角色一览")
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
        ) {
            Button(
                onClick = { navController.navigate(HomeNavItem.Home.route + "/info/maimai/nameplate") },
                Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "姓名框一览按钮图标",
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "姓名框一览")
            }
            Button(
                onClick = { navController.navigate(HomeNavItem.Home.route + "/info/maimai/frame") },
                Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Rectangle,
                    contentDescription = "底板一览按钮图标",
                    Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "底板一览")
            }
        }
        Button(
            onClick = { navController.navigate(HomeNavItem.Home.route + "/info/maimai/levels") },
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        {
            Icon(
                imageVector = Icons.Default.PieChart,
                contentDescription = "歌曲完成度按钮图标",
                Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "歌曲完成度")
        }
    }
}

@Composable
fun InfoMaimaiCharacterCapsule(entry: UserMaimaiCharacterEntry) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        AsyncImage(
            model = entry.url,
            contentDescription = "${entry.name}的头像",
            modifier = Modifier
                .clip(CircleShape)
                .border(
                    BorderStroke(1.dp, Color.Black),
                    CircleShape
                )
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(1.dp),
            contentScale = ContentScale.Crop
        )
        Text(text = entry.level)
    }
}