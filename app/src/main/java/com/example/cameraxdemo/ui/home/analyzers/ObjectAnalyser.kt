package com.example.cameraxdemo.ui.home.analyzers

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import java.util.concurrent.TimeUnit

class ObjectAnalyser(
  private val isMultipleMode: Boolean,
  private val listener: (MutableList<FirebaseVisionObject>) -> Unit
) :
    ImageAnalysis.Analyzer {

  companion object {
    const val TAG = "CameraXAnalyser"
  }

  private var lastTimeStamp = 0L

  private var image: FirebaseVisionImage? = null
  private val options = FirebaseVisionObjectDetectorOptions.Builder()
      .apply {
        setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
        if (isMultipleMode) enableMultipleObjects()
      }
      .build()

  private val detector = FirebaseVision.getInstance()
      .getOnDeviceObjectDetector(options)

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
              .addOnSuccessListener { firebaseVisionObjectList ->
                this.image = null
                listener.invoke(firebaseVisionObjectList)
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