package com.example.cameraxdemo

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class MyApplication : Application(), CameraXConfig.Provider {
  override fun getCameraXConfig(): CameraXConfig {
    return Camera2Config.defaultConfig()
  }

}