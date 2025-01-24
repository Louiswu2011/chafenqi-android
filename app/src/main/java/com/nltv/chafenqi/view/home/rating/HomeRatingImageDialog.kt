package com.nltv.chafenqi.view.home.rating

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.service.chooser.ChooserAction
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil3.Image
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.toBitmap
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.user.CFQUser
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import java.io.File

@Composable
fun HomeRatingImageDialog(
    mode: Int,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        HomeRatingImageSurface(mode, onDismissRequest)
    }
}

@Composable
fun HomeRatingImageSurface(
    mode: Int,
    onDismissRequest: () -> Unit
) {
    val platformContext = LocalPlatformContext.current
    var finished by remember { mutableStateOf(false) }

    var image by remember { mutableStateOf<Image?>(null) }

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ZoomableAsyncImage(
                model = ImageRequest
                    .Builder(platformContext)
                    .httpHeaders(
                        NetworkHeaders
                            .Builder()
                            .set("Authorization", "Bearer ${CFQUser.token}")
                            .build()
                    )
                    .data(if (mode == 0) CFQServer.b30Path else CFQServer.b50Path)
                    .listener(
                        onSuccess = { _, result ->
                            finished = true
                            image = result.image },
                        onError = { _, _ -> finished = true }
                    )
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .build(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )

            AnimatedVisibility(!finished) {
                CircularProgressIndicator()
            }

            Row (
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    onDismissRequest()
                }, modifier = Modifier.padding(10.dp)) {
                    Icon(Icons.Default.Close, "关闭")
                }

                IconButton(onClick = {
                    val fileUri = File(platformContext.cacheDir, if (mode == 0) "b30.jpg" else "b50.jpg")
                        .also { file ->
                            file.delete()
                            file.createNewFile()
                            file.outputStream()
                                .use {
                                    image!!.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, it)
                                }
                        }
                        .let {
                            FileProvider.getUriForFile(
                                platformContext,
                                platformContext.applicationContext.packageName + ".provider",
                                it
                            )
                        }

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        type = "image/jpg"
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        clipData = ClipData.newRawUri(null, fileUri)
                    }
                    platformContext.startActivity(Intent.createChooser(sendIntent, null))
                }, modifier = Modifier.padding(10.dp), enabled = image != null) {
                    Icon(Icons.Default.Share, "分享")
                }
            }
        }
    }
}