package com.sedat.note.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApp: Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig = Camera2Config.defaultConfig()

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}