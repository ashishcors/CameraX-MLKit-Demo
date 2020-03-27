package com.example.cameraxdemo.ui.home

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cameraxdemo.AppConstants
import com.example.cameraxdemo.R.drawable
import com.example.cameraxdemo.databinding.ActivityHomeBinding
import com.example.cameraxdemo.ui.base.BaseActivity
import com.example.cameraxdemo.ui.home.barcode.BarcodeFragment
import com.example.cameraxdemo.ui.home.luminosity.LuminosityFragment
import com.example.cameraxdemo.utils.addImageGetOutputStream
import com.example.cameraxdemo.utils.padWithDisplayCutout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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

  private val fragments = arrayOf<Fragment>(
      BarcodeFragment(),
//      TextScanFragment(),
//      ObjectDetectionFragment(),
//      FaceDetectionFragment(),
      LuminosityFragment()
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    binding.previewView.post {
      displayId = binding.previewView.display.displayId
      buildCameraUi()
    }
    initUI()
    initViewPager()
    addListeners()
    binding.previewView.post { bindCameraUseCase() }
  }

  private fun initUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      binding.cutoutSafeArea.padWithDisplayCutout()
    }
  }

  private fun initViewPager() {
    val adapter = ViewPagerAdapter(supportFragmentManager, fragments)
    binding.viewPager.adapter = adapter
  }

  override fun onPause() {
    super.onPause()
    cameraProvider?.unbindAll()
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
    binding.btnAnalyzeBarcode.setOnClickListener {

    }

    binding.btnAnalyzeText.setOnClickListener {

    }

    binding.btnAnalyzeObject.setOnClickListener {

    }

    binding.btnAnalyzeFace.setOnClickListener {

    }

    binding.btnAnalyzeLuminosity.setOnClickListener {

    }
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

  private fun hideSystemUI() {
    window.decorView.systemUiVisibility = FLAGS_FULLSCREEN
  }

  override fun getInflatedViewBinding(): ActivityHomeBinding =
    ActivityHomeBinding.inflate(layoutInflater)

  override fun getViewModelClass() = HomeViewModel::class.java
}
