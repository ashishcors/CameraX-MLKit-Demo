package com.example.cameraxdemo.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.cameraxdemo.R.id
import com.example.cameraxdemo.R.layout
import com.example.cameraxdemo.databinding.ActivityHomeBinding
import com.example.cameraxdemo.ui.base.BaseActivity
import com.example.cameraxdemo.utils.padWithDisplayCutout

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

  companion object {
    private const val FLAGS_FULLSCREEN =
      View.SYSTEM_UI_FLAG_LOW_PROFILE or
          View.SYSTEM_UI_FLAG_FULLSCREEN or
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
          View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  }

  private var navController: NavController? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    navController = supportFragmentManager.findFragmentById(id.nav_host_fragment)
        ?.findNavController()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      // Use extension method to pad "inside" view containing UI using display cutout's bounds
      binding.cutoutSafeArea.padWithDisplayCutout()
    }
    addListeners()
  }

  override fun onResume() {
    super.onResume()
    hideSystemUI()
  }

  private fun addListeners() {
    binding.btnAnalyzeBarcode.setOnClickListener {
      navController?.navigate(id.to_barcodeFragment)
    }

    binding.btnAnalyzeText.setOnClickListener {
      navController?.navigate(id.to_textScanFragment)
    }

    binding.btnAnalyzeObject.setOnClickListener {
      navController?.navigate(id.to_objectDetectionFragment)
    }

    binding.btnAnalyzeFace.setOnClickListener {
      navController?.navigate(id.to_faceDetectionFragment)
    }

    binding.btnAnalyzeLuminosity.setOnClickListener {
      navController?.navigate(id.to_luminosityFragment)
    }
  }

  private fun hideSystemUI() {
    window.decorView.systemUiVisibility = FLAGS_FULLSCREEN
  }

  override fun layoutId() = layout.activity_home

  override fun getViewModelClass() = HomeViewModel::class.java
}
