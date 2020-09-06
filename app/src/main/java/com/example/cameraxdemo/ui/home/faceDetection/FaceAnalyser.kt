package com.example.cameraxdemo.ui.home.faceDetection

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyser(
  mode: Int,
  private val listener: (MutableList<Face>) -> Unit
) : ImageAnalysis.Analyzer {

  companion object {
    private const val TAG = "FaceAnalyser"
    const val HIGH_ACCURACY = 0
    const val REAL_TIME = 1
  }

  private val realTimeOpts by lazy {
    FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()
  }

  private val highAccuracyOpts by lazy {
    FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
  }

  private val detector = FaceDetection
      .getClient(
          when (mode) {
            HIGH_ACCURACY -> highAccuracyOpts
            REAL_TIME -> realTimeOpts
            else -> throw Exception("Invalid mode selected")
          }
      )

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