package com.nltv.chafenqi.view.home.rating.share

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun HomeRatingShareDialog(
    onDismissRequest: () -> Unit
) {
    val model = viewModel<HomeRatingShareDialogViewModel>()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sendImageIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uiState.imageUri)
        setDataAndType(uiState.imageUri, "image/jpeg")
    }
    val shareIntent = Intent.createChooser(sendImageIntent, null)

    LaunchedEffect(Unit) {
        model.fetchImage(context)
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card (
            modifier = Modifier.fillMaxWidth()
                .height(550.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column (
                modifier = Modifier.fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column (
                    modifier = Modifier.fillMaxWidth()
                        .height(450.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(uiState.isLoadingImage, label = "LoadingImage") {
                        if (it) {
                            CircularProgressIndicator()
                        } else {
                            AsyncImage(
                                model = uiState.imageUri,
                                contentDescription = "分享图片"
                            )
                        }
                    }
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton( onClick = { onDismissRequest() } ) {
                        Text("取消")
                    }
                    Row {
//                        TextButton( onClick = {}, enabled = uiState.imageUri != null ) {
//                            Text("保存")
//                        }
                        TextButton( onClick = {
                            context.startActivity(shareIntent)
                        }, enabled = uiState.imageUri != null ) {
                            Text("分享")
                        }
                    }
                }
            }
        }
    }
}