package com.example.cameraxdemo.utils

import android.os.Build
import android.view.DisplayCutout
import android.view.View
import androidx.annotation.RequiresApi

/** Pad this view with the insets provided by the device cutout (i.e. notch) */
@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {

  /** Helper method that applies padding from cutout's safe insets */
  fun doPadding(cutout: DisplayCutout) = setPadding(
      cutout.safeInsetLeft,
      cutout.safeInsetTop,
      cutout.safeInsetRight,
      cutout.safeInsetBottom
  )

  // Apply padding using the display cutout designated "safe area"
  rootWindowInsets?.displayCutout?.let { doPadding(it) }

  // Set a listener for window insets since view.rootWindowInsets may not be ready yet
  setOnApplyWindowInsetsListener { _, insets ->
    insets.displayCutout?.let { doPadding(it) }
    insets
  }
}

fun View.visible(isVisible: Boolean) {
  this.visibility = if (isVisible) View.VISIBLE else View.GONE
}