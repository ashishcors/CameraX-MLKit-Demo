package com.example.cameraxdemo.utils.customView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.cameraxdemo.databinding.LayoutCustomProgressDialogBinding

class CustomProgressDialog : DialogFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = LayoutCustomProgressDialogBinding.inflate(inflater, container, false)
    return binding.root
  }

}