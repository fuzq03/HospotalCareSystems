package com.xyx.travelingshare.Fragment

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.xyx.travelingshare.Fragment.provider.RootNodeProvider
import com.xyx.travelingshare.Fragment.provider.SecondNodeProvider
import com.xyx.travelingshare.entity.node_section.RootNode

class NodeSectionAdapter:BaseNodeAdapter() {
    init {
        addFullSpanNodeProvider(RootNodeProvider())
        addNodeProvider(SecondNodeProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        val node = data[position]
        return if(node is RootNode){
            0
        }else{
            1
        }
    }
}