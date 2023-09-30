package com.luisangeldd.mediapicker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
//import api.luisangeldd.mediapicker.k.utils.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        /*GlobalContext.startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }*/
    }
}