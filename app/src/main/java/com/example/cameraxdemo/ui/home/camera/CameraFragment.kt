package com.example.cameraxdemo.ui.home.camera

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cameraxdemo.AppConstants.TAG
import com.example.cameraxdemo.R.drawable
import com.example.cameraxdemo.R.layout
import com.example.cameraxdemo.databinding.FragmentCameraBinding
import com.example.cameraxdemo.ui.base.BaseFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.example.cameraxdemo.utils.addImageGetOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : BaseFragment<FragmentCameraBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getLayoutId() = layout.fragment_camera

  private var displayId = -1
  private var lensFacing = CameraSelector.LENS_FACING_BACK
  private var preview: Preview? = null
  private var imageCapture: ImageCapture? = null
  private var camera: Camera? = null

  private lateinit var cameraExecutor: ExecutorService

  private var cameraProvider: ProcessCameraProvider? = null

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    addListener()
    binding?.let { binding ->
      binding.previewView.post {
        displayId = binding.previewView.display.displayId
        buildCameraUi()
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

  private fun addListener() {
    binding?.let { binding ->
      binding.cameraControls.flashButton.setOnClickListener {

      }
    }
  }

  private fun buildCameraUi() {
    binding?.cameraControls?.captureButton?.setOnClickListener {
      imageCapture?.let { imageCapture ->
        val metadata = Metadata().apply {
          isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }
        val outputFileOptions =
          ImageCapture.OutputFileOptions.Builder(addImageGetOutputStream(requireContext()))
              .setMetadata(metadata)
              .build()
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
              override fun onImageSaved(outputFileResults: OutputFileResults) {
                showMessage("Image Saved: $outputFileResults")
              }

              override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
              }
            }
        )
      }
    }

    binding?.cameraControls?.flipButton?.setOnClickListener {
      if (lensFacing == CameraSelector.LENS_FACING_BACK) {
        lensFacing = CameraSelector.LENS_FACING_FRONT
        binding?.cameraControls?.flipButton?.setImageDrawable(
            getDrawable(
                requireContext(),
                drawable.ic_camera_rear_black_48dp
            )
        )
      } else {
        lensFacing = CameraSelector.LENS_FACING_BACK
        binding?.cameraControls?.flipButton?.setImageDrawable(
            getDrawable(
                requireContext(),
                drawable.ic_camera_front_black_24dp
            )
        )
      }
      bindCameraUseCase()
    }
  }

  private fun bindCameraUseCase() {
//    binding.container.systemUiVisibility = FLAGS_FULLSCREEN
    binding?.let { binding ->
      val metrics = DisplayMetrics().also { binding.previewView.display.getRealMetrics(it) }
      val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
//      val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
      val rotation = binding.previewView.display.rotation

      val cameraSelector = CameraSelector.Builder()
          .requireLensFacing(lensFacing)
          .build()
      val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
      cameraProviderFuture.addListener({
        cameraProvider = cameraProviderFuture.get()
        preview = Preview.Builder()
            .setTargetResolution(screenSize)
            .setTargetRotation(rotation)
            .build()

        preview?.setSurfaceProvider(binding.previewView.createSurfaceProvider())

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(screenSize)
            .setTargetRotation(rotation)
            .build()


        cameraProvider?.unbindAll()
        try {
          camera = cameraProvider?.bindToLifecycle(
              this,
              cameraSelector,
              preview,
              imageCapture
          )
        } catch (e: Exception) {
          Log.e(TAG, "Use case binding failed", e)
        }

      }, ContextCompat.getMainExecutor(requireContext()))
    }
  }
}

