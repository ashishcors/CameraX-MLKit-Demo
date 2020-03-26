package com.example.cameraxdemo.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding, AVM : ViewModel> :
    Fragment() {
  protected var binding: B? = null
  protected lateinit var viewModel: AVM

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = getInflatedViewBinding(inflater, container)
    bindContentView()
    return binding?.root
  }

  private fun bindContentView() {
    viewModel =
      ViewModelProvider(getActivityViewModelOwner())[getActivityViewModelClass()]
  }

  override fun onDestroy() {
    super.onDestroy()
    binding = null
  }

  abstract fun getActivityViewModelClass(): Class<AVM>

  abstract fun getActivityViewModelOwner(): ViewModelStoreOwner

  protected fun showMessage(
    message: String?,
    length: Int = Toast.LENGTH_LONG
  ) {
    Toast.makeText(activity, message, length)
        .show()
  }

  abstract fun getInflatedViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean = false
  ): B

}