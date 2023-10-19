package com.nltv.chafenqi.view

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nltv.chafenqi.ChafenqiApplication
import com.nltv.chafenqi.view.login.LoginPageViewModel
import com.nltv.chafenqi.view.songlist.SongListPageViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LoginPageViewModel(
                chafenqiApplication().container.maiListRepository,
                chafenqiApplication().container.chuListRepository,
                chafenqiApplication().container.userMaiDataRepository
            )
        }
        initializer {
            SongListPageViewModel(
                chafenqiApplication().container.maiListRepository,
                chafenqiApplication().container.chuListRepository
            )
        }
    }
}

fun CreationExtras.chafenqiApplication(): ChafenqiApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChafenqiApplication)