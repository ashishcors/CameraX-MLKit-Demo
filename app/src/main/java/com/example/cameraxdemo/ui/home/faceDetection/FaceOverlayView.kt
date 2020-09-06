package com.example.cameraxdemo.ui.home.faceDetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import com.example.cameraxdemo.utils.customView.GraphicOverlay
import com.example.cameraxdemo.utils.customView.GraphicOverlay.Graphic
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour

class FaceOverlayView(
  overlay: GraphicOverlay,
  private val faces: MutableList<Face>
) : Graphic(overlay) {

  private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.argb(70, 255, 255, 255)
    style = Paint.Style.FILL
  }

  private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.GREEN
    style = Paint.Style.STROKE
    strokeWidth = 5f
  }
  private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    style = Paint.Style.FILL
    textAlign = Paint.Align.CENTER
    typeface = Typeface.create("", Typeface.BOLD)
    textSize = 20f
  }

  override fun draw(canvas: Canvas) {
    faces.forEach {
      drawFace(canvas, it)
    }
  }

  private fun drawFace(
    canvas: Canvas,
    face: Face
  ) {
    val x = translateX(
        face.boundingBox
            .centerX()
            .toFloat()
    )
    val y = translateY(
        face.boundingBox
            .centerY()
            .toFloat()
    )
    // Draws a bounding box around the face.
    val xOffset = scaleX(
        face.boundingBox
            .width() / 2.0f
    )
    val yOffset = scaleY(
        face.boundingBox
            .height() / 2.0f
    )
    val left: Float = x - xOffset
    val top: Float = y - yOffset
    val right: Float = x + xOffset
    val bottom: Float = y + yOffset
    canvas.drawRect(left, top, right, bottom, paintFill)

    // If contour detection was enabled:
    val pointsToDraw: MutableList<PointF> = arrayListOf()
    pointsToDraw.apply {
      addAll(face.getContour(FaceContour.FACE)?.points.orEmpty())
      addAll(face.getContour(FaceContour.LEFT_EYE)?.points.orEmpty())
      addAll(face.getContour(FaceContour.RIGHT_EYE)?.points.orEmpty())
      addAll(face.getContour(FaceContour.LOWER_LIP_TOP)?.points.orEmpty())
      addAll(face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points.orEmpty())
      addAll(face.getContour(FaceContour.UPPER_LIP_TOP)?.points.orEmpty())
      addAll(face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points.orEmpty())
      addAll(face.getContour(FaceContour.NOSE_BOTTOM)?.points.orEmpty())
      addAll(face.getContour(FaceContour.NOSE_BRIDGE)?.points.orEmpty())
      addAll(face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)?.points.orEmpty())
      addAll(face.getContour(FaceContour.LEFT_EYEBROW_TOP)?.points.orEmpty())
      addAll(face.getContour(FaceContour.RIGHT_EYEBROW_TOP)?.points.orEmpty())
      addAll(face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM)?.points.orEmpty())
    }

//    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
//    leftEar?.let {
//      val leftEarPos = leftEar.position
//    }

//    // If classification was enabled:
//    if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
//      val smileProb = face.smilingProbability
//    }
//    if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
//      val rightEyeOpenProb = face.rightEyeOpenProbability
//    }

//    // If face tracking was enabled:
//    if (face.trackingId != Face.INVALID_ID) {
//      val id = face.trackingId
//    }
    pointsToDraw.forEach {
      canvas.drawPoint(translateX(it.x), it.y, paintStroke)
    }
  }
}