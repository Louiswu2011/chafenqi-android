package com.nltv.chafenqi.view.home.rating.screenshot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class HomeRatingScreenshotViewModel : ViewModel() {
    fun shareScreenshot(bitmap: Bitmap?, context: Context): Boolean {
        if (bitmap == null) return false

        try {
            viewModelScope.launch {
                val cacheDir = context.cacheDir
                val filePathString = cacheDir.absolutePath + "/screenshot.png"
                val tempFile = File(filePathString)

                FileOutputStream(tempFile).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    val uri =
                        FileProvider.getUriForFile(context, "com.nltv.chafenqi.provider", tempFile)
                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/jpeg"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}