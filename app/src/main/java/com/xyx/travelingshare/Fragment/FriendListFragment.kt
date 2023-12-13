package com.xyx.travelingshare.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.entity.node.BaseNode
import com.xyx.travelingshare.R
import com.xyx.travelingshare.entity.node_section.ItemNode
import com.xyx.travelingshare.entity.node_section.RootNode

class FriendListFragment:Fragment() {
    companion object {
        private const val TAG = "FriendListFragment"
        private const val ARG_POSITION = "Position"

        fun newInstance(position: Int): FriendListFragment {
            val bundle = Bundle().apply {
                putInt(ARG_POSITION, position)
            }
            val fragment = FriendListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var list = mutableListOf<BaseNode>()

    private val mAdapter by lazy {
        NodeSectionAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_type_list, container, false)
        initView(view)
        initData()
        return view
    }

    private fun initView(view: View) {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val rvList = view.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = layoutManager
        rvList.adapter = mAdapter
    }

    private fun initData() {
        setResources()
        mAdapter.setList(list)
    }

    private fun setResources() {
        disableGroup()
    }

    private fun disableGroup() {
        /**
         * 添加好友列表内容
         */
        val item_friend1 = ItemNode(0,"好友1")
        val item_friend2 = ItemNode(0,"好友2")
        val items = mutableListOf<BaseNode>()
        items.add(item_friend1)
        items.add(item_friend2)
        val entity_friend = RootNode(items,"我的好友")
        entity_friend.isExpanded = true
        list.add(entity_friend)
        /**
         * 添加组队内容
         */
        val item_queue1 = ItemNode(0,"小队1")
        val item_queue2 = ItemNode(0,"小队2")
        val items1 = mutableListOf<BaseNode>()
        items1.add(item_queue1)
        items1.add(item_queue2)
        val entity_queue = RootNode(items1,"我的组队")
        entity_friend.isExpanded = true
        list.add(entity_queue)
    }

    fun goBack(v: View?) {
        requireActivity().finish()
    }
}