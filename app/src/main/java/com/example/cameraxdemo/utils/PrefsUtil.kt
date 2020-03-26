package com.example.cameraxdemo.utils

import android.content.Context
import com.example.cameraxdemo.Analyzer

class PrefsUtil(context: Context) {

  private val prefs by lazy {
    context.getSharedPreferences("cxd", Context.MODE_PRIVATE)
  }

  companion object {
    const val KEY_ANALYZER_MODE = "KEY_ANALYZER_MODE"
    const val KEY_MULTIPLE_OBJECT_DETECTION_ENABLED = "KEY_MULTIPLE_OBJECT_DETECTION_ENABLED"
  }

  var analyzer: Analyzer
    get() = Analyzer.getAnalyzer(
        prefs.getString(
            KEY_ANALYZER_MODE,
            ""
        ) ?: ""
    )
    set(value) = prefs.edit()
        .putString(
            KEY_ANALYZER_MODE, value.value)
        .apply()

  var isMultipleObjectDetectionEnabled: Boolean
    get() = prefs.getBoolean(
        KEY_MULTIPLE_OBJECT_DETECTION_ENABLED, false)
    set(value) = prefs.edit()
        .putBoolean(
            KEY_MULTIPLE_OBJECT_DETECTION_ENABLED, value)
        .apply()

}