package com.sbn.netwoking.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection


class RemoteBitmapData : BitmapRemoteSource {

    @WorkerThread
    override fun getRemoteData(imageUrl: String): Bitmap? {
        var bitmap: Bitmap? = null
        val url = URL(imageUrl)
        val urlConn: URLConnection = url.openConnection()
        val httpConn = urlConn as HttpURLConnection
        httpConn.connect()
        val responseCode = httpConn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = httpConn.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        }
        return bitmap
    }
}