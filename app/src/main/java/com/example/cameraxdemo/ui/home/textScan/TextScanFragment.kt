package com.example.cameraxdemo.ui.home.textScan

import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.ImageAnalysis
import com.example.cameraxdemo.databinding.FragmentTextScanBinding
import com.example.cameraxdemo.ui.base.BaseAnalyzerFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.google.firebase.ml.vision.text.FirebaseVisionText

class TextScanFragment : BaseAnalyzerFragment<FragmentTextScanBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getInflatedViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ): FragmentTextScanBinding = FragmentTextScanBinding.inflate(inflater, container, attachToParent)

  override fun initAnalyzerUi(binding: FragmentTextScanBinding) {

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
            TextAnalyser { string ->
              createOverlays(string)
            }
        )
      }

  private fun createOverlays(firebaseVisionText: FirebaseVisionText) {
    binding?.let { binding ->
      binding.overlayContainer.clear()
      firebaseVisionText.textBlocks.forEach { textBlock ->
        val overlayView =
          TextOverlayView(
              binding.overlayContainer, textBlock
          )
        binding.overlayContainer.add(overlayView)
      }
    }
  }
}

