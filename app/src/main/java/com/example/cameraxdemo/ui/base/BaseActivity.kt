package com.example.cameraxdemo.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.cameraxdemo.utils.PrefsUtil
import com.example.cameraxdemo.utils.UiUtils

abstract class BaseActivity<B : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

  protected lateinit var binding: B
  protected lateinit var viewModel: VM

  protected lateinit var prefsUtil: PrefsUtil
  protected lateinit var uiUtils: UiUtils

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = getInflatedViewBinding()
    setContentView(binding.root)
    viewModel = ViewModelProvider(this)[getViewModelClass()]
    uiUtils = UiUtils(this)
    prefsUtil = PrefsUtil(this)
  }

  protected fun showProgress() {
    uiUtils.showProgress(supportFragmentManager)
  }

  protected fun hideProgress() {
    uiUtils.hideProgress()
  }

  abstract fun getInflatedViewBinding(): B

  abstract fun getViewModelClass(): Class<VM>

  protected fun showMessage(message: String) {
    uiUtils.showMessage(message)
  }
}