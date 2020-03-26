package com.example.cameraxdemo.ui.home.faceDetection

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import java.util.concurrent.TimeUnit

class FaceAnalyser(
  mode: Int,
  private val listener: (MutableList<FirebaseVisionFace>) -> Unit
) : ImageAnalysis.Analyzer {

  companion object {
    private const val TAG = "CameraXAnalyser"
    const val HIGH_ACCURACY = 0
    const val REAL_TIME = 1
  }

  private var lastTimeStamp = 0L

  private var image: FirebaseVisionImage? = null
  private val realTimeOpts = FirebaseVisionFaceDetectorOptions.Builder()
      .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
      .build()

  private val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
      .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
      .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
      .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
      .build()
  private val detector = FirebaseVision.getInstance()
      .getVisionFaceDetector(
          when (mode) {
            HIGH_ACCURACY -> highAccuracyOpts
            REAL_TIME -> realTimeOpts
            else -> throw Exception("Invalid mode selected")
          }
      )

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
          detector.detectInImage(image)
              .addOnSuccessListener { firebaseVisionFace ->
                this.image = null
                listener.invoke(firebaseVisionFace)
              }
              .addOnFailureListener {
                Log.e(
                    TAG, "Error: ${it.message}", it
                )
              }
        }
      }
    }
    imageProxy.close()
  }

}