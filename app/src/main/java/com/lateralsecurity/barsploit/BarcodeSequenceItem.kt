package com.lateralsecurity.barsploit

import java.io.Serializable

class BarcodeSequenceItem : Serializable {
    var barcode: Barcode? = null
    var delay: Delay? = null

    constructor(barcode: Barcode) {
        this.barcode = barcode
    }

    constructor(delay: Delay) {
        this.delay = delay
    }

    override fun toString(): String {
        return barcode?.toString() ?: delay.toString()
    }
}