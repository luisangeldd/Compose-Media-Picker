package com.luisangeldd.mediapicker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
//import api.luisangeldd.mediapicker.k.utils.appModule


@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        /*GlobalContext.startKoin {
            androidLogger()
            androidContext(this@App)
            modules(MediaPickerModuleKoin)
        }*/
    }
}