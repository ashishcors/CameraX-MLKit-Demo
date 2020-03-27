package com.example.cameraxdemo.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraxdemo.databinding.LayoutViewPagerItemBinding
import com.example.cameraxdemo.ui.home.ViewPagerAdapter.ViewPagerHolder

class ViewPagerAdapter(
  private val fragmentManager: FragmentManager,
  private val fragments: Array<Fragment>
) : RecyclerView.Adapter<ViewPagerHolder>() {

  inner class ViewPagerHolder(
    val binding: LayoutViewPagerItemBinding
  ) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewPagerHolder {
    val binding = LayoutViewPagerItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
    return ViewPagerHolder(binding)
  }

  override fun getItemCount(): Int = fragments.size

  override fun onBindViewHolder(
    holder: ViewPagerHolder,
    position: Int
  ) {
    fragmentManager.beginTransaction()
        .replace(holder.binding.frameLayout.id, fragments[holder.adapterPosition])
        .commit()
  }

}