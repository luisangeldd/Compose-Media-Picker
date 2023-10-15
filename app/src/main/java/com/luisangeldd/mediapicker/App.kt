package com.luisangeldd.mediapicker

import android.app.Application
import android.content.Context
import api.luisangeldd.mediapicker.core.MediaPickerModule
import api.luisangeldd.mediapicker.core.MediaPickerModuleImpl

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        mediaPickerModule = MediaPickerModuleImpl(this)
    }
    companion object{
        lateinit var mediaPickerModule: MediaPickerModule
    }
}