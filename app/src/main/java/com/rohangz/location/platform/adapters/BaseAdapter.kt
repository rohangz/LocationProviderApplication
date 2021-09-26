package com.rohangz.location.platform.adapters

import androidx.recyclerview.widget.RecyclerView
import com.rohangz.location.platform.viewholders.BaseViewHolder

abstract class BaseAdapter(
    val itemList: ArrayList<Any>
): RecyclerView.Adapter<BaseViewHolder>() {

    final override fun getItemCount(): Int {
        return itemList.size
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindData(position, itemList[position])
    }

}