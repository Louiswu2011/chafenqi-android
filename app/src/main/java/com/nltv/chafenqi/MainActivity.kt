package com.nltv.chafenqi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.ui.theme.ChafenqiTheme
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.HomePage
import com.nltv.chafenqi.view.home.announcement.HomeAnnouncementPage
import com.nltv.chafenqi.view.home.leaderboard.HomeLeaderboardPage
import com.nltv.chafenqi.view.home.log.HomeLogPage
import com.nltv.chafenqi.view.home.log.LogDetailPage
import com.nltv.chafenqi.view.home.rating.HomeRatingPage
import com.nltv.chafenqi.view.home.recent.HomeRecentPage
import com.nltv.chafenqi.view.home.recent.RecentDetailPage
import com.nltv.chafenqi.view.info.InfoPage
import com.nltv.chafenqi.view.info.chunithm.InfoChunithmCharacterPage
import com.nltv.chafenqi.view.info.chunithm.InfoChunithmMapIconPage
import com.nltv.chafenqi.view.info.chunithm.InfoChunithmNameplatePage
import com.nltv.chafenqi.view.info.chunithm.InfoChunithmSkillPage
import com.nltv.chafenqi.view.info.chunithm.InfoChunithmTicketPage
import com.nltv.chafenqi.view.info.chunithm.InfoChunithmTrophyPage
import com.nltv.chafenqi.view.info.chunithm.level.InfoChunithmLevelsPage
import com.nltv.chafenqi.view.info.maimai.InfoMaimaiCharacterPage
import com.nltv.chafenqi.view.info.maimai.InfoMaimaiFramePage
import com.nltv.chafenqi.view.info.maimai.InfoMaimaiNameplatePage
import com.nltv.chafenqi.view.info.maimai.InfoMaimaiTrophyPage
import com.nltv.chafenqi.view.info.maimai.level.InfoMaimaiLevelsPage
import com.nltv.chafenqi.view.login.LoginPage
import com.nltv.chafenqi.view.premium.PremiumRedeemPage
import com.nltv.chafenqi.view.settings.SettingsPage
import com.nltv.chafenqi.view.settings.about.SettingsAboutPage
import com.nltv.chafenqi.view.settings.about.SettingsAcknowledgePage
import com.nltv.chafenqi.view.settings.advanced.SettingsAdvancedPage
import com.nltv.chafenqi.view.settings.home.SettingsHomeArrangementPage
import com.nltv.chafenqi.view.settings.home.SettingsHomePage
import com.nltv.chafenqi.view.settings.playerInfo.SettingsInfoPage
import com.nltv.chafenqi.view.settings.qs.SettingsQSTilePage
import com.nltv.chafenqi.view.settings.user.SettingsBindFishPage
import com.nltv.chafenqi.view.settings.user.SettingsBindQQPage
import com.nltv.chafenqi.view.settings.user.SettingsUserPage
import com.nltv.chafenqi.view.songlist.SongDetailPage
import com.nltv.chafenqi.view.songlist.SongListPage
import com.nltv.chafenqi.view.songlist.comment.CommentPage
import com.nltv.chafenqi.view.songlist.record.MusicRecordPage
import com.nltv.chafenqi.view.songlist.stats.SongStatsPage
import com.nltv.chafenqi.view.updater.UpdaterHelpPage
import com.nltv.chafenqi.view.updater.UpdaterHomePage

enum class UIState {
    Pending, Loading, Finished
}

val SCREEN_PADDING = 10.dp

class MainActivity : ComponentActivity() {
    private val userState by viewModels<CFQUserStateViewModel>()
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        // enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics

        setContent {
            CompositionLocalProvider(LocalUserState provides userState) {
                ChafenqiApp()
            }
        }
    }
}

@Composable
fun ChafenqiApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userState = LocalUserState.current
    val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .maxSizePercent(0.05)
                .directory(context.cacheDir.resolve("cover_cache"))
                .build()
        }
        .crossfade(true)
        .build()

    Coil.setImageLoader(imageLoader)

    LaunchedEffect(Unit) {
        CFQServer.setDefaultServerPath(context.getString(R.string.serverAddress))
    }

    ChafenqiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Crossfade(targetState = userState.isLoggedIn, label = "Login page") {
                if (it) {
                    LogonPage(navController)
                } else {
                    LoginPage()
                }
            }
        }
    }
}

