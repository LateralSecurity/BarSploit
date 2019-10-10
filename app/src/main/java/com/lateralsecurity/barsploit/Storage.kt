package com.lateralsecurity.barsploit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.gson.Gson
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class Storage(val context: Context) {
    private val gson = Gson()

    fun readFile(filename: String): String {
        val file = context.openFileInput(filename)
        val bytes = String(file.readBytes(), Charsets.UTF_8)
        file.close()
        return bytes
    }

    fun saveFile(filename: String, data: String) {
        val file = context.openFileOutput(filename, Context.MODE_PRIVATE)
        file.write(data.toByteArray())
        file.close()
    }

    fun deleteFile(filename: String) {
        File(context.filesDir, filename).delete()
    }

    fun readGroups(): Map<String, String> {
        var groups = mapOf<String, String>()
        try {
            Log.d("readGroups", readFile("index.json"))
            groups = gson.fromJson(readFile("index.json"), Map::class.java) as Map<String, String>
        } catch (e: Exception) {
            updateGroups(groups)
        }
        return groups
    }

    fun updateGroups(map: Map<*, *>) {
        val json = gson.toJson(map).toString()
        Log.d("updateGroups", json)
        saveFile("index.json", json)
    }

    fun saveNewGroup(name: String, barcodes: BarcodeSequence): String {
        val id = UUID.randomUUID().toString()
        barcodes.id = id
        updateGroup(barcodes)
        return id
    }

    fun updateGroup(barcodes: BarcodeSequence) {
        if (barcodes.id == null) return

        val json = gson.toJson(barcodes).toString()
        Log.d("updateGroup", json)

        val filename = "group_" + barcodes.id + ".json"
        saveFile(filename, json)
        val groups = readGroups().toMutableMap()
        groups[barcodes.id ?: ""] = "group " + barcodes.id
        updateGroups(groups)
    }

    fun readGroup(id: String): BarcodeSequence? {
        return try {
            val json = readFile("group_$id.json")
            Log.d("readGroup", json)
            gson.fromJson(json, BarcodeSequence::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun exportGroup(activity: Activity, id: String, name: String?) {
        // TODO: actually implement the export...

        val json = readFile("group_$id.json")
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "barcodes_${time}_$name.json"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, filename)
        }

        // TODO: avoid magic numbers
        startActivityForResult(activity, intent, 3, null)
    }

    fun deleteGroup(id: String) {
        deleteFile("group_$id.json")
        val groups = readGroups().toMutableMap()
        groups.remove(id)
        updateGroups(groups)
    }
}