package com.lateralsecurity.barsploit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), GroupListAdapter.OnListInteractionListener {

    private var barcodeSequenceList = arrayListOf<BarcodeSequence>()
    private val adapter = GroupListAdapter(barcodeSequenceList, this)
    private val storage = Storage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        groupList.adapter = adapter
        groupList.layoutManager = LinearLayoutManager(this)

        val tracker = SelectionTracker.Builder<String>(
            "groupListSelection",
            groupList,
            GroupItemKeyProvider(groupList),
            ItemLookup(groupList),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker

        addGroupButton.setOnClickListener {
            val intent = Intent(this, BarcodeSequenceActivity::class.java)
            startActivityForResult(intent, 1)
        }

        val groups = storage.readGroups()
        initGroups(groups)

        clearSelectionButton.setOnClickListener {
            tracker.clearSelection()
        }

        selectAllButton.setOnClickListener {
            for (position in 0 until adapter.itemCount) tracker.select(adapter.getCustomItemId(position))
        }

        deleteButton.setOnClickListener {
            for (id in tracker.selection.toList()) {
                Log.d("MainActivity", "deleting $id")
                storage.deleteGroup(id)
                tracker.deselect(id)

                updateList(null, adapter.findById(id) ?: -1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val barcodes = data?.getSerializableExtra("barcodes") as ArrayList<BarcodeSequenceItem>
            val name = data.getStringExtra("name")
            Log.d("MainActivity", "requestCode == 1")

            val barcodeSequence = BarcodeSequence(barcodes, UUID.randomUUID().toString(), name ?: "Untitled")
            storage.updateGroup(barcodeSequence)
            updateList(barcodeSequence)
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            val barcodes = data?.getSerializableExtra("barcodes") as ArrayList<BarcodeSequenceItem>
            val position = data.getIntExtra("position", -1)
            val id = data.getStringExtra("id") ?: ""
            val name = data.getStringExtra("name") ?: ""
            Log.d("MainActivity", "name=$name")

            val barcodeSequence = BarcodeSequence(barcodes, id, name)
            storage.updateGroup(barcodeSequence)
            updateList(barcodeSequence, position)
        }
    }

    override fun onListInteraction(item: BarcodeSequence, position: Int) {
        val intent = Intent(this, BarcodeSequenceActivity::class.java)
        intent.putExtra("barcodes", barcodeSequenceList[position])
        intent.putExtra("position", position)
        startActivityForResult(intent, 2)
    }

    private fun updateList(barcodes: BarcodeSequence?, position: Int = -1) {
        if (barcodes == null) {
            barcodeSequenceList.removeAt(position)
            adapter.notifyItemRemoved(position)
        } else {
            if (position == -1) {
                barcodeSequenceList.add(barcodes)
                adapter.notifyItemInserted(barcodeSequenceList.size - 1)
            } else {
                barcodeSequenceList[position] = barcodes
                adapter.notifyItemChanged(position)
            }
        }

        start.visibility = if (barcodeSequenceList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun initGroups(groups: Map<String, String>) {
        for (item in groups) {
            val group = storage.readGroup(item.key)
            if (group != null) updateList(group)
        }
    }
}