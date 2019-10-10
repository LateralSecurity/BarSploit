package com.lateralsecurity.barsploit

import java.io.Serializable
import kotlin.collections.ArrayList

class BarcodeSequence(val list: ArrayList<BarcodeSequenceItem>, var id: String?, var name: String = "Untitled"): Serializable