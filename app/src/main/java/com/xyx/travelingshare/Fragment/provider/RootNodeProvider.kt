package com.xyx.travelingshare.Fragment.provider

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xyx.travelingshare.entity.node_section.RootNode
import com.xyx.travelingshare.ext.rotation
import com.xyx.travelingshare.R

class RootNodeProvider:BaseNodeProvider() {
    override val itemViewType: Int
        get() = 0
    override val layoutId: Int
        get() = R.layout.def_section_head

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val entity:RootNode = item as RootNode
        helper.setText(R.id.header,entity.title)
        setArrowSpin(helper,item,false)
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        getAdapter()!!.expandOrCollapse(position)
    }
    private fun setArrowSpin(helper: BaseViewHolder, data: BaseNode, isAnimate: Boolean) {
        val entity: RootNode = data as RootNode
        val imageView = helper.getView<ImageView>(R.id.ivIcon)
        if (entity.isExpanded) {
            imageView.rotation(0f, isAnimate)
        } else {
            imageView.rotation(90f, isAnimate)
        }
    }
}