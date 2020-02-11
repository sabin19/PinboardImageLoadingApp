package com.sbn.netwoking.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


object Utility {
    fun isMain(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }

    private fun decodeFile(f: File): Bitmap? {
        try { //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)
            //Find the correct scale value. It should be the power of 2.
            val REQUIRED_SIZE = 70
            var width = o.outWidth
            var height = o.outHeight
            var scale = 1
            while (true) {
                if (width / 2 < REQUIRED_SIZE || height / 2 < REQUIRED_SIZE) break
                width /= 2
                height /= 2
                scale *= 2
            }
            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }
        return null
    }
}