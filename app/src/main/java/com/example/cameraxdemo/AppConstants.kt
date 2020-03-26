package com.example.cameraxdemo

import androidx.camera.core.AspectRatio
import com.example.cameraxdemo.AppConstants.RATIO_16_9_VALUE
import com.example.cameraxdemo.AppConstants.RATIO_4_3_VALUE
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object AppConstants {

  const val TAG = "CameraXDemo"
  const val RATIO_4_3_VALUE = 4.0 / 3.0
  const val RATIO_16_9_VALUE = 16.0 / 9.0
}

enum class Analyzer(val value: String) {
  NONE("None"),
  BARCODE("Barcode"),
  TEXT("Text"),
  OBJECT("Object");

  companion object {
    fun getAnalyzer(value: String): Analyzer = values().firstOrNull { it.value == value } ?: NONE
  }
}

fun aspectRatio(
  width: Int,
  height: Int
): Int {
  val previewRatio = max(width, height).toDouble() / min(width, height)
  if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
    return AspectRatio.RATIO_4_3
  }
  return AspectRatio.RATIO_16_9
}