package com.example.cameraxdemo.utils.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector
import java.util.ArrayList

class GraphicOverlay(
  context: Context,
  attrs: AttributeSet
) : View(context, attrs) {

  /**
   * Base class for a custom graphics object to be rendered within the graphic overlay. Subclass
   * this and implement the [Graphic.draw] method to define the graphics element. Add
   * instances to the overlay using [GraphicOverlay.add].
   */
  abstract class Graphic protected constructor(protected val overlay: GraphicOverlay) {
    protected val context: Context = overlay.context

    /** Draws the graphic on the supplied canvas.  */
    abstract fun draw(canvas: Canvas)

    /**
     * Adjusts a horizontal value of the supplied value from the preview scale to the view scale.
     */
    open fun scaleX(horizontal: Float): Float {
      return horizontal * overlay.widthScaleFactor
    }

    /**
     * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
     */
    open fun scaleY(vertical: Float): Float {
      return vertical * overlay.heightScaleFactor
    }

    /**
     * Adjusts the x coordinate from the preview's coordinate system to the view coordinate system.
     */
    open fun translateX(x: Float): Float {
      return if (overlay.facing == CameraSelector.LENS_FACING_FRONT) {
        overlay.width - scaleX(x)
      } else {
        scaleX(x)
      }
    }

    /**
     * Adjusts the y coordinate from the preview's coordinate system to the view coordinate system.
     */
    open fun translateY(y: Float): Float {
      return scaleY(y)
    }

    open fun postInvalidate() {
      overlay.postInvalidate()
    }
  }

  private val lock = Any()

  private var previewWidth: Int = 0
  private var widthScaleFactor = 1.0f
  private var previewHeight: Int = 0
  private var heightScaleFactor = 1.0f
  private val graphics = ArrayList<Graphic>()
  var facing = CameraSelector.LENS_FACING_BACK

  /** Removes all graphics from the overlay.  */
  fun clear() {
    synchronized(lock) {
      graphics.clear()
    }
    postInvalidate()
  }

  /** Adds a graphic to the overlay.  */
  fun add(graphic: Graphic) {
    synchronized(lock) {
      graphics.add(graphic)
    }
  }

  fun translateX(x: Float): Float = x * widthScaleFactor
  fun translateY(y: Float): Float = y * heightScaleFactor

  /**
   * Adjusts the `rect`'s coordinate from the preview's coordinate system to the view
   * coordinate system.
   */
  fun translateRect(rect: Rect) = RectF(
      translateX(rect.left.toFloat()),
      translateY(rect.top.toFloat()),
      translateX(rect.right.toFloat()),
      translateY(rect.bottom.toFloat())
  )

  /** Draws the overlay with its associated graphic objects.  */
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    if (previewWidth > 0 && previewHeight > 0) {
      widthScaleFactor = width.toFloat() / previewWidth
      heightScaleFactor = height.toFloat() / previewHeight
    }

    synchronized(lock) {
      graphics.forEach { it.draw(canvas) }
    }
  }

}