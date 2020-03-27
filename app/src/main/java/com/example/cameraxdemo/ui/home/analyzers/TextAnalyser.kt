package com.example.cameraxdemo.ui.home.analyzers

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.util.concurrent.TimeUnit

class TextAnalyser(private val listener: (FirebaseVisionText) -> Unit) : ImageAnalysis.Analyzer {

  companion object {
    const val TAG = "CameraXAnalyser"
  }

  private var lastTimeStamp = 0L

  private var image: FirebaseVisionImage? = null
  private val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

  private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
    0 -> FirebaseVisionImageMetadata.ROTATION_0
    90 -> FirebaseVisionImageMetadata.ROTATION_90
    180 -> FirebaseVisionImageMetadata.ROTATION_180
    270 -> FirebaseVisionImageMetadata.ROTATION_270
    else -> throw Exception("Invalid rotation value")
  }

  @SuppressLint("UnsafeExperimentalUsageError")
  override fun analyze(imageProxy: ImageProxy) {
    val currentTimeStamp = System.currentTimeMillis()
    val intervalInSeconds = TimeUnit.SECONDS.toSeconds(1)
    val deltaTime = currentTimeStamp - lastTimeStamp
    if (deltaTime >= intervalInSeconds) {
      lastTimeStamp = currentTimeStamp
      val mediaImage = imageProxy.image
      val rotationDegrees = imageProxy.imageInfo.rotationDegrees
      val imageRotation = degreesToFirebaseRotation(rotationDegrees)
      if (mediaImage != null && image == null) {
        image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
        image?.let { image ->
          detector.processImage(image)
              .addOnSuccessListener { firebaseVisionText ->
                this.image = null
                listener.invoke(firebaseVisionText)
              }
              .addOnFailureListener {
                Log.e(
                    TAG, "Error: ${it.message}", it)
              }
        }
      }
    }
    imageProxy.close()
  }

}