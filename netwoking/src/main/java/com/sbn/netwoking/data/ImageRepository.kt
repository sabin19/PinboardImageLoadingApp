package com.sbn.netwoking.data

import android.graphics.Bitmap
import com.sbn.netwoking.domain.response.Response

interface ImageRepository {
    fun getBitMapImage(url: String): Response<Bitmap>
}

class DefaultImageRepository constructor(
    private val dataSource: BitmapRemoteSource
) : ImageRepository {
    override fun getBitMapImage(url: String): Response<Bitmap> {
        return try {
                try {
                    val bitmap = dataSource.getRemoteData(url)
                    if (bitmap != null)
                        Response.Success(bitmap)
                    else Response.Error(Exception("Something went wrong"))
                } catch (e: Exception) {
                    Response.Error(e)
                }
        } catch (e: Exception) {
            Response.Error(e)
        }
    }


}