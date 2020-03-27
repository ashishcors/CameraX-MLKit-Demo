package com.example.cameraxdemo.ui.home.customViews

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.example.cameraxdemo.utils.customView.GraphicOverlay
import com.example.cameraxdemo.utils.customView.GraphicOverlay.Graphic

class StringOverlayView(
  overlay: GraphicOverlay,
  private val string: String,
  private val positionX: Float,
  private val positionY: Float
) : Graphic(overlay) {

  private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.WHITE
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    typeface = Typeface.create("", Typeface.BOLD)
    textSize = 150f
  }

  override fun draw(canvas: Canvas) {
    canvas.drawText(string, positionX, positionY, paintText)
//    canvas.drawText(string, overlay.width / 2f, overlay.height / 2f, paintText)
  }

}