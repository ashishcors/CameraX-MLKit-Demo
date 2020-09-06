package com.example.cameraxdemo.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.cameraxdemo.utils.PrefsUtil
import com.example.cameraxdemo.utils.UiUtils

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

  protected lateinit var binding: B
  protected lateinit var viewModel: VM

  protected lateinit var prefsUtil: PrefsUtil
  protected lateinit var uiUtils: UiUtils

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, layoutId())
    viewModel = ViewModelProvider(this)[getViewModelClass()]
    uiUtils = UiUtils(this)
    prefsUtil = PrefsUtil(this)
  }

  @LayoutRes
  abstract fun layoutId(): Int

  abstract fun getViewModelClass(): Class<VM>

  protected fun showProgress() {
    uiUtils.showProgress(supportFragmentManager)
  }

  protected fun hideProgress() {
    uiUtils.hideProgress()
  }

  @Suppress("SameParameterValue")
  protected fun showMessage(message: String) {
    uiUtils.showMessage(message)
  }
}