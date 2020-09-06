package com.example.cameraxdemo.ui.home.objectDetection

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectAnalyser(
  private val isMultipleMode: Boolean,
  private val listener: (MutableList<DetectedObject>) -> Unit
) : ImageAnalysis.Analyzer {

  companion object {
    private const val TAG = "ObjectAnalyser"
  }

  private val options = ObjectDetectorOptions.Builder()
      .apply {
        setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        if (isMultipleMode) enableMultipleObjects()
      }
      .build()

  private val detector = ObjectDetection.getClient(options)

  @SuppressLint("UnsafeExperimentalUsageError")
  override fun analyze(imageProxy: ImageProxy) {
    val mediaImage = imageProxy.image ?: kotlin.run {
      imageProxy.close()
      return
    }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    detector.process(image)
        .addOnSuccessListener { visionObjectList ->
          listener(visionObjectList)
        }
        .addOnFailureListener {
          Log.e(TAG, "Error: ${it.message}", it)
        }
        .addOnCompleteListener {
          imageProxy.close()
        }
  }

}