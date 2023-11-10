package com.nltv.chafenqi.view.updater

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alorma.compose.settings.ui.SettingsSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdaterHomePage() {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "传分") },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            ProxyToggle()

        }
    }
}

@Composable
fun ProxyToggle() {
    var isVpnOn by remember {
        mutableStateOf(false)
    }

    val model: UpdaterViewModel = viewModel()
    val context = LocalContext.current

    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                model.startVPN(context)
                isVpnOn = true
            }
        })

    SettingsSwitch(
        title = { Text(text = "开关") },
        icon = { Icon(imageVector = Icons.Default.Wifi, contentDescription = "代理开关") },
        onCheckedChange = { checked ->
            isVpnOn = checked
            if (checked) {
                val intent = model.prepareVPN(context)
                intent?.also { vpnLauncher.launch(it) } ?: run {
                    model.startVPN(context)
                }
            } else {
                model.stopVPN(context)
            }
        }
    )
}