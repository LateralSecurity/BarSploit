package com.lateralsecurity.barsploit

import com.google.common.base.Splitter
import org.apache.commons.codec.binary.Hex
import java.io.Serializable

class DataSequenceItem: Serializable {
    var stringData: String? = null
    var byteData: ByteArray? = null

    constructor(data: String) {
        stringData = data
    }

    constructor(data: ByteArray) {
        byteData = data
    }

    override fun toString(): String {
        val bytes = byteData
        if (bytes != null) {
            return if (bytes.isEmpty()) "[]" else Splitter.fixedLength(2).split(Hex.encodeHexString(bytes, false)).joinToString(", 0x", "[0x", "]")
        }
        return stringData ?: ""
    }
}