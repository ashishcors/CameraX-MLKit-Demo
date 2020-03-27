package com.example.cameraxdemo.ui.home.objectDetection

import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.camera.core.ImageAnalysis
import com.example.cameraxdemo.R
import com.example.cameraxdemo.databinding.FragmentObjectDetectionBinding
import com.example.cameraxdemo.ui.base.BaseAnalyzerFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.google.firebase.ml.vision.objects.FirebaseVisionObject

class ObjectDetectionFragment : BaseAnalyzerFragment<FragmentObjectDetectionBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getInflatedViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ): FragmentObjectDetectionBinding =
    FragmentObjectDetectionBinding.inflate(inflater, container, attachToParent)

  override fun initAnalyzerUi(binding: FragmentObjectDetectionBinding) {
    setListeners(binding)
  }

  override fun getImageAnalyzer(
    screenSize: Size,
    screenAspectRatio: Int,
    screenRotation: Int
  ): ImageAnalysis? = ImageAnalysis.Builder()
      .setTargetResolution(screenSize)
      .setTargetRotation(screenRotation)
      .build()
      .apply {
        setAnalyzer(
            cameraExecutor,
            ObjectAnalyser(
                multiObjectMode
            ) { firebaseVisionObjectList ->
              createOverlays(firebaseVisionObjectList)
            }
        )
      }

  private var multiObjectMode = false

  private fun setListeners(binding: FragmentObjectDetectionBinding) {
    binding.btnDetectionMode.setOnClickListener {
      multiObjectMode = multiObjectMode.not()
      binding.btnDetectionMode.setImageDrawable(
          if (multiObjectMode)
            getDrawable(requireContext(), R.drawable.ic_filter_all)
          else
            getDrawable(requireContext(), R.drawable.ic_filter_1)
      )
      bindCameraUseCase(binding)
      binding.overlayContainer.clear()
    }
  }

  private fun createOverlays(firebaseVisionObjectList: MutableList<FirebaseVisionObject>) {
    binding?.overlayContainer?.clear()
    binding?.let { binding ->
      firebaseVisionObjectList.forEach { firebaseVisionObject ->
        val overlayView =
          ObjectOverlayView(
              binding.overlayContainer, firebaseVisionObject
          )
        binding.overlayContainer.add(overlayView)
      }
    }
  }
}