@Composable
fun LogonPage(navController: NavHostController) {
    val screenList = listOf(HomeNavItem.Home, HomeNavItem.Uploader, HomeNavItem.SongList)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screenList.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.displayIcon, screen.displayName) },
                        label = { Text(text = screen.displayName) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNavItem.Home.route,
            modifier = Modifier.padding(innerPadding),
            /*enterTransition = {
                fadeIn(tween(300, easing = LinearEasing)) +
                        slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(300, easing = EaseIn))
            },
            exitTransition = {
                fadeOut(tween(300, easing = LinearEasing)) +
                        slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(300, easing = EaseOut))
            },
            popEnterTransition = {
                fadeIn(tween(300, easing = LinearEasing))
            },*/
            /*popExitTransition = {
                fadeOut(tween(300, easing = LinearEasing)) +
                        slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(300, easing = EaseOut))
            }*/
        ) {
            composable(HomeNavItem.Home.route) { HomePage(navController = navController) }

            composable(HomeNavItem.Home.route + "/announcement") {
                HomeAnnouncementPage(
                    navController = navController
                )
            }

            composable(HomeNavItem.Home.route + "/log") { HomeLogPage(navController) }
            composable(HomeNavItem.Home.route + "/log/maimai/{index}") {
                LogDetailPage(
                    navController = navController,
                    mode = 1,
                    index = it.arguments?.getString("index")?.toInt() ?: 0
                )
            }
            composable(HomeNavItem.Home.route + "/log/chunithm/{index}") {
                LogDetailPage(
                    navController = navController,
                    mode = 0,
                    index = it.arguments?.getString("index")?.toInt() ?: 0
                )
            }

            composable(HomeNavItem.Home.route + "/recent") { HomeRecentPage(navController = navController) }
            composable(HomeNavItem.Home.route + "/recent/maimai/{index}") { navBackStackEntry ->
                RecentDetailPage(
                    mode = 1,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    navController = navController
                )
            }
            composable(HomeNavItem.Home.route + "/recent/chunithm/{index}") { navBackStackEntry ->
                RecentDetailPage(
                    mode = 0,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    navController = navController
                )
            }
            composable(HomeNavItem.Home.route + "/rating") { HomeRatingPage(navController) }
            composable(HomeNavItem.Home.route + "/leaderboard") { HomeLeaderboardPage(navController) }

            composable(HomeNavItem.Home.route + "/settings") { SettingsPage(navController) }
            composable(HomeNavItem.Home.route + "/settings/user") { SettingsUserPage(navController) }
            composable(HomeNavItem.Home.route + "/settings/home") { SettingsHomePage(navController) }
            composable(HomeNavItem.Home.route + "/settings/home/arrangement") { SettingsHomeArrangementPage(
                navController = navController
            ) }
            composable(HomeNavItem.Home.route + "/settings/playerInfo") {
                SettingsInfoPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/settings/qsTile") {
                SettingsQSTilePage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/settings/advanced") {
                SettingsAdvancedPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/settings/about") { SettingsAboutPage(navController) }
            composable(HomeNavItem.Home.route + "/settings/user/redeem") {
                PremiumRedeemPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/settings/user/bind/fish") {
                SettingsBindFishPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/settings/user/bind/qq") {
                SettingsBindQQPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/settings/about/acknowledge") {
                SettingsAcknowledgePage(
                    navController
                )
            }

            composable(HomeNavItem.Home.route + "/info") { InfoPage(navController) }
            composable(HomeNavItem.Home.route + "/info/maimai/trophy") {
                InfoMaimaiTrophyPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/maimai/character") {
                InfoMaimaiCharacterPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/maimai/nameplate") {
                InfoMaimaiNameplatePage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/maimai/frame") {
                InfoMaimaiFramePage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/maimai/levels") {
                InfoMaimaiLevelsPage(
                    navController
                )
            }

            composable(HomeNavItem.Home.route + "/info/chunithm/character") {
                InfoChunithmCharacterPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/chunithm/skill") {
                InfoChunithmSkillPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/chunithm/nameplate") {
                InfoChunithmNameplatePage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/chunithm/trophy") {
                InfoChunithmTrophyPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/chunithm/ticket") {
                InfoChunithmTicketPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/chunithm/mapIcon") {
                InfoChunithmMapIconPage(
                    navController
                )
            }
            composable(HomeNavItem.Home.route + "/info/chunithm/levels") {
                InfoChunithmLevelsPage(
                    navController
                )
            }

            composable(HomeNavItem.Uploader.route) { UpdaterHomePage(navController) }
            composable(HomeNavItem.Uploader.route + "/help") { UpdaterHelpPage(navController) }

            composable(HomeNavItem.SongList.route) { SongListPage(navController) }
            composable(HomeNavItem.SongList.route + "/maimai/{index}") { navBackStackEntry ->
                SongDetailPage(
                    mode = 1,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    navController = navController
                )
            }
            composable(HomeNavItem.SongList.route + "/maimai/{index}/{levelIndex}") { navBackStackEntry ->
                MusicRecordPage(
                    navController = navController,
                    mode = 1,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    levelIndex = navBackStackEntry.arguments?.getString("levelIndex")?.toInt() ?: 0
                )
            }
            composable(HomeNavItem.SongList.route + "/maimai/stats/{index}/{levelIndex}") { navBackStackEntry ->
                SongStatsPage(
                    mode = 1,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    difficulty = navBackStackEntry.arguments?.getString("levelIndex")?.toInt() ?: 0,
                    navController = navController
                )
            }
            composable(HomeNavItem.SongList.route + "/maimai/{index}/comment") { navBackStackEntry ->
                CommentPage(
                    mode = 1,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    navController = navController
                )
            }
            composable(HomeNavItem.SongList.route + "/chunithm/{index}") { navBackStackEntry ->
                SongDetailPage(
                    mode = 0,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    navController = navController
                )
            }
            composable(HomeNavItem.SongList.route + "/chunithm/{index}/{levelIndex}") { navBackStackEntry ->
                MusicRecordPage(
                    navController = navController,
                    mode = 0,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    levelIndex = navBackStackEntry.arguments?.getString("levelIndex")?.toInt() ?: 0
                )
            }
            composable(HomeNavItem.SongList.route + "/chunithm/stats/{index}/{levelIndex}") { navBackStackEntry ->
                SongStatsPage(
                    mode = 0,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    difficulty = navBackStackEntry.arguments?.getString("levelIndex")?.toInt() ?: 0,
                    navController = navController
                )
            }
            composable(HomeNavItem.SongList.route + "/chunithm/{index}/comment") { navBackStackEntry ->
                CommentPage(
                    mode = 0,
                    index = navBackStackEntry.arguments?.getString("index")?.toInt() ?: 0,
                    navController = navController
                )
            }
        }
    }
}

