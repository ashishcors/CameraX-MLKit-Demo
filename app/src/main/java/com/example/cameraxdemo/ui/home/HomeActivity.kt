package com.example.cameraxdemo.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cameraxdemo.AppConstants
import com.example.cameraxdemo.R.drawable
import com.example.cameraxdemo.databinding.ActivityHomeBinding
import com.example.cameraxdemo.ui.base.BaseActivity
import com.example.cameraxdemo.ui.home.customViews.FocusReticuleView
import com.example.cameraxdemo.utils.addImageGetOutputStream
import com.example.cameraxdemo.utils.padWithDisplayCutout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

  companion object {
    private const val FLAGS_FULLSCREEN =
      View.SYSTEM_UI_FLAG_LOW_PROFILE or
          View.SYSTEM_UI_FLAG_FULLSCREEN or
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
          View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  }

  private var displayId = -1
  private var lensFacing = CameraSelector.LENS_FACING_BACK
  private var preview: Preview? = null
  private var imageCapture: ImageCapture? = null
  private var camera: Camera? = null

  private lateinit var cameraExecutor: ExecutorService

  private var cameraProvider: ProcessCameraProvider? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    initUI()
    addListeners()
    setUpTapToFocus()
    binding.previewView.post {
      displayId = binding.previewView.display.displayId
      buildCameraUi()
      bindCameraUseCase()
    }
  }

  private fun initUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      binding.cutoutSafeArea.padWithDisplayCutout()
    }
  }

  override fun onResume() {
    super.onResume()
    hideSystemUI()
  }

  private fun bindCameraUseCase() {
    val metrics = DisplayMetrics().also { binding.previewView.display.getRealMetrics(it) }
    val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
    val rotation = binding.previewView.display.rotation

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener(Runnable {
      cameraProvider = cameraProviderFuture.get()
      preview = Preview.Builder()
          .setTargetResolution(screenSize)
          .setTargetRotation(rotation)
          .build()

      preview?.setSurfaceProvider(binding.previewView.previewSurfaceProvider)

      imageCapture = ImageCapture.Builder()
          .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
          .setTargetResolution(screenSize)
          .setTargetRotation(rotation)
          .build()

      cameraProvider?.let {
        if (it.isBound(preview!!))
          it.unbind(preview)
        if (it.isBound(imageCapture!!))
          it.unbind(imageCapture)
      }
      try {
        camera = cameraProvider?.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            imageCapture
        )
      } catch (e: Exception) {
        Log.e(AppConstants.TAG, "Use case binding failed", e)
      }

    }, ContextCompat.getMainExecutor(this))
  }

  private fun addListeners() {

  }

  private fun buildCameraUi() {
    binding.cameraControls.captureButton.setOnClickListener {
      imageCapture?.let { imageCapture ->
        val metadata = Metadata().apply {
          isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }
        val outputFileOptions =
          ImageCapture.OutputFileOptions.Builder(
              addImageGetOutputStream(this)
          )
              .setMetadata(metadata)
              .build()
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
              override fun onImageSaved(outputFileResults: OutputFileResults) {
                showMessage("Image Saved: $outputFileResults")
              }

              override fun onError(exception: ImageCaptureException) {
                Log.e(AppConstants.TAG, "Photo capture failed: ${exception.message}", exception)
              }
            }
        )
      }
    }

    binding.cameraControls.flipButton.setOnClickListener {
      if (lensFacing == CameraSelector.LENS_FACING_BACK) {
        lensFacing = CameraSelector.LENS_FACING_FRONT
        binding.cameraControls.flipButton.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                drawable.ic_camera_rear_black_48dp
            )
        )
      } else {
        lensFacing = CameraSelector.LENS_FACING_BACK
        binding.cameraControls.flipButton.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                drawable.ic_camera_front_black_24dp
            )
        )
      }
      bindCameraUseCase()
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setUpTapToFocus() {
    //Todo: Fix the onTouchListener, we only need tap event
    binding.previewView.setOnTouchListener { _, event ->
      if (event.action != MotionEvent.ACTION_UP) {
        return@setOnTouchListener true
      }
      val cameraControl = camera?.cameraControl
      val metrics = DisplayMetrics().also { binding.previewView.display.getRealMetrics(it) }
      val factory = SurfaceOrientedMeteringPointFactory(
          metrics.widthPixels.toFloat(), metrics.heightPixels.toFloat()
      )
      binding.overlayContainer.clear()
      binding.overlayContainer.add(
          FocusReticuleView(binding.overlayContainer, event.x, event.y)
      )
      val point = factory.createPoint(event.x, event.y)
      val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
          .setAutoCancelDuration(5, TimeUnit.SECONDS)
          .build()

      val future = cameraControl?.startFocusAndMetering(action)
      future?.addListener(Runnable {
        //can get focusing result using future.get()
        binding.overlayContainer.clear()
      }, cameraExecutor)

      return@setOnTouchListener true
    }
  }

  private fun hideSystemUI() {
    window.decorView.systemUiVisibility = FLAGS_FULLSCREEN
  }

  override fun getInflatedViewBinding(): ActivityHomeBinding =
    ActivityHomeBinding.inflate(layoutInflater)

  override fun getViewModelClass() = HomeViewModel::class.java
}
