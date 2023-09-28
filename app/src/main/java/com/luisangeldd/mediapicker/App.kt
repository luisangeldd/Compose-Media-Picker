package com.luisangeldd.mediapicker

import android.app.Application
import api.luisangeldd.mediapicker.utils.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}