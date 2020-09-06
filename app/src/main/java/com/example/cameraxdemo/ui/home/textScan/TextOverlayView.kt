package com.example.cameraxdemo.ui.home.textScan

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Typeface
import androidx.core.graphics.toPointF
import com.example.cameraxdemo.utils.customView.GraphicOverlay
import com.example.cameraxdemo.utils.customView.GraphicOverlay.Graphic
import com.google.mlkit.vision.text.Text.TextBlock

class TextOverlayView(
  overlay: GraphicOverlay,
  private val textBlock: TextBlock
) : Graphic(overlay) {

  companion object {
    const val PADDING = 5
  }

  private val path = Path()

  private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.argb(70, 255, 255, 255)
    style = Paint.Style.FILL
  }

  private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.GRAY
    style = Paint.Style.STROKE
    strokeWidth = 3f
  }

  private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    typeface = Typeface.create("", Typeface.BOLD)
  }

  private fun Array<Point>.toPointFArray() = Array(this.size) { this[it].toPointF() }

  private fun avg(
    float1: Float,
    float2: Float
  ): Float = (float1 + float2) / 2f

  override fun draw(canvas: Canvas) {
    textBlock.cornerPoints?.toPointFArray()
        ?.also { points ->
          path.moveTo(points[0].x - PADDING, points[0].y - PADDING)
          path.lineTo(points[1].x + PADDING, points[1].y - PADDING)
          path.lineTo(points[2].x + PADDING, points[2].y + PADDING)
          path.lineTo(points[3].x - PADDING, points[3].y + PADDING)
          path.lineTo(points[0].x - PADDING, points[0].y - PADDING)
          canvas.drawPath(path, paintFill)
          canvas.drawPath(path, paintStroke)
        }
  }

}