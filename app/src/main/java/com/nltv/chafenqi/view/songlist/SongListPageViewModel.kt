package com.nltv.chafenqi.view.songlist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SongListPageViewModel(

) : ViewModel() {
    private var isLoading = MutableStateFlow(true)

    var isUiLoading = isLoading.asStateFlow()

    init {
        isLoading.update { false }
    }



    companion object {
        private const val TIMEOUT_MILLS = 5_000L
    }
}
