package com.xyx.travelingshare.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.entity.node.BaseNode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xyx.travelingshare.entity.Friend
import com.xyx.travelingshare.entity.node_section.ItemNode
import com.xyx.travelingshare.entity.node_section.RootNode
import com.xyx.travelingshare.utils.HttpPostRequest
import com.xyx.travelingshare.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException

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
        initData()
        initView(view)
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
        fetchAllTypes()
    }

    private fun fetchAllTypes() {
        val url = "http://192.168.8.26:8080/friend/getTypeCount"
        val requestBody = FormBody.Builder()
            .build()
        HttpPostRequest().okhttpPost(url, requestBody,object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "post请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body()?.string()
                if (json != null && json.isNotEmpty()) {
                    val gson = Gson()
                    val typeList = gson.fromJson<List<String>>(json, object : TypeToken<List<String>>() {}.type)
                    for (type in typeList) {
                        fetchDataByType(type)
                    }
                }
            }
        })
    }

    private fun fetchDataByType(type: String) {
        val url = "http://192.168.8.26:8080/friend/getByType"
        val requestBody = FormBody.Builder()
            .add("type", type)
            .build()
        HttpPostRequest().okhttpPost(url, requestBody, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "post请求失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body()?.string()
                if (json != null && json.isNotEmpty()) {
                    val gson = Gson()
                    val friendListType = object : TypeToken<List<Friend>>() {}.type
                    val friendList = gson.fromJson<List<Friend>>(json, friendListType)
                    Log.d("HomeActivity","$friendList")
                    val items = mutableListOf<BaseNode>()
                    for (friend in friendList) {
                        val item = ItemNode(0, friend.friend_name,friend.id)
                        items.add(item)
                    }
                    val entity_friend = RootNode(items, type)
                    entity_friend.isExpanded = true
                    list.add(entity_friend)

                    requireActivity().runOnUiThread {
                        mAdapter.setList(list)
                    }
                }
            }
        })
    }
}