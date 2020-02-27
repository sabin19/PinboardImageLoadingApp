package com.sbn.netwoking.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * A simple subclass of [ImageWorker] that resizes images from resources given a target width
 * and height. Useful for when the input images might be too large to simply load directly into
 * memory.
 */
open class ImageResizer : ImageWorker {
    protected var mImageWidth = 0
    protected var mImageHeight = 0

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    constructor(context: Context?, imageWidth: Int, imageHeight: Int) : super(
        context!!
    ) {
        setImageSize(imageWidth, imageHeight)
    }

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */
    constructor(context: Context?, imageSize: Int) : super(context!!) {
        setImageSize(imageSize)
    }

    /**
     * Set the target image width and height.
     *
     * @param width
     * @param height
     */
    fun setImageSize(width: Int, height: Int) {
        mImageWidth = width
        mImageHeight = height
    }

    /**
     * Set the target image size (width and height will be the same).
     *
     * @param size
     */
    fun setImageSize(size: Int) {
        setImageSize(size, size)
    }

    /**
     * The main processing method. This happens in a background task. In this case we are just
     * sampling down the bitmap and returning it from a resource.
     *
     * @param resId
     * @return
     */
    private fun processBitmap(resId: Int): Bitmap {
        return decodeSampledBitmapFromResource(
            mResources, resId, mImageWidth,
            mImageHeight, imageCache
        )
    }

    override fun processBitmap(data: Any?): Bitmap? {
        return processBitmap(data.toString().toInt())
    }

    companion object {

        fun decodeSampledBitmapFromResource(
            res: Resources?, resId: Int,
            reqWidth: Int, reqHeight: Int, cache: ImageCache?
        ): Bitmap { // BEGIN_INCLUDE (read_bitmap_dimensions)
// First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, options)
            // Calculate inSampleSize
            options.inSampleSize =
                calculateInSampleSize(options, reqWidth, reqHeight)
            // END_INCLUDE (read_bitmap_dimensions)
// Try to use inBitmap
            addInBitmapOptions(options, cache)
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(res, resId, options)
        }

        private fun addInBitmapOptions(
            options: BitmapFactory.Options,
            cache: ImageCache?
        ) {
            options.inMutable = true
            if (cache != null) { // Try and find a bitmap to use for inBitmap
                val inBitmap = cache.getBitmapFromReusableSet(options)
                if (inBitmap != null) {
                    options.inBitmap = inBitmap
                }
            }
            //END_INCLUDE(add_bitmap_options)
        }


        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while (halfHeight / inSampleSize > reqHeight
                    && halfWidth / inSampleSize > reqWidth
                ) {
                    inSampleSize *= 2
                }
                var totalPixels = width * height / inSampleSize.toLong()
                // Anything more than 2x the requested pixels we'll sample down further
                val totalReqPixelsCap = reqWidth * reqHeight * 2.toLong()
                while (totalPixels > totalReqPixelsCap) {
                    inSampleSize *= 2
                    totalPixels /= 2
                }
            }
            return inSampleSize

        }
    }
}