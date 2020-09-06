package com.example.cameraxdemo.ui.home.faceDetection

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
import com.example.cameraxdemo.R
import com.example.cameraxdemo.R.layout
import com.example.cameraxdemo.databinding.FragmentFaceDetectionBinding
import com.example.cameraxdemo.ui.base.BaseFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.google.mlkit.vision.face.Face
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceDetectionFragment : BaseFragment<FragmentFaceDetectionBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getLayoutId() = layout.fragment_face_detection

  private var displayId = -1
  private var lensFacing = CameraSelector.LENS_FACING_FRONT
  private var preview: Preview? = null
  private var imageAnalyser: ImageAnalysis? = null
  private var camera: Camera? = null

  private lateinit var cameraExecutor: ExecutorService

  private var cameraProvider: ProcessCameraProvider? = null

  private var faceAnalyserMode = FaceAnalyser.REAL_TIME

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    binding?.let { binding ->
      binding.previewView.post {
        displayId = binding.previewView.display.displayId
        binding.overlayContainer.facing = CameraSelector.LENS_FACING_FRONT
        setListeners()
      }
    }
  }

  private fun setListeners() {
    binding?.let { binding ->
      binding.btnDetectionMode.setOnClickListener {
        if (faceAnalyserMode == FaceAnalyser.REAL_TIME) {
          faceAnalyserMode = FaceAnalyser.HIGH_ACCURACY
          binding.btnDetectionMode.setImageDrawable(
              getDrawable(requireContext(), R.drawable.ic_filter_1)
          )
        } else {
          faceAnalyserMode = FaceAnalyser.REAL_TIME
          binding.btnDetectionMode.setImageDrawable(
              getDrawable(requireContext(), R.drawable.ic_filter_all)
          )
        }
        bindCameraUseCase()
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

        imageAnalyser =
          ImageAnalysis.Builder()
              .setTargetResolution(screenSize)
              .setTargetRotation(rotation)
              .build()
              .apply {
                setAnalyzer(
                    cameraExecutor,
                    FaceAnalyser(faceAnalyserMode) { faceList ->
                      createOverlays(faceList)
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

  private fun createOverlays(faceList: MutableList<Face>) {
    binding?.let { binding ->
      binding.overlayContainer.clear()
      val overlayView = FaceOverlayView(binding.overlayContainer, faceList)
      binding.overlayContainer.add(overlayView)
    }
  }
}

