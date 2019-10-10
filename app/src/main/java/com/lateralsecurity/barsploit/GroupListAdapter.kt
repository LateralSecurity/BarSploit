package com.lateralsecurity.barsploit

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_list_item.view.*

class GroupListAdapter(
    private val mValues: List<BarcodeSequence>,
    private val mListener: OnListInteractionListener?
) : RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    var tracker: SelectionTracker<String>? = null

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BarcodeSequence
            mListener?.onListInteraction(item, mValues.indexOf(item))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.name

        tracker?.let {
            // Android Studio lies, the fallback string is not optional
            holder.bind(item.name ?: "<no name>", it.isSelected(item.id))
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    fun getCustomItemId(position: Int): String {
        return mValues[position].id ?: ""
    }

    fun findById(id: String): Int? {
        for (item in mValues) {
            if (item.id == id) {
                return mValues.indexOf(item)
            }
        }
        return null
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = (itemView.tag as BarcodeSequence).id
            }

        fun bind(value: String, isActivated: Boolean = false) {
            mContentView.text = value
            mView.isActivated = isActivated
        }
    }

    interface OnListInteractionListener {
        // TODO: Update argument type and name
        fun onListInteraction(item: BarcodeSequence, position: Int)
    }

}