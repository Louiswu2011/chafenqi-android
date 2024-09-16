package com.nltv.chafenqi.view.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nltv.chafenqi.storage.user.CFQUser

@Composable
fun HomePageLogSection(navController: NavController) {
    Column {
        HomePageLogBar(navController = navController)
        if (CFQUser.isPremium) {
            HomePageLogInfo()
        }
    }

}

@Composable
fun HomePageLogBar(navController: NavController) {
    val model: HomePageViewModel = viewModel()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Text(
            text = "出勤记录",
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit(16f, TextUnitType.Sp)
        )
        Text(
            text = "显示全部",
            fontSize = TextUnit(14f, TextUnitType.Sp),
            modifier = Modifier.clickable {
                if (CFQUser.isPremium) {
                    navController.navigate(HomeNavItem.Home.route + "/log")
                } else {
                    navController.navigate(HomeNavItem.Home.route + "/settings/user/redeem")
                }
            },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun HomePageLogInfo() {
    val model: HomePageViewModel = viewModel()
    val uiState by model.uiState.collectAsState()

    LaunchedEffect(model.user.mode) {
        model.updateLog()
    }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        if (uiState.isLogEmpty) {
            Text(text = "暂无出勤记录，请先上传数据后刷新页面")
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = uiState.logLastPlayedTime)
                    Text(text = "上次出勤时间", fontSize = 12.sp)
                }
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = uiState.logLastPlayedCount)
                    Text(text = "游玩曲目数", fontSize = 12.sp)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = uiState.logLastPlayedDuration)
                    Text(text = "出勤时长", fontSize = 12.sp)
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = uiState.logLastPlayedAverageScore)
                    Text(text = "平均成绩", fontSize = 12.sp)
                }
            }
        }
    }
}