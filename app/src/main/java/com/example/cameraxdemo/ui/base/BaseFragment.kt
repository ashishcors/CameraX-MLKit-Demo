package com.example.cameraxdemo.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

abstract class BaseFragment<B : ViewDataBinding, AVM : ViewModel> :
    Fragment() {

  protected var binding: B? = null
  protected lateinit var viewModel: AVM

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
    binding?.lifecycleOwner = this
    setupViewModel()
    return binding?.root
  }

  @LayoutRes
  abstract fun getLayoutId(): Int

  abstract fun getActivityViewModelClass(): Class<AVM>

  abstract fun getActivityViewModelOwner(): ViewModelStoreOwner

  private fun setupViewModel() {
    viewModel = ViewModelProvider(getActivityViewModelOwner())[getActivityViewModelClass()]
  }

  override fun onDestroy() {
    super.onDestroy()
    binding = null
  }

  protected fun showMessage(
    message: String?,
    length: Int = Toast.LENGTH_LONG
  ) {
    Toast.makeText(activity, message, length)
        .show()
  }

}