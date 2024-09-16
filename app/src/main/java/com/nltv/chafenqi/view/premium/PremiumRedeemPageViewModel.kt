package com.nltv.chafenqi.view.premium

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import com.nltv.chafenqi.R
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.user.CFQUser

class PremiumRedeemPageViewModel : ViewModel() {
    val user = CFQUser

    fun openPremiumPurchaseWebpage(handler: UriHandler) {
        handler.openUri(
            Uri.parse("https://afdian.com/a/chafenqi")
                .toString()
        )
    }

    suspend fun redeemMembership(redeemCode: String): Boolean {
        return CFQServer.apiRedeem(user.username, redeemCode)
    }
}

data class PremiumPerkData(
    val title: String = "",
    val text: String = "",
    val icon: ImageVector = Icons.Default.Person,
    val resId: Int = -1
)

val PREMIUM_PERKS = listOf(
    PremiumPerkData(
        title = "国服排行榜",
        text = "查看Rating、总分、总游玩曲目、榜一获得数的总榜及详细信息",
        icon = Icons.Default.Leaderboard,
        resId = R.drawable.leaderboard_android
    ),
    PremiumPerkData(
        title = "详细个人信息",
        text = "全面查看各难度歌曲完成情况、已获得的收藏品和人物立绘",
        icon = Icons.Default.PersonSearch,
        resId = R.drawable.userinfo_android
    ),
    PremiumPerkData(
        title = "单曲历史成绩图表",
        text = "查询和比较单曲的历史游玩成绩和详细信息，并显示成绩趋势图",
        icon = Icons.AutoMirrored.Filled.ShowChart,
        resId = R.drawable.scoretrend_android
    ),
    PremiumPerkData(
        title = "出勤记录",
        text = "精确到每日的出勤详细记录、数据变化和趋势分析",
        icon = Icons.Default.History,
        resId = R.drawable.log_android
    ),
//    PremiumPerkData(
//        title = "小组件自定义",
//        text = "使用已获得的角色、称号、底板等装饰桌面小组件",
//        icon = Icons.Default.DashboardCustomize
//    ),
    PremiumPerkData(
        title = "更多功能",
        text = "不定期加入的订阅会员限定功能",
        icon = Icons.Default.MoreHoriz
    ),
)