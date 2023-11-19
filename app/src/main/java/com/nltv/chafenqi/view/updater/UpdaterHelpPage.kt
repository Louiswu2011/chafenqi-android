package com.nltv.chafenqi.view.updater

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nltv.chafenqi.SCREEN_PADDING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdaterHelpPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "传分") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回上一级"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column (
            Modifier
                .padding(paddingValues)
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UpdaterHelpStepBlock()
        }
    }
}

@Composable
fun UpdaterHelpStepBlock() {
    HELPS.forEach { info ->
        Row (
            Modifier.fillMaxWidth()
                .padding(SCREEN_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(imageVector = info.icon, contentDescription = info.contentDescription)
            Column {
                Text(text = info.title, style = MaterialTheme.typography.titleMedium)
                Text(text = info.text, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}