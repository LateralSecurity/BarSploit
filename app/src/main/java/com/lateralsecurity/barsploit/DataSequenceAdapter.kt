package com.lateralsecurity.barsploit

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView

import kotlinx.android.synthetic.main.data_sequence_item.view.*
import org.apache.commons.codec.binary.Hex

class DataSequenceAdapter(
    private val values: ArrayList<DataSequenceObject>,
    private val listener: OnListInteractionListener?
) : RecyclerView.Adapter<DataSequenceAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DataSequenceObject
            listener?.onListInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_sequence_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.typeView.text = item.type
        holder.contentView.text = item.toString()

        for (i in 0 until holder.spinner.adapter.count)
            if (holder.spinner.adapter.getItem(i) == item.type) holder.spinner.setSelection(i)
        holder.text.setText(item.toDataString())

        holder.add.setOnClickListener {
            // FIXME: use data_types properly
            val selected = holder.spinner.selectedItem.toString()
            holder.typeView.text = selected
            if (selected == "Text") {
                item.item.stringData = holder.text.text.toString()
                item.item.byteData = null
            } else if (selected == "Bytes") {
                var text = holder.text.text.toString()
                // Each hex byte needs to be 2 chars long
                if (text.length % 2 == 1) text = text.substring(0, text.length - 1)
                item.item.byteData = Hex.decodeHex(text)
                item.item.stringData = null
            }
            item.type = selected
            item.complete = true
            updateItemType(holder, position)
            notifyItemChanged(position)
        }
        holder.deleteView.setOnClickListener {
            if (item.type != "deleted") {
                item.type = "deleted"
                values.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }
        }

        updateItemType(holder, position)

        with(holder.view) {
            tag = item
            setOnClickListener {
                item.complete = false
                updateItemType(holder, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = if (values[position].complete) 0 else 1

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val typeViewContainer: View = view.item_type_container
        val typeView: TextView = view.item_type
        val contentView: TextView = view.content
        val deleteView: ImageButton = view.delete

        val spinner: Spinner = view.spinner
        val text: EditText = view.text
        val add: ImageButton = view.add
    }

    interface OnListInteractionListener {
        // TODO: Update argument type and name
        fun onListInteraction(item: DataSequenceObject)
    }

    private fun updateItemType(holder: ViewHolder, position: Int) {
        // this is pretty horrible
        if (getItemViewType(position) == 0) {
            holder.typeViewContainer.visibility = View.VISIBLE
            holder.contentView.visibility = View.VISIBLE
            holder.spinner.visibility = View.GONE
            holder.text.visibility = View.GONE
            holder.add.visibility = View.GONE
        } else {
            holder.typeViewContainer.visibility = View.GONE
            holder.contentView.visibility = View.GONE
            holder.spinner.visibility = View.VISIBLE
            holder.text.visibility = View.VISIBLE
            holder.add.visibility = View.VISIBLE
        }
    }
}
