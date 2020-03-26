package com.example.cameraxdemo.ui.home.barcode

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BarcodeOverlayView(
  context: Context,
  attributeSet: AttributeSet
) : View(context, attributeSet) {

  private val boxPaint: Paint = Paint().apply {
    color = Color.parseColor("#40000000")
    style = Style.STROKE
    strokeWidth = 5f
  }

  private val scrimPaint: Paint = Paint().apply {
    color = Color.parseColor("#99000000")
  }

  private val eraserPaint: Paint = Paint().apply {
    strokeWidth = boxPaint.strokeWidth
    xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
  }

  private val boxCornerRadius: Float = 20f

  val boxRect: RectF by lazy { getBarcodeReticleBox() }

  private fun getBarcodeReticleBox(): RectF {
    val overlayWidth = measuredWidth.toFloat()
    val overlayHeight = measuredHeight.toFloat()
    val boxWidth = overlayWidth * 80 / 100
    val boxHeight = overlayHeight * 30 / 100
    val cx = overlayWidth / 2
    val cy = overlayHeight / 2
    return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
    // As the stroke is always centered, so erase twice with FILL and STROKE respectively to clear
    // all area that the box rect would occupy.
    eraserPaint.style = Style.FILL
    canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint)
    eraserPaint.style = Style.STROKE
    canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint)
    // Draws the box.
    canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, boxPaint)
  }
}