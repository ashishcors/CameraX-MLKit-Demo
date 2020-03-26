package com.example.cameraxdemo.ui.base

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.example.cameraxdemo.AppConstants.TAG
import com.example.cameraxdemo.aspectRatio
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraFragment<B : ViewBinding, VM : ViewModel, AVM : ViewModel> : BaseFragment<B, AVM>() {

  abstract fun buildCameraUi(binding: B)
  abstract fun getPreviewView(binding: B): PreviewView
  protected fun getImageAnalyzer(
    screenSize: Size,
    screenAspectRatio: Int,
    screenRotation: Int
  ): ImageAnalysis? = null

  protected fun getPreview(
    screenSize: Size,
    screenAspectRatio: Int,
    screenRotation: Int
  ): Preview? = Preview.Builder()
      .setTargetResolution(screenSize)
      .setTargetRotation(screenRotation)
      .build()

  protected fun getImageCapture(
    screenSize: Size,
    screenAspectRatio: Int,
    screenRotation: Int
  ): ImageCapture? = null

  private var displayId = -1
  private var lensFacing = CameraSelector.LENS_FACING_BACK
  private var preview: Preview? = null
  private var imageAnalyser: ImageAnalysis? = null
  private var imageCapture: ImageCapture? = null
  private var camera: Camera? = null

  private lateinit var cameraExecutor: ExecutorService

  private var cameraProvider: ProcessCameraProvider? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    binding?.let { binding ->
      binding.root.post {
        displayId = getPreviewView(binding).display.displayId
        buildCameraUi(binding)
        bindCameraUseCase(binding)
      }
    }
  }

  protected fun bindCameraUseCase(binding: B) {
    val metrics = DisplayMetrics().also { getPreviewView(binding).display.getRealMetrics(it) }
    val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
    val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
    val rotation = getPreviewView(binding).display.rotation

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
    cameraProviderFuture.addListener(Runnable {
      cameraProvider = cameraProviderFuture.get()
      preview = getPreview(screenSize, screenAspectRatio, rotation)

      preview?.setSurfaceProvider(getPreviewView(binding).previewSurfaceProvider)

      imageAnalyser = getImageAnalyzer(screenSize, screenAspectRatio, rotation)
      imageCapture = getImageCapture(screenSize, screenAspectRatio, rotation)

      cameraProvider?.unbindAll()
      val userCases = ArrayList<UseCase?>().apply {
        add(preview)
        add(imageCapture)
        add(imageAnalyser)
      }

      try {
        camera = cameraProvider?.bindToLifecycle(
            this,
            cameraSelector,
            *userCases.filterNotNull().toTypedArray()
        )
      } catch (e: Exception) {
        Log.e(TAG, "Use case binding failed", e)
      }

    }, ContextCompat.getMainExecutor(requireContext()))
  }



  override fun onPause() {
    super.onPause()
    cameraProvider?.unbindAll()
  }

}