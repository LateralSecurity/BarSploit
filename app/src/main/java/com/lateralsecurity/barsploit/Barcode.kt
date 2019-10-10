package com.lateralsecurity.barsploit

import com.google.zxing.BarcodeFormat
import java.io.Serializable

class Barcode(var format: BarcodeFormat, var data: DataSequence): Serializable {
    override fun toString(): String {
        return data.toString()
    }
}