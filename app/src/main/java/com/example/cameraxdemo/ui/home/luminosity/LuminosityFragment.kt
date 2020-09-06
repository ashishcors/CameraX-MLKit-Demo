package com.example.cameraxdemo.ui.home.luminosity

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cameraxdemo.AppConstants.TAG
import com.example.cameraxdemo.R.layout
import com.example.cameraxdemo.aspectRatio
import com.example.cameraxdemo.databinding.FragmentLuminosityBinding
import com.example.cameraxdemo.ui.base.BaseFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.example.cameraxdemo.ui.home.barcode.BarcodeTextOverlayView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LuminosityFragment : BaseFragment<FragmentLuminosityBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getLayoutId() = layout.fragment_luminosity

  private var displayId = -1
  private var lensFacing = CameraSelector.LENS_FACING_BACK
  private var preview: Preview? = null
  private var imageAnalyser: ImageAnalysis? = null
  private var camera: Camera? = null

  private lateinit var cameraExecutor: ExecutorService

  private var cameraProvider: ProcessCameraProvider? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    binding?.let { binding ->
      binding.previewView.post {
        displayId = binding.previewView.display.displayId
      }
    }
  }

  override fun onPause() {
    super.onPause()
    cameraProvider?.unbindAll()
  }

  override fun onResume() {
    super.onResume()
    binding?.previewView?.post { bindCameraUseCase() }
  }

  private fun bindCameraUseCase() {
    binding?.let { binding ->
      binding.overlayContainer.clear()
      val metrics = DisplayMetrics().also { binding.previewView.display.getRealMetrics(it) }
      val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
      val rotation = binding.previewView.display.rotation

      val cameraSelector = CameraSelector.Builder()
          .requireLensFacing(lensFacing)
          .build()
      val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
      cameraProviderFuture.addListener({
        cameraProvider = cameraProviderFuture.get()
        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        preview?.setSurfaceProvider(binding.previewView.createSurfaceProvider())

        imageAnalyser =
          ImageAnalysis.Builder()
              .setTargetAspectRatio(screenAspectRatio)
              .setTargetRotation(rotation)
              .build()
              .apply {
                setAnalyzer(
                    cameraExecutor,
                    LuminosityAnalyzer { string ->
                      createOverlays(string)
                    }
                )
              }


        cameraProvider?.unbindAll()
        try {
          camera = cameraProvider?.bindToLifecycle(
              this,
              cameraSelector,
              preview,
              imageAnalyser
          )
        } catch (e: Exception) {
          Log.e(TAG, "Use case binding failed", e)
        }

      }, ContextCompat.getMainExecutor(requireContext()))
    }
  }

  private fun createOverlays(text: String) {
    binding?.let { binding ->
      binding.overlayContainer.clear()
      binding.overlayContainer.add(
          BarcodeTextOverlayView(
              binding.overlayContainer, text
          )
      )
    }
  }
}

