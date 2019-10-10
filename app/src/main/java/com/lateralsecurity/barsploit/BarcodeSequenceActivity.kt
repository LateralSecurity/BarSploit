package com.lateralsecurity.barsploit

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.print.PrintHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.BarcodeFormat

import kotlinx.android.synthetic.main.activity_barcode_sequence.*
import kotlinx.android.synthetic.main.content_barcode_sequence.*
import kotlin.collections.ArrayList
import android.graphics.Bitmap
import android.text.Html
import android.util.Base64
import android.view.View
import kotlinx.android.synthetic.main.content_barcode_sequence.start
import java.io.ByteArrayOutputStream
import java.util.*
import androidx.recyclerview.widget.DividerItemDecoration

class BarcodeSequenceActivity : AppCompatActivity(), BarcodeSequenceAdapter.OnListInteractionListener {

    private var barcodeSequence: BarcodeSequence? = null
    private var adapter: BarcodeSequenceAdapter? = null
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_sequence)
        setSupportActionBar(toolbar)

        val sequence = intent.getSerializableExtra("barcodes")
        if (sequence == null) {
            barcodeSequence = BarcodeSequence(arrayListOf(), UUID.randomUUID().toString())
        } else {
            position = intent.getIntExtra("position", -1)
            barcodeSequence = sequence as BarcodeSequence
        }

        start.visibility = if (sequence == null) View.VISIBLE else View.GONE

        adapter = BarcodeSequenceAdapter(barcodeSequence?.list ?: arrayListOf(), this)
        barcodeList.adapter = adapter
        barcodeList.layoutManager = LinearLayoutManager(this)

        barcodeList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        nameText.text.clear()
        nameText.text.append(barcodeSequence!!.name)

        addBarcodeButton.setOnClickListener {
            val intent = Intent(this, AddDataActivity::class.java)
            startActivityForResult(intent, 1)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.barcode_sequence, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_print -> {
            this.also { context ->
                PrintHelper(context).apply {
                    scaleMode = PrintHelper.SCALE_MODE_FIT
                }.also { printHelper ->
                    // TODO: separate this better and improve the page layout

                    // move HTML to resources?
                    val bodyTemplate = "<style>.barcode { text-align: center; } .barcode img { padding: .1em; max-width: 10cm; }</style>{items}"
                    val itemTemplate = "<div class='barcode'><img src='{url}'><br>{text}</div>"

                    val itemsHtml = StringBuilder()

                    for (barcodeItem in barcodeSequence!!.list) {
                        val barcode = barcodeItem.barcode ?: continue
                        val bitmap = generateBitmap(String(barcode.data.toBytes(), Charsets.ISO_8859_1), barcode.format)

                        val output = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                        val image = "data:image/png;base64," + Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)

                        itemsHtml.append(itemTemplate.replace("{url}", image).replace("{text}", Html.escapeHtml(barcodeItem.barcode.toString())))
                    }

                    val bodyHtml = bodyTemplate.replace("{items}", itemsHtml.toString())

                    Print(this).printHtml(bodyHtml)
                }
            }
            true
        }
        R.id.action_export -> {
            val id = barcodeSequence?.id
            if (id != null) Storage(this).exportGroup(this, id, nameText.text.toString())
            true
        }
        R.id.action_accept -> {
            // TODO: put all this in one object
            barcodeSequence!!.name = nameText.text.toString()
            intent.putExtra("position", position)
            intent.putExtra("barcodes", barcodeSequence!!.list)
            intent.putExtra("name", barcodeSequence!!.name)
            intent.putExtra("id", barcodeSequence!!.id)
            setResult(RESULT_OK, intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val sequence = data?.getSerializableExtra("sequence") as ArrayList<DataSequenceObject>
            val format = BarcodeFormat.valueOf(data.getStringExtra("format") ?: "")
            val barcode = Barcode(format, DataSequence(sequence))
            barcodeSequence!!.list.add(BarcodeSequenceItem(barcode))
            adapter!!.notifyItemInserted(barcodeSequence!!.list.size - 1)
            // TODO: move list handling to separate method
            start.visibility = if (barcodeSequence!!.list.isEmpty()) View.VISIBLE else View.GONE
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            val sequence = data?.getSerializableExtra("sequence") as ArrayList<DataSequenceObject>
            val format = BarcodeFormat.valueOf(data.getStringExtra("format") ?: "")
            val position = data.getIntExtra("position", -1)
            val barcode = Barcode(format, DataSequence(sequence))
            barcodeSequence!!.list[position] =  BarcodeSequenceItem(barcode)
            adapter!!.notifyItemChanged(position)
        }
    }

    override fun onListInteraction(item: BarcodeSequenceItem, position: Int) {
        // TODO: handle delays

        val intent = Intent(this, AddDataActivity::class.java)
        intent.putExtra("sequence", item.barcode?.data?.list)
        intent.putExtra("format", item.barcode?.format.toString())
        intent.putExtra("position", position)
        startActivityForResult(intent, 2)
    }

}