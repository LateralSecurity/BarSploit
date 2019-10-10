package com.lateralsecurity.barsploit

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlin.Exception

fun generateBitmap(text: String, format: BarcodeFormat): Bitmap {
    return try {
        // FIXME: this works well for a page of 1D barcodes, but is horrible for 2D barcodes
        val barcode = MultiFormatWriter().encode(text, format, 512, 64)
        val encoder = BarcodeEncoder()
        encoder.createBitmap(barcode)
    } catch (e: Exception) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}