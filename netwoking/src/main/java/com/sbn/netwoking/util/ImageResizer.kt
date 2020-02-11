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
        private const val TAG = "ImageResizer"
        /**
         * Decode and sample down a bitmap from resources to the requested width and height.
         *
         * @param res       The resources object containing the image data
         * @param resId     The resource id of the image data
         * @param reqWidth  The requested width of the resulting bitmap
         * @param reqHeight The requested height of the resulting bitmap
         * @param cache     The ImageCache used to find candidate bitmaps for use with inBitmap
         * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
         * that are equal to or greater than the requested width and height
         */
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

        /**
         * Decode and sample down a bitmap from a file to the requested width and height.
         *
         * @param filename  The full path of the file to decode
         * @param reqWidth  The requested width of the resulting bitmap
         * @param reqHeight The requested height of the resulting bitmap
         * @param cache     The ImageCache used to find candidate bitmaps for use with inBitmap
         * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
         * that are equal to or greater than the requested width and height
         */
        fun decodeSampledBitmapFromFile(
            filename: String?,
            reqWidth: Int, reqHeight: Int, cache: ImageCache?
        ): Bitmap { // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filename, options)
            // Calculate inSampleSize
            options.inSampleSize =
                calculateInSampleSize(options, reqWidth, reqHeight)
            // Try to use inBitmap
            addInBitmapOptions(options, cache)
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(filename, options)
        }

        /**
         * Decode and sample down a bitmap from a file input stream to the requested width and height.
         *
         * @param fileDescriptor The file descriptor to read from
         * @param reqWidth       The requested width of the resulting bitmap
         * @param reqHeight      The requested height of the resulting bitmap
         * @param cache          The ImageCache used to find candidate bitmaps for use with inBitmap
         * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
         * that are equal to or greater than the requested width and height
         */
        fun decodeSampledBitmapFromByteArray(
            inputStream: ByteArray,
            reqWidth: Int,
            reqHeight: Int,
            cache: ImageCache?
        ): Bitmap? { // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val mBitmap = BitmapFactory.decodeByteArray(inputStream, 0, inputStream.size, options)
            // Calculate inSampleSize

            options.inSampleSize =
                calculateInSampleSize(options, reqWidth, reqHeight)
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            // Try to use inBitmap
            addInBitmapOptions(options, cache)
            return BitmapFactory.decodeByteArray(inputStream, 0, inputStream.size, options)
        }

        private fun addInBitmapOptions(
            options: BitmapFactory.Options,
            cache: ImageCache?
        ) { //BEGIN_INCLUDE(add_bitmap_options)
// inBitmap only works with mutable bitmaps so force the decoder to
// return mutable bitmaps.
            options.inMutable = true
            if (cache != null) { // Try and find a bitmap to use for inBitmap
                val inBitmap = cache.getBitmapFromReusableSet(options)
                if (inBitmap != null) {
                    options.inBitmap = inBitmap
                }
            }
            //END_INCLUDE(add_bitmap_options)
        }

        /**
         * Calculate an inSampleSize for use in a [BitmapFactory.Options] object when decoding
         * bitmaps using the decode* methods from [BitmapFactory]. This implementation calculates
         * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
         * having a width and height equal to or larger than the requested width and height.
         *
         * @param options   An options object with out* params already populated (run through a decode*
         * method with inJustDecodeBounds==true
         * @param reqWidth  The requested width of the resulting bitmap
         * @param reqHeight The requested height of the resulting bitmap
         * @return The value to be used for inSampleSize
         */
        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
        ): Int { // BEGIN_INCLUDE (calculate_sample_size)
// Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
// height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > reqHeight
                    && halfWidth / inSampleSize > reqWidth
                ) {
                    inSampleSize *= 2
                }
                // This offers some additional logic in case the image has a strange
// aspect ratio. For example, a panorama may have a much larger
// width than height. In these cases the total pixels might still
// end up being too large to fit comfortably in memory, so we should
// be more aggressive with sample down the image (=larger inSampleSize).
                var totalPixels = width * height / inSampleSize.toLong()
                // Anything more than 2x the requested pixels we'll sample down further
                val totalReqPixelsCap = reqWidth * reqHeight * 2.toLong()
                while (totalPixels > totalReqPixelsCap) {
                    inSampleSize *= 2
                    totalPixels /= 2
                }
            }
            return inSampleSize
            // END_INCLUDE (calculate_sample_size)
        }
    }
}