package com.lateralsecurity.barsploit

import java.io.Serializable

class DataSequence(val list: ArrayList<DataSequenceObject>): Serializable {
    override fun toString(): String {
        return list.joinToString(" ")
    }

    fun toBytes(): ByteArray {
        // there might be a better way to do this

        val byteList = arrayListOf<ByteArray>()
        var size = 0
        for (item in list) {
            val itemBytes = item.toBytes()
            size += itemBytes.size
            byteList.add(itemBytes)
        }
        val bytes = ByteArray(size)
        var i = 0
        for (item in byteList) {
            item.copyInto(bytes, i)
            i += item.size
        }
        return bytes
    }
}