package com.example.cameraxdemo.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.cameraxdemo.utils.customView.CustomProgressDialog

class UiUtils (private val context: Context) {

  private val customProgressDialog = CustomProgressDialog()

  fun showMessage(
    message: String,
    length: Int = Toast.LENGTH_SHORT
  ) {
    Toast.makeText(context, message, length)
        .show()
  }

  fun showProgress(manager: FragmentManager) {
    if (customProgressDialog.isAdded.not() || customProgressDialog.isVisible.not())
      customProgressDialog.show(manager, "customProgress")
  }

  fun hideProgress() {
    if (customProgressDialog.isAdded && customProgressDialog.isVisible)
      customProgressDialog.dismiss()
  }
}