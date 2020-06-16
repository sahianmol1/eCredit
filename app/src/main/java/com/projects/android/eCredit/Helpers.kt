package com.projects.android.eCredit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.ByteArrayOutputStream
import java.util.*

fun bitmapToString(bitmap: Bitmap?): String? {
    var encode: String? = null
    val baos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b = baos.toByteArray()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        encode = Base64.getEncoder().encodeToString(b)
    } else {
        encode = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
    }
    return encode
}

fun stringToBitmap(string: String?): Bitmap? {
    if (string == null) {
        return null
    }
    val data = android.util.Base64.decode(string, android.util.Base64.DEFAULT)
    if (data != null) {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
    return null
}