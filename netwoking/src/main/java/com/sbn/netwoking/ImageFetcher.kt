package com.sbn.netwoking

import android.content.Context
import android.graphics.Bitmap
import com.sbn.netwoking.data.DefaultImageRepository
import com.sbn.netwoking.data.ImageRepository
import com.sbn.netwoking.data.RemoteBitmapData
import com.sbn.netwoking.domain.image.ImageDownloadUseCase
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.ImageResizer
import com.sbn.netwoking.util.NetworkUtils

/**
 * A simple subclass of [ImageResizer] that fetches and resizes images fetched from a URL.
 */
class ImageFetcher : ImageResizer {
    private val mHttpDiskCacheStarting = true


    /**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    constructor(
        context: Context,
        imageWidth: Int,
        imageHeight: Int
    ) : super(context, imageWidth, imageHeight) {
        init(context)
    }

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */
    constructor(context: Context, imageSize: Int) : super(context, imageSize) {
        init(context)
    }

    private lateinit var networkUtils: NetworkUtils

    lateinit var useCase: ImageDownloadUseCase
    private fun init(context: Context) {
        networkUtils = NetworkUtils((context))
        val repository: ImageRepository = DefaultImageRepository(RemoteBitmapData())
        useCase = ImageDownloadUseCase(networkUtils, repository)
        // checkConnection(context)
    }

    override fun clearCacheInternal() {
        super.clearCacheInternal()
    }

    override fun closeCacheInternal() {
        super.closeCacheInternal()
    }


    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    private fun processBitmap(data: String): Bitmap? {
        var bitmap: Bitmap? = null
        val response = useCase.executeNow(data)
        if (response is Response.Success) {
            bitmap = response.data
        }
        return bitmap
    }

    override fun processBitmap(data: Any?): Bitmap? {
        return processBitmap(data.toString())
    }
}