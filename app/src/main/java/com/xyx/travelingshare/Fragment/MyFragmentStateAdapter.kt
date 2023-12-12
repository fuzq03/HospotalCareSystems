package com.xyx.travelingshare.Fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragmentStateAdapter(fragmentActivity: FragmentActivity, private val mData: List<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return mData[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}