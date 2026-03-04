package com.moviestreamer.di

import com.moviestreamer.ui.parental.ParentalControlsManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val securityModule = module {
    single { ParentalControlsManager(androidContext()) }
}
