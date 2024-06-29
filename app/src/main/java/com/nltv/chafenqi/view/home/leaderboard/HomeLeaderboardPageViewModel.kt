package com.nltv.chafenqi.view.home.leaderboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.storage.user.CFQUser
import com.nltv.chafenqi.view.songlist.stats.SongStatsTabItem
import java.util.Locale

// TODO: 第一名获得数

class HomeLeaderboardPageViewModel: ViewModel() {
    val mode = CFQUser.mode

    val maimaiStatsTabs = listOf(
        SongStatsTabItem(
            title = "Rating",
            unselectedIcon = Icons.Outlined.Leaderboard,
            selectedIcon = Icons.Filled.Leaderboard
        ),
        SongStatsTabItem(
            title = "总完成率",
            unselectedIcon = Icons.Outlined.PieChart,
            selectedIcon = Icons.Filled.PieChart
        ),
        SongStatsTabItem(
            title = "总游玩曲目",
            unselectedIcon = Icons.Outlined.WorkspacePremium,
            selectedIcon = Icons.Filled.WorkspacePremium
        )
    )

    val chunithmStatsTabs = listOf(
        SongStatsTabItem(
            title = "Rating",
            unselectedIcon = Icons.Outlined.Leaderboard,
            selectedIcon = Icons.Filled.Leaderboard
        ),
        SongStatsTabItem(
            title = "总分数",
            unselectedIcon = Icons.Outlined.PieChart,
            selectedIcon = Icons.Filled.PieChart
        ),
        SongStatsTabItem(
            title = "总游玩曲目",
            unselectedIcon = Icons.Outlined.WorkspacePremium,
            selectedIcon = Icons.Filled.WorkspacePremium
        )
    )

    val maiRatingData = CFQUser.Maimai.Aux.ratingLeaderboard.mapIndexed { index, item ->
        HomeLeaderboardRowData(
            index = index,
            uid = item.uid,
            username = item.username,
            nickname = item.nickname,
            info = item.rating.toString()
        )
    }
    val maiScoreData = CFQUser.Maimai.Aux.totalScoreLeaderboard.mapIndexed { index, item ->
        HomeLeaderboardRowData(
            index = index,
            uid = item.uid,
            username = item.username,
            nickname = item.nickname,
            info = String.format(Locale.ENGLISH, "%.4f", item.totalAchievements) + "%"
        )
    }
    val maiPlayedData = CFQUser.Chunithm.Aux.totalPlayedLeaderboard.mapIndexed { index, item ->
        HomeLeaderboardRowData(
            index = index,
            uid = item.uid,
            username = item.username,
            nickname = item.nickname,
            info = item.totalPlayed.toString()
        )
    }

    val chuRatingData = CFQUser.Chunithm.Aux.ratingLeaderboard.mapIndexed { index, item ->
        HomeLeaderboardRowData(
            index = index,
            uid = item.uid,
            username = item.username,
            nickname = item.nickname,
            info = String.format(Locale.ENGLISH, "%.2f", item.rating)
        )
    }
    val chuScoreData = CFQUser.Chunithm.Aux.totalScoreLeaderboard.mapIndexed { index, item ->
        HomeLeaderboardRowData(
            index = index,
            uid = item.uid,
            username = item.username,
            nickname = item.nickname,
            info = item.totalScore.toString()
        )
    }
    val chuPlayedData = CFQUser.Chunithm.Aux.totalPlayedLeaderboard.mapIndexed { index, item ->
        HomeLeaderboardRowData(
            index = index,
            uid = item.uid,
            username = item.username,
            nickname = item.nickname,
            info = item.totalPlayed.toString()
        )
    }
}