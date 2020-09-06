package com.example.cameraxdemo.ui.home.textScan

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

class TextAnalyser(private val listener: (Text) -> Unit) : ImageAnalysis.Analyzer {

  companion object {
    const val TAG = "TextAnalyser"
  }

  private val detector = TextRecognition.getClient()

  @SuppressLint("UnsafeExperimentalUsageError")
  override fun analyze(imageProxy: ImageProxy) {
    val mediaImage = imageProxy.image ?: kotlin.run {
      imageProxy.close()
      return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    detector.process(image)
        .addOnSuccessListener { listener.invoke(it) }
        .addOnFailureListener { Log.e(TAG, "Error: ${it.message}", it) }
        .addOnCompleteListener { imageProxy.close() }

  }

}