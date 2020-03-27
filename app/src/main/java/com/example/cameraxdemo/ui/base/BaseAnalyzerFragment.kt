package com.example.cameraxdemo.ui.base

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.example.cameraxdemo.AppConstants.TAG
import com.example.cameraxdemo.aspectRatio
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseAnalyzerFragment<B : ViewBinding, AVM : ViewModel> : BaseFragment<B, AVM>() {

  abstract fun initAnalyzerUi(binding: B)
  abstract fun getImageAnalyzer(
    screenSize: Size,
    screenAspectRatio: Int,
    screenRotation: Int
  ): ImageAnalysis?

  private var lensFacing = CameraSelector.LENS_FACING_BACK
  private var imageAnalyser: ImageAnalysis? = null
  private var camera: Camera? = null

  protected lateinit var cameraExecutor: ExecutorService

  private var cameraProvider: ProcessCameraProvider? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    binding?.let { binding ->
      binding.root.post {
        initAnalyzerUi(binding)
//        bindCameraUseCase(binding)
      }
    }
  }

  protected fun bindCameraUseCase(binding: B) {
    val metrics = DisplayMetrics().also { binding.root.display.getRealMetrics(it) }
    val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
    val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
    val rotation = binding.root.display.rotation

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
    cameraProviderFuture.addListener(Runnable {
      cameraProvider = cameraProviderFuture.get()

      imageAnalyser = getImageAnalyzer(screenSize, screenAspectRatio, rotation)

      cameraProvider?.let {
        if (it.isBound(imageAnalyser!!))
          it.unbind(imageAnalyser)
      }
      try {
        camera = cameraProvider?.bindToLifecycle(
            this,
            cameraSelector,
            imageAnalyser
        )
      } catch (e: Exception) {
        Log.e(TAG, "Use case binding failed", e)
      }

    }, ContextCompat.getMainExecutor(requireContext()))
  }

//  override fun onPause() {
//    super.onPause()
//    cameraProvider?.unbind(imageAnalyser)
//  }

  override fun onResume() {
    super.onResume()
    binding?.let {
      it.root.post { bindCameraUseCase(it) }
    }
  }

}