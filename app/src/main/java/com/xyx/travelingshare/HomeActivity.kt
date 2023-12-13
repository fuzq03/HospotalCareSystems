package com.xyx.travelingshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.xyx.travelingshare.Fragment.FriendListFragment
import com.xyx.travelingshare.Fragment.MyFragment
import com.xyx.travelingshare.Fragment.MyFragmentStateAdapter
import com.xyx.travelingshare.entity.User_All

class HomeActivity : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var  mBottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mViewPager = findViewById(R.id.main_viewPager)
        mBottomNavigationView = findViewById(R.id.main_bottomNavigationView)
        mViewPager.adapter = MyFragmentStateAdapter(this,initData())
        mViewPager.offscreenPageLimit = 1
        mBottomNavigationView.setOnItemSelectedListener { item ->
            // 点击Tab, 切换对应的 Fragment
            when (item.itemId) {
                R.id.fragment_1 -> {
                    // TODO 当点击 Tab 时，ViewPager 也切换到对应的 Fragment
                    mViewPager.setCurrentItem(0, true)
                    true
                }
                R.id.fragment_2 -> {
                    mViewPager.setCurrentItem(1, true)
                    true
                }
                R.id.fragment_3 -> {
                    mViewPager.setCurrentItem(2, true)
                    true
                }
                R.id.fragment_4 -> {
                    mViewPager.setCurrentItem(3, true)
                    true
                }
                else -> false
            }
        }
        mViewPager.registerOnPageChangeCallback(onPageChangeCallback)
    }
    val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            var itemID = R.id.fragment_1
            when (position) {
                0 -> {
                    itemID = R.id.fragment_1
                    Log.d("HL", "1")
                }
                1 -> {
                    itemID = R.id.fragment_2
                    Log.d("HL", "2")
                }
                2 -> {
                    itemID = R.id.fragment_3
                    Log.d("HL", "3")

                }
                3 -> {
                    itemID = R.id.fragment_4
                    Log.d("HL", "4")
                }
            }
            // TODO 当Fragment滑动改变时，底部的Tab也跟着改变
            mBottomNavigationView.selectedItemId = itemID
        }
    }
    /**
     * 初始化数据
     */
    private fun initData(): List<Fragment> {
        val mData = ArrayList<Fragment>()
        for (i in 0 until 4) {
            if(i == 2){
                mData.add(FriendListFragment.newInstance(i))
            }else{
                mData.add(MyFragment.newInstance(i))
            }
        }
        return mData
    }
}