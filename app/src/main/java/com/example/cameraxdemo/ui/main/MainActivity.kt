package com.example.cameraxdemo.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraxdemo.R.layout
import com.example.cameraxdemo.databinding.ActivityMainBinding
import com.example.cameraxdemo.ui.base.BaseActivity
import com.example.cameraxdemo.ui.home.HomeActivity

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

  override fun layoutId() = layout.activity_main

  override fun getViewModelClass() = MainViewModel::class.java

  companion object {
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private const val PERMISSION_CODE = 1
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestPermissions()
  }

  private fun hasAllPermissions() = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
  }

  private fun requestPermissions() {
    if (hasAllPermissions()) {
      binding.root.post { goToHome() }
    } else {
      ActivityCompat.requestPermissions(
          this,
          REQUIRED_PERMISSIONS,
          PERMISSION_CODE
      )
    }
  }

  private fun goToHome() {
    startActivity(Intent(this, HomeActivity::class.java))
    finishAffinity()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    if (requestCode == PERMISSION_CODE) {
      if (hasAllPermissions()) {
        binding.root.post { goToHome() }
      } else {
        showMessage(
            "Camera permission not granted."
        )
        finish()
      }
    }
  }
}
