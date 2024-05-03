package com.nltv.chafenqi.view.info.chunithm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nltv.chafenqi.SCREEN_PADDING
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.home.HomeNavItem
import kotlinx.coroutines.launch

@Composable
fun InfoChunithmPage(navController: NavController, snackbarHostState: SnackbarHostState) {
    val isEmpty = CFQUser.chunithm.isExtraEmpty
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        InfoChunithmLeader()
        InfoChunithmActiveSkill()
        InfoChunithmStats()
        InfoChunithmDetailButtons(navController)
        InfoChunithmFriendCode(snackbarHostState)
    }
}

@Composable
fun InfoChunithmLeader() {
    val model: InfoChunithmPageViewModel = viewModel()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
    ) {
        AsyncImage(
            model = model.currentCollection.charUrl,
            contentDescription = "角色立绘",
            modifier = Modifier.fillMaxWidth(0.8f),
            contentScale = ContentScale.Crop
        )
        Text(text = model.currentCollection.charName, fontWeight = FontWeight.Bold)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "等级")
                Text(text = model.currentCollection.charRank, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = model.currentCollection.charExp.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InfoChunithmActiveSkill() {
    val model: InfoChunithmPageViewModel = viewModel()

    if (model.currentSkill != null) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(SCREEN_PADDING)
                .height(52.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = model.currentSkill.icon,
                    contentDescription = "当前技能图标",
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = model.currentSkill.name, fontSize = 14.sp)
                    Text(
                        text = model.currentSkill.description,
                        maxLines = 2,
                        fontSize = 12.sp,
                        softWrap = true,
                        lineHeight = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "等级")
                Text(text = model.currentSkill.level.toString(), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoChunithmStats() {
    val model: InfoChunithmPageViewModel = viewModel()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "游玩次数")
            Text(text = model.info.playCount.toString(), fontWeight = FontWeight.Bold)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "OVERPOWER")
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${String.format("%.2f", model.info.rawOverpower)} ")
                    }
                    append("(${String.format("%.2f", model.info.overpowerPercentage)}%)")

                }
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "金币数")
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${model.info.currentGold} ")
                    }
                    append("(${model.info.totalGold})")
                }
            )
        }
    }
}

@Composable
fun InfoChunithmDetailButtons(navController: NavController) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
        ) {
            InfoChunithmNavigationButton(
                icon = Icons.Default.Group,
                iconDescription = "角色一览按钮",
                text = "角色一览",
                navController = navController,
                additionalRoute = "character",
                modifier = Modifier.weight(1f)
            )
            InfoChunithmNavigationButton(
                icon = Icons.Default.Star,
                iconDescription = "技能一览按钮",
                text = "技能一览",
                navController = navController,
                additionalRoute = "skill",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
        ) {
            InfoChunithmNavigationButton(
                icon = Icons.Default.PhotoLibrary,
                iconDescription = "名牌版一览按钮",
                text = "名牌版一览",
                navController = navController,
                additionalRoute = "nameplate",
                modifier = Modifier.weight(1f)
            )
            InfoChunithmNavigationButton(
                icon = Icons.Default.CheckCircle,
                iconDescription = "称号一览按钮",
                text = "称号一览",
                navController = navController,
                additionalRoute = "trophy",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING)
        ) {
            InfoChunithmNavigationButton(
                icon = Icons.Default.AirplaneTicket,
                iconDescription = "功能票一览按钮",
                text = "功能票一览",
                navController = navController,
                additionalRoute = "ticket",
                modifier = Modifier.weight(1f)
            )
            InfoChunithmNavigationButton(
                icon = Icons.Default.PersonPin,
                iconDescription = "地图头像一览按钮",
                text = "地图头像一览",
                navController = navController,
                additionalRoute = "mapIcon",
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = { navController.navigate(HomeNavItem.Home.route + "/info/chunithm/levels") },
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
fun InfoChunithmNavigationButton(
    icon: ImageVector,
    iconDescription: String,
    text: String,
    navController: NavController,
    additionalRoute: String,
    modifier: Modifier
) {
    Button(
        onClick = { navController.navigate(HomeNavItem.Home.route + "/info/chunithm/$additionalRoute") },
        Modifier
            .fillMaxWidth()
            .then(modifier), shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
            Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = text)
    }
}

@Composable
fun InfoChunithmFriendCode(snackbarHostState: SnackbarHostState) {
    val model: InfoChunithmPageViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(SCREEN_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "好友代码")
        Row(
            horizontalArrangement = Arrangement.spacedBy(SCREEN_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = model.info.friendCode, fontWeight = FontWeight.Bold)
            Text(text = "复制", modifier = Modifier.clickable {
                clipboardManager.setText(AnnotatedString(model.info.friendCode))
                scope.launch { snackbarHostState.showSnackbar("已复制到剪贴板") }
            }, color = MaterialTheme.colorScheme.primary)
        }
    }
}