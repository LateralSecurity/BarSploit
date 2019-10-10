package com.lateralsecurity.barsploit

class SequenceObject {
    private var barcode: Barcode? = null
    private var delay: Delay? = null

    constructor(obj: Barcode) {
        barcode = obj
    }

    constructor(obj: Delay) {
        delay = obj
    }
}
