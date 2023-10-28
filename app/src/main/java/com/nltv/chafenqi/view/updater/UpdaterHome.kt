package com.nltv.chafenqi.view.updater

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UpdaterHomePage() {
    var isVpnOn by remember {
        mutableStateOf(false)
    }

    val model: UpdaterViewModel = viewModel()
    val context = LocalContext.current

    val vpnLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult() , onResult = {
        if (it.resultCode == Activity.RESULT_OK) {
            model.startVPN(context)
            isVpnOn = true
        }
    })

    Column {
        Switch(checked = isVpnOn, onCheckedChange = { checked ->
            isVpnOn = checked
            if (checked) {
                val intent = model.prepareVPN(context)
                intent?.also { vpnLauncher.launch(it) } ?: run {
                    model.startVPN(context)
                }
            } else {
                model.stopVPN(context)
            }
        })
    }
}