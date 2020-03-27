package com.example.cameraxdemo.ui.home.customViews

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.example.cameraxdemo.utils.customView.GraphicOverlay
import com.example.cameraxdemo.utils.customView.GraphicOverlay.Graphic
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

class FaceOverlayView(
  overlay: GraphicOverlay,
  private val faces: MutableList<FirebaseVisionFace>
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
    face: FirebaseVisionFace
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
    val pointsToDraw: MutableList<FirebaseVisionPoint> = arrayListOf()
    pointsToDraw.apply {
      addAll(face.getContour(FirebaseVisionFaceContour.FACE).points)
      addAll(face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points)
      addAll(face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points)
      addAll(face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).points)
      addAll(face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points)
      addAll(face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points)
      addAll(face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points)
      addAll(face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points)
      addAll(face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points)
      addAll(face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).points)
      addAll(face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).points)
      addAll(face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).points)
      addAll(face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).points)
    }

    val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
    leftEar?.let {
      val leftEarPos = leftEar.position
    }

    // If classification was enabled:
    if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
      val smileProb = face.smilingProbability
    }
    if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
      val rightEyeOpenProb = face.rightEyeOpenProbability
    }

    // If face tracking was enabled:
    if (face.trackingId != FirebaseVisionFace.INVALID_ID) {
      val id = face.trackingId
    }
    pointsToDraw.forEach {
      canvas.drawPoint(translateX(it.x), it.y, paintStroke)
    }
  }
}