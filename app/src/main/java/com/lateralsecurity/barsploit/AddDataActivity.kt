package com.lateralsecurity.barsploit

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.print.PrintHelper
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

import kotlinx.android.synthetic.main.activity_add_data.*
import kotlinx.android.synthetic.main.content_add_data.*
import org.apache.commons.codec.binary.Hex
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

// TODO: use consistent activity/class names
class AddDataActivity : AppCompatActivity(), DataSequenceAdapter.OnListInteractionListener,
    AdapterView.OnItemSelectedListener {

    private val data = DataSequence(arrayListOf())
    private val adapter = DataSequenceAdapter(data.list, this)
    private var format: BarcodeFormat = BarcodeFormat.CODE_128
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)
        setSupportActionBar(toolbar)

        val sequence = intent.getSerializableExtra("sequence")
        if (sequence != null) {
            position = intent.getIntExtra("position", -1)
            format = BarcodeFormat.valueOf(intent.getStringExtra("format") ?: "")
            data.list.clear()
            data.list.addAll(sequence as ArrayList<DataSequenceObject>)
        }

        dataView.adapter = adapter
        dataView.layoutManager = LinearLayoutManager(this)
        dataView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val barcodeTypesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, BarcodeFormat.values())
        spinner.adapter = barcodeTypesAdapter
        spinner.onItemSelectedListener = this
        spinner.setSelection(BarcodeFormat.values().indexOf(format))

        update(String(data.toBytes(), Charsets.ISO_8859_1))

        action_print.setOnClickListener {
            this.also { context ->
                PrintHelper(context).apply {
                    scaleMode = PrintHelper.SCALE_MODE_FIT
                }.also { printHelper ->
                    val bitmap = generateBitmap(String(data.toBytes(), Charsets.ISO_8859_1), format)
                    printHelper.printBitmap("barcode", bitmap)
                }
            }
        }

        button.setOnClickListener {
            addItem(DataSequenceObject("", DataSequenceItem(""), false))
            with (mainView) {
                // FIXME: this doesn't work
                val lastChild = getChildAt(childCount - 1)
                val bottom = lastChild.bottom + paddingBottom
                val delta = bottom - (scrollY + height)
                smoothScrollBy(0, delta)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_data, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_accept -> {
            val intent = Intent()
            intent.putExtra("sequence", data.list)
            intent.putExtra("format", format.name)
            intent.putExtra("position", position)
            setResult(RESULT_OK, intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onListInteraction(item: DataSequenceObject) {
        Toast.makeText(this, item.item.stringData, Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        format = parent?.getItemAtPosition(position) as BarcodeFormat
        update(String(data.toBytes(), Charsets.ISO_8859_1))
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented")
    }

    private fun addItem(item: DataSequenceObject) {
        data.list.add(item)
        adapter.notifyItemInserted(data.list.size - 1)
        // attempt to store binary data in a string
        update(String(data.toBytes(), Charsets.ISO_8859_1))
    }

    private fun update(text: String) {
        try {
            val bitmap = generateBitmap(text, format)
            barcodeImage.setImageBitmap(bitmap)
        } catch (error: Exception) {
            barcodeImage.setImageDrawable(null)
        }
    }

}
