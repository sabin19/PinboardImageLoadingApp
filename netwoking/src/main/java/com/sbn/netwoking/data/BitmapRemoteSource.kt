package com.sbn.netwoking.data

import android.graphics.Bitmap

interface BitmapRemoteSource {
    fun getRemoteData(imageUrl: String): Bitmap?
}