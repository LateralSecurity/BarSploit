package com.lateralsecurity.barsploit

import org.apache.commons.codec.binary.Hex
import java.io.Serializable

class DataSequenceObject(private val display: String, var item: DataSequenceItem, var complete: Boolean = true, var type: String = "String"): Serializable {
    override fun toString(): String {
        if (display.isNotEmpty()) return display
        return item.toString()
    }

    fun toDataString(): String {
        return item.stringData ?: Hex.encodeHexString(item.byteData)
    }

    fun toBytes(): ByteArray {
        return item.byteData ?: item.stringData?.toByteArray() ?: ByteArray(0)
    }
}