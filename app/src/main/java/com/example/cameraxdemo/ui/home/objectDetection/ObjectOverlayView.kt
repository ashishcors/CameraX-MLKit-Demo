package com.example.cameraxdemo.ui.home.objectDetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.cameraxdemo.utils.customView.GraphicOverlay
import com.example.cameraxdemo.utils.customView.GraphicOverlay.Graphic
import com.google.mlkit.vision.objects.DetectedObject

class ObjectOverlayView(
  overlay: GraphicOverlay,
  private val firebaseVisionObject: DetectedObject
) : Graphic(overlay) {

  private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.argb(70, 255, 255, 255)
    style = Paint.Style.FILL
  }

  private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.GRAY
    style = Paint.Style.STROKE
    strokeWidth = 3f
  }

  override fun draw(canvas: Canvas) {
    val rect = overlay.translateRect(firebaseVisionObject.boundingBox)
    canvas.drawRect(rect, paintFill)
    canvas.drawRect(rect, paintStroke)
  }

}