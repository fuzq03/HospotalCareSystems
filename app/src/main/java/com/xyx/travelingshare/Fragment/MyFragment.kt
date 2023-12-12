package com.xyx.travelingshare.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MyFragment : Fragment() {

    companion object {
        private const val TAG = "MyFragment"
        private const val ARG_POSITION = "Position"

        fun newInstance(position: Int): MyFragment {
            val bundle = Bundle().apply {
                putInt(ARG_POSITION, position)
            }
            val fragment = MyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabIndex = arguments?.getInt(ARG_POSITION)
        Log.d(TAG, "$tabIndex fragment onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}