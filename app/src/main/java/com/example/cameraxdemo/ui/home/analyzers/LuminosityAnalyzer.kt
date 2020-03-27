package com.example.cameraxdemo.ui.home.analyzers

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

class LuminosityAnalyzer(private val listener: (String) -> Unit) : ImageAnalysis.Analyzer {
  private var lastTimeStamp = 0L

  override fun analyze(image: ImageProxy) {
    val currentTimestamp = System.currentTimeMillis()
    val intervalInSeconds = TimeUnit.SECONDS.toMillis(1)
    val deltaTime = currentTimestamp - lastTimeStamp
    if (deltaTime >= intervalInSeconds) {
      val buffer = image.planes[0].buffer
      val data = buffer.toByteArray()
      val pixels = data.map { it.toInt() and 0xFF }
      val luma = pixels.average()
      lastTimeStamp = currentTimestamp
      listener.invoke(
          luma.toInt()
              .toString()
      )
    }
    image.close()
  }

  private fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
  }

}