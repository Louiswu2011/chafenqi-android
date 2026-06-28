package com.nltv.chafenqi.di

import com.nltv.chafenqi.storage.user.CFQUser
import org.koin.dsl.module

val userModule = module {
    single { CFQUser() }
}
