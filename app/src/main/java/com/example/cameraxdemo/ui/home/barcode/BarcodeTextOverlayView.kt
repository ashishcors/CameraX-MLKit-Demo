package com.example.cameraxdemo.ui.home.barcode

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.example.cameraxdemo.utils.customView.GraphicOverlay
import com.example.cameraxdemo.utils.customView.GraphicOverlay.Graphic

class BarcodeTextOverlayView(
  overlay: GraphicOverlay,
  private val string: String
) : Graphic(overlay) {

  private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    typeface = Typeface.create("", Typeface.BOLD)
    textSize = 30f
  }

  override fun draw(canvas: Canvas) {
    canvas.drawText(string, overlay.width / 2f, overlay.height / 2f, paintText)
  }

}