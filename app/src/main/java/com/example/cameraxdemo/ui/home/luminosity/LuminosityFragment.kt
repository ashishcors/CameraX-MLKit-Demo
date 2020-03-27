package com.example.cameraxdemo.ui.home.luminosity

import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.ImageAnalysis
import com.example.cameraxdemo.databinding.FragmentLuminosityBinding
import com.example.cameraxdemo.ui.base.BaseAnalyzerFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel
import com.example.cameraxdemo.ui.home.barcode.BarcodeTextOverlayView

class LuminosityFragment : BaseAnalyzerFragment<FragmentLuminosityBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getInflatedViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ): FragmentLuminosityBinding =
    FragmentLuminosityBinding.inflate(inflater, container, attachToParent)

  override fun initAnalyzerUi(binding: FragmentLuminosityBinding) {

  }

  override fun getImageAnalyzer(
    screenSize: Size,
    screenAspectRatio: Int,
    screenRotation: Int
  ): ImageAnalysis? = ImageAnalysis.Builder()
      .setTargetAspectRatio(screenAspectRatio)
      .setTargetRotation(screenRotation)
      .build()
      .apply {
        setAnalyzer(
            cameraExecutor,
            LuminosityAnalyzer { string ->
              createOverlays(string)
            }
        )
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

