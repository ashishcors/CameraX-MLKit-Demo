package com.example.cameraxdemo.ui.home.objectDetection

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.cameraxdemo.AppConstants.TAG
import com.example.cameraxdemo.R.drawable
import com.example.cameraxdemo.R.layout
import com.example.cameraxdemo.databinding.FragmentObjectDetectionBinding
import com.example.cameraxdemo.ui.base.BaseFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.google.mlkit.vision.objects.DetectedObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ObjectDetectionFragment : BaseFragment<FragmentObjectDetectionBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getLayoutId() = layout.fragment_object_detection

  private var displayId = -1
  private var lensFacing = CameraSelector.LENS_FACING_BACK
  private var preview: Preview? = null
  private var imageAnalyser: ImageAnalysis? = null
  private var camera: Camera? = null

  private lateinit var cameraExecutor: ExecutorService

  private var cameraProvider: ProcessCameraProvider? = null

  private var multiObjectMode = false

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    binding?.let { binding ->
      binding.previewView.post {
        displayId = binding.previewView.display.displayId
        setListeners()
      }
    }
  }

  private fun setListeners() {
    binding?.let { binding ->
      binding.btnDetectionMode.setOnClickListener {
        multiObjectMode = multiObjectMode.not()
        binding.btnDetectionMode.setImageDrawable(
            if (multiObjectMode)
              getDrawable(requireContext(), drawable.ic_filter_all)
            else
              getDrawable(requireContext(), drawable.ic_filter_1)
        )
        bindCameraUseCase()
        binding.overlayContainer.clear()
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
      val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
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

        imageAnalyser = ImageAnalysis.Builder()
            .setTargetResolution(screenSize)
            .setTargetRotation(rotation)
            .build()
            .apply {
              setAnalyzer(
                  cameraExecutor,
                  ObjectAnalyser(multiObjectMode) { objectList ->
                    createOverlays(objectList)
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

  private fun createOverlays(objectList: MutableList<DetectedObject>) {
    binding?.overlayContainer?.clear()
    binding?.let { binding ->
      objectList.forEach { firebaseVisionObject ->
        val overlayView =
          ObjectOverlayView(
              binding.overlayContainer, firebaseVisionObject
          )
        binding.overlayContainer.add(overlayView)
      }
    }
  }

}

