package com.nltv.chafenqi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nltv.chafenqi.view.login.LoginPage
import com.nltv.chafenqi.ui.theme.ChafenqiTheme
import com.nltv.chafenqi.view.AppViewModelProvider
import com.nltv.chafenqi.view.home.HomeNavItem
import com.nltv.chafenqi.view.home.HomePage
import com.nltv.chafenqi.view.home.HomeRecentPage
import com.nltv.chafenqi.view.login.LoginPageViewModel
import com.nltv.chafenqi.view.songlist.SongListPage
import com.nltv.chafenqi.view.updater.UpdaterHomePage
import java.io.File

enum class UIState {
    Pending, Loading, Finished
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChafenqiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChafenqiApp()
                }
            }
        }
    }
}

@Composable
fun ChafenqiApp() {
    val navController = rememberNavController()
    val loginModel: LoginPageViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val loginState by loginModel.loginUiState.collectAsStateWithLifecycle()

    if (loginState.loginState == UIState.Finished) {
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
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(HomeNavItem.Home.route) { HomePage(navController = navController) }
                composable(HomeNavItem.Home.route + "/recent") { HomeRecentPage(navController = navController) }

                composable(HomeNavItem.Uploader.route) { UpdaterHomePage() }
                composable(HomeNavItem.SongList.route) { SongListPage(navController = navController) }
            }
        }
    } else {
        LoginPage()
    }
}

