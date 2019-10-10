package com.lateralsecurity.barsploit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.data_sequence_item.view.*

// TODO: remove unused boilerplate
class BarcodeSequenceAdapter(
     private val mValues: List<BarcodeSequenceItem>,
        private val mListener: OnListInteractionListener?
    ) : RecyclerView.Adapter<BarcodeSequenceAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BarcodeSequenceItem
            mListener?.onListInteraction(item, mValues.indexOf(item))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.barcode_sequence_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }

    interface OnListInteractionListener {
        // TODO: Update argument type and name
        fun onListInteraction(item: BarcodeSequenceItem, position: Int)
    }

}