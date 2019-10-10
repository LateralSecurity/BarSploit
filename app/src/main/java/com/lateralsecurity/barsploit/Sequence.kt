package com.lateralsecurity.barsploit

public class Sequence {
    private val list = arrayListOf<SequenceObject>()

    fun add(barcode: Barcode) {
        list.add(SequenceObject(barcode))
    }

    fun add(delay: Delay) {
        list.add(SequenceObject(delay))
    }
}