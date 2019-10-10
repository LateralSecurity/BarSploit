package com.lateralsecurity.barsploit

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class GroupItemKeyProvider(private val recyclerView: RecyclerView): ItemKeyProvider<String>(SCOPE_MAPPED) {

    override fun getKey(position: Int): String {
        return (recyclerView.adapter as GroupListAdapter).getCustomItemId(position)
    }

    override fun getPosition(key: String): Int {
        val pos = (recyclerView.adapter as GroupListAdapter).findById(key)
        return pos ?: RecyclerView.NO_POSITION
    }
}