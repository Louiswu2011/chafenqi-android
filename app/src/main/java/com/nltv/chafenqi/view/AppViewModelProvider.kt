package com.nltv.chafenqi.view

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nltv.chafenqi.ChafenqiApplication
import com.nltv.chafenqi.view.home.HomePageViewModel
import com.nltv.chafenqi.view.login.LoginPageViewModel
import com.nltv.chafenqi.view.songlist.SongListPageViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LoginPageViewModel(

            )
        }
        initializer {
            SongListPageViewModel()
        }
        initializer {
            HomePageViewModel(

            )
        }
    }
}

fun CreationExtras.chafenqiApplication(): ChafenqiApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChafenqiApplication)