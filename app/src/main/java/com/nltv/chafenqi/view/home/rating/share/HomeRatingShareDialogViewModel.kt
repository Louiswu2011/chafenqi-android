package com.nltv.chafenqi.view.home.rating.share

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class HomeRatingShareDialogViewUiState(
    val imageUri: Uri? = null,
    val isLoadingImage: Boolean = false
)

class HomeRatingShareDialogViewModel: ViewModel() {
    val user = CFQUser

    private val _uiState = MutableStateFlow(HomeRatingShareDialogViewUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchImage(context: Context) {
        when (user.mode) {
            0 -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            isLoadingImage = true,
                            imageUri = null
                        )
                    }
                    val byteArray = CFQServer.apiFetchUserImage(user.token, "0", "b30")
                    if (byteArray != null) {
                        val file = File(context.cacheDir, "b30.jpg")
                        file.delete()
                        file.createNewFile()
                        file.outputStream().use {
                            it.write(byteArray)
                        }
                        _uiState.update {
                            it.copy(
                                isLoadingImage = false,
                                imageUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                imageUri = null,
                                isLoadingImage = false
                            )
                        }
                    }
                }
            }
            1 -> {
                // TODO: Implement Maimai share
            }
        }
    }
}