package com.example.cameraxdemo.ui.home.customViews

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.cameraxdemo.utils.customView.GraphicOverlay
import com.example.cameraxdemo.utils.customView.GraphicOverlay.Graphic

class FocusReticuleView(
  overlay: GraphicOverlay,
  private val positionX: Float,
  private val positionY: Float
) : Graphic(overlay) {

  private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.WHITE
    style = Paint.Style.STROKE
    strokeWidth = 3f
  }

  override fun draw(canvas: Canvas) {
    canvas.drawCircle(positionX, positionY, 50f, paintStroke)

    canvas.scale(2f, 2f)
//    canvas.drawText(string, overlay.width / 2f, overlay.height / 2f, paintText)
  }

}