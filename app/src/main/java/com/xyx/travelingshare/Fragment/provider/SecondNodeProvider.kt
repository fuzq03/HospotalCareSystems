package com.xyx.travelingshare.Fragment.provider

import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xyx.travelingshare.R
import com.xyx.travelingshare.entity.node_section.ItemNode

class SecondNodeProvider:BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1
    override val layoutId: Int
        get() = R.layout.item_section_list_content

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        if(item == null) return
        val entity:ItemNode = item as ItemNode
        helper.itemView.run{
            findViewById<TextView>(R.id.header).text = entity.name
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        Toast.makeText(context,"$position",Toast.LENGTH_SHORT).show()
    }
}