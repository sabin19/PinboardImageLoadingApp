package com.sbn.netwoking.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.collection.LruCache
import androidx.core.graphics.BitmapCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.lang.ref.SoftReference
import java.util.*

open class ImageCache private constructor(cacheParams: ImageCacheParams) {
    private var mMemoryCache: LruCache<String, BitmapDrawable>? = null
    private var mCacheParams: ImageCacheParams? = null
    private var mReusableBitmaps: MutableSet<SoftReference<Bitmap>>? =
        null

    /**
     * Initialize the cache, providing all parameters.
     *
     * @param cacheParams The cache parameters to initialize the cache
     */
    private fun init(cacheParams: ImageCacheParams) {
        mCacheParams = cacheParams
// Set up memory cache
        if (mCacheParams!!.memoryCacheEnabled) {
            mReusableBitmaps =
                Collections.synchronizedSet(HashSet())
            mMemoryCache = object :
                LruCache<String, BitmapDrawable>(mCacheParams!!.memCacheSize) {
                /**
                 * Notify the removed entry that is no longer being cached
                 *
                 */

                override fun entryRemoved(
                    evicted: Boolean,
                    key: String,
                    oldValue: BitmapDrawable,
                    newValue: BitmapDrawable?
                ) {
                    if (oldValue is RecyclingBitmapDrawable) { // The removed entry is a recycling drawable, so notify it
// that it has been removed from the memory cache
                        oldValue.setIsCached(false)
                    } else { // The removed entry is a standard BitmapDrawable.
// Add the bitmap to a SoftReference set for possible use with inBitmap
// later.
                        (mReusableBitmaps as MutableSet<SoftReference<Bitmap>>).add(
                            SoftReference(
                                oldValue.bitmap
                            )
                        )
                    }
                }

                /**
                 * Measure item size in kilobytes rather than units which is more practical
                 * for a bitmap cache
                 */
                override fun sizeOf(key: String, value: BitmapDrawable): Int {
                    val bitmapSize =
                        getBitmapSize(value) / 1024
                    return if (bitmapSize == 0) 1 else bitmapSize
                }
            }
        }
    }

    fun addBitmapToCache(
        data: String?,
        value: BitmapDrawable?
    ) {
        if (data == null || value == null) {
            return
        }
        // Add to memory cache
        if (mMemoryCache != null) {
            if (value is RecyclingBitmapDrawable) { // The removed entry is a recycling drawable, so notify it
// that it has been added into the memory cache
                value.setIsCached(true)
            }
            mMemoryCache!!.put(data, value)
        }

    }

    /**
     * Get from memory cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap drawable if found in cache, null otherwise
     */
    fun getBitmapFromMemCache(data: String): BitmapDrawable? {
        var memValue: BitmapDrawable? = null
        if (mMemoryCache != null) {
            memValue = mMemoryCache!![data]
        }
        return memValue

    }

    fun getBitmapFromReusableSet(options: BitmapFactory.Options): Bitmap? {
        var bitmap: Bitmap? = null
        if (mReusableBitmaps != null && !mReusableBitmaps!!.isEmpty()) {
            synchronized(mReusableBitmaps!!) {
                val iterator =
                    mReusableBitmaps!!.iterator()
                var item: Bitmap?
                while (iterator.hasNext()) {
                    item = iterator.next().get()
                    if (null != item && item.isMutable) { // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(
                                item,
                                options
                            )
                        ) {
                            bitmap = item
                            // Remove from reusable set so it can't be used again
                            iterator.remove()
                            break
                        }
                    } else { // Remove from the set if the reference has been cleared.
                        iterator.remove()
                    }
                }
            }
        }
        return bitmap
    }

    fun clearCache() {
        if (mMemoryCache != null) {
            mMemoryCache!!.evictAll()
        }
    }

    /**
     * A holder class that contains cache parameters.
     */
    class ImageCacheParams {
        var memCacheSize = DEFAULT_MEM_CACHE_SIZE
        var memoryCacheEnabled =
            DEFAULT_MEM_CACHE_ENABLED

        fun setMemCacheSizePercent(percent: Float) {
            require(!(percent < 0.01f || percent > 0.8f)) {
                ("setMemCacheSizePercent - percent must be "
                        + "between 0.01 and 0.8 (inclusive)")
            }
            memCacheSize =
                Math.round(percent * Runtime.getRuntime().maxMemory() / 1024)
        }
    }

    class RetainFragment : Fragment() {
        var `object`: Any? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // Make sure this Fragment is retained over a configuration change
            retainInstance = true
        }

    }

    companion object {
        private const val TAG = "ImageCache"
        // Default memory cache size in kilobytes
        private const val DEFAULT_MEM_CACHE_SIZE = 1024 * 5 // 5MB
        // Constants to easily toggle various caches
        private const val DEFAULT_MEM_CACHE_ENABLED = true


        fun getInstance(
            fragmentManager: FragmentManager, cacheParams: ImageCacheParams
        ): ImageCache { // Search for, or create an instance of the non-UI RetainFragment
            val mRetainFragment =
                findOrCreateRetainFragment(
                    fragmentManager
                )
            var imageCache =
                mRetainFragment.`object` as ImageCache?

            if (imageCache == null) {
                imageCache = ImageCache(cacheParams)
                mRetainFragment.`object` = imageCache
            }
            return imageCache
        }

        private fun canUseForInBitmap(
            candidate: Bitmap, targetOptions: BitmapFactory.Options
        ): Boolean {
            val width = targetOptions.outWidth / targetOptions.inSampleSize
            val height = targetOptions.outHeight / targetOptions.inSampleSize
            val byteCount =
                width * height * getBytesPerPixel(
                    candidate.config
                )
            return byteCount <= candidate.allocationByteCount

        }

        /**
         * Return the byte usage per pixel of a bitmap based on its configuration.
         *
         * @param config The bitmap configuration.
         * @return The byte usage per pixel.
         */
        private fun getBytesPerPixel(config: Bitmap.Config): Int {
            if (config == Bitmap.Config.ARGB_8888) {
                return 4
            } else if (config == Bitmap.Config.RGB_565) {
                return 2
            } else if (config == Bitmap.Config.ARGB_4444) {
                return 2
            } else if (config == Bitmap.Config.ALPHA_8) {
                return 1
            }
            return 1
        }


        private fun getBitmapSize(value: BitmapDrawable): Int {
            return BitmapCompat.getAllocationByteCount(value.bitmap)
        }

        private fun findOrCreateRetainFragment(fm: FragmentManager): RetainFragment { //BEGIN_INCLUDE(find_create_retain_fragment)
// Check to see if we have retained the worker fragment.
            var mRetainFragment =
                fm.findFragmentByTag(TAG) as RetainFragment?
            // If not retained (or first time running), we need to create and add it.
            if (mRetainFragment == null) {
                mRetainFragment = RetainFragment()
                fm.beginTransaction()
                    .add(mRetainFragment, TAG)
                    .commitAllowingStateLoss()
            }
            return mRetainFragment
            //END_INCLUDE(find_create_retain_fragment)
        }
    }

    init {
        init(cacheParams)
    }
}