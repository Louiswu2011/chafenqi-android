package com.nltv.chafenqi.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomePageTeamSection(navController: NavController) {
    val model: HomePageViewModel = viewModel()
    val state by model.uiState.collectAsStateWithLifecycle()

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = MaterialTheme.shapes.small,
        onClick = {
            navController.navigate(HomeNavItem.Home.route + "/team")
        }
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(if (state.team == null) "加入或创建团队" else state.team!!.info.displayName, fontWeight = FontWeight.Bold)
        }
    }
}
