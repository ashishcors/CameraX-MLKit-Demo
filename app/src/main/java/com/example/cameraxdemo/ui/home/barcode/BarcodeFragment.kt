package com.example.cameraxdemo.ui.home.barcode

import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.ImageAnalysis
import com.example.cameraxdemo.databinding.FragmentBarcodeBinding
import com.example.cameraxdemo.ui.base.BaseAnalyzerFragment
import com.example.cameraxdemo.ui.home.HomeActivity
import com.example.cameraxdemo.ui.home.HomeViewModel

class BarcodeFragment : BaseAnalyzerFragment<FragmentBarcodeBinding, HomeViewModel>() {

  override fun getActivityViewModelClass() = HomeViewModel::class.java
  override fun getActivityViewModelOwner() = (activity as HomeActivity)
  override fun getInflatedViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ): FragmentBarcodeBinding = FragmentBarcodeBinding.inflate(inflater, container, attachToParent)

  override fun initAnalyzerUi(binding: FragmentBarcodeBinding) {

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
            QRCodeAnalyser { string ->
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

