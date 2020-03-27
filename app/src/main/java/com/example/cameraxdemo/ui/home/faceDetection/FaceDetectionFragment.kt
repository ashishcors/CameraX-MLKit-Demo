package com.example.cameraxdemo.ui.home.faceDetection

import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import com.example.cameraxdemo.R
import com.example.cameraxdemo.databinding.FragmentFaceDetectionBinding
import com.example.cameraxdemo.ui.base.BaseAnalyzerFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.google.firebase.ml.vision.face.FirebaseVisionFace

class FaceDetectionFragment : BaseAnalyzerFragment<FragmentFaceDetectionBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getInflatedViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ): FragmentFaceDetectionBinding =
    FragmentFaceDetectionBinding.inflate(inflater, container, attachToParent)

  override fun initAnalyzerUi(binding: FragmentFaceDetectionBinding) {
    binding.overlayContainer.facing = CameraSelector.LENS_FACING_FRONT
    setListeners(binding)
  }

  override fun getImageAnalyzer(
    screenSize: Size,
    screenAspectRatio: Int,
    screenRotation: Int
  ): ImageAnalysis? {
    return ImageAnalysis.Builder()
        .setTargetResolution(screenSize)
        .setTargetRotation(screenRotation)
        .build()
        .apply {
          setAnalyzer(
              cameraExecutor,
              FaceAnalyser(faceAnalyserMode) { firebaseVisionFaceList ->
                createOverlays(firebaseVisionFaceList)
              }
          )
        }
  }

  private var faceAnalyserMode = FaceAnalyser.REAL_TIME

  private fun setListeners(binding: FragmentFaceDetectionBinding) {
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
      bindCameraUseCase(binding)
    }

  }

  private fun createOverlays(firebaseVisionFaceList: MutableList<FirebaseVisionFace>) {
    binding?.let { binding ->
      binding.overlayContainer.clear()
      val overlayView =
        FaceOverlayView(
            binding.overlayContainer, firebaseVisionFaceList
        )
      binding.overlayContainer.add(overlayView)
    }
  }
}

