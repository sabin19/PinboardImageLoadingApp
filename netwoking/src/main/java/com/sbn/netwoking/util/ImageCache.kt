/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sbn.netwoking.util

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION_CODES
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
        //BEGIN_INCLUDE(init_memory_cache)
// Set up memory cache
        if (mCacheParams!!.memoryCacheEnabled) { // If we're running on Honeycomb or newer, create a set of reusable bitmaps that can be
// populated into the inBitmap field of BitmapFactory.Options. Note that the set is
// of SoftReferences which will actually not be very effective due to the garbage
// collector being aggressive clearing Soft/WeakReferences. A better approach
// would be to use a strongly references bitmaps, however this would require some
// balancing of memory usage between this set and the bitmap LruCache. It would also
// require knowledge of the expected size of the bitmaps. From Honeycomb to JellyBean
// the size would need to be precise, from KitKat onward the size would just need to
// be the upper bound (due to changes in how inBitmap can re-use bitmaps).
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
        //END_INCLUDE(init_memory_cache)
    }

    /**
     * Adds a bitmap to both memory and disk cache.
     *
     * @param data  Unique identifier for the bitmap to store
     * @param value The bitmap drawable to store
     */
    fun addBitmapToCache(
        data: String?,
        value: BitmapDrawable?
    ) { //BEGIN_INCLUDE(add_bitmap_to_cache)
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
        //END_INCLUDE(add_bitmap_to_cache)
    }

    /**
     * Get from memory cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap drawable if found in cache, null otherwise
     */
    fun getBitmapFromMemCache(data: String): BitmapDrawable? { //BEGIN_INCLUDE(get_bitmap_from_mem_cache)
        var memValue: BitmapDrawable? = null
        if (mMemoryCache != null) {
            memValue = mMemoryCache!![data]
        }
        return memValue
        //END_INCLUDE(get_bitmap_from_mem_cache)
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    fun getBitmapFromReusableSet(options: BitmapFactory.Options): Bitmap? { //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
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
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    /**
     * Clears both the memory and disk cache associated with this ImageCache object. Note that
     * this includes disk access so this should not be executed on the main/UI thread.
     */
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

        /**
         * Sets the memory cache size based on a percentage of the max available VM memory.
         * Eg. setting percent to 0.2 would set the memory cache to one fifth of the available
         * memory. Throws [IllegalArgumentException] if percent is < 0.01 or > .8.
         * memCacheSize is stored in kilobytes instead of bytes as this will eventually be passed
         * to construct a LruCache which takes an int in its constructor.
         *
         *
         * This value should be chosen carefully based on a number of factors
         * Refer to the corresponding Android Training class for more discussion:
         * http://developer.android.com/training/displaying-bitmaps/
         *
         * @param percent Percent of available app memory to use to size memory cache
         */
        fun setMemCacheSizePercent(percent: Float) {
            require(!(percent < 0.01f || percent > 0.8f)) {
                ("setMemCacheSizePercent - percent must be "
                        + "between 0.01 and 0.8 (inclusive)")
            }
            memCacheSize =
                Math.round(percent * Runtime.getRuntime().maxMemory() / 1024)
        }
    }

    /**
     * A simple non-UI Fragment that stores a single Object and is retained over configuration
     * changes. It will be used to retain the ImageCache object.
     */
    class RetainFragment
    /**
     * Empty constructor as per the Fragment documentation
     */
        : Fragment() {
        /**
         * Get the stored object.
         *
         * @return The stored object
         */
        /**
         * Store a single object in this Fragment.
         *
         * @param object The object to store
         */
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

        /**
         * Return an [ImageCache] instance. A [RetainFragment] is used to retain the
         * ImageCache object across configuration changes such as a change in device orientation.
         *
         * @param fragmentManager The fragment manager to use when dealing with the retained fragment.
         * @param cacheParams     The cache parameters to use if the ImageCache needs instantiation.
         * @return An existing retained ImageCache object or a new one if one did not exist
         */
        fun getInstance(
            fragmentManager: FragmentManager, cacheParams: ImageCacheParams
        ): ImageCache { // Search for, or create an instance of the non-UI RetainFragment
            val mRetainFragment =
                findOrCreateRetainFragment(
                    fragmentManager
                )
            // See if we already have an ImageCache stored in RetainFragment
            var imageCache =
                mRetainFragment.`object` as ImageCache?
            // No existing ImageCache, create one and store it in RetainFragment
            if (imageCache == null) {
                imageCache = ImageCache(cacheParams)
                mRetainFragment.`object` = imageCache
            }
            return imageCache
        }

        /**
         * @param candidate     - Bitmap to check
         * @param targetOptions - Options that have the out* value populated
         * @return true if `candidate` can be used for inBitmap re-use with
         * `targetOptions`
         */
        @TargetApi(VERSION_CODES.KITKAT)
        private fun canUseForInBitmap(
            candidate: Bitmap, targetOptions: BitmapFactory.Options
        ): Boolean { //BEGIN_INCLUDE(can_use_for_inbitmap)
// From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
// is smaller than the reusable bitmap candidate allocation byte count.
            val width = targetOptions.outWidth / targetOptions.inSampleSize
            val height = targetOptions.outHeight / targetOptions.inSampleSize
            val byteCount =
                width * height * getBytesPerPixel(
                    candidate.config
                )
            return byteCount <= candidate.allocationByteCount
            //END_INCLUDE(can_use_for_inbitmap)
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

        private fun bytesToHexString(bytes: ByteArray): String { // http://stackoverflow.com/questions/332079
            val sb = StringBuilder()
            for (b in bytes) {
                val hex = Integer.toHexString(0xFF and b.toInt())
                if (hex.length == 1) {
                    sb.append('0')
                }
                sb.append(hex)
            }
            return sb.toString()
        }

        /**
         * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat)
         * onward this returns the allocated memory size of the bitmap which can be larger than the
         * actual bitmap data byte count (in the case it was re-used).
         */
        private fun getBitmapSize(value: BitmapDrawable): Int {
            return BitmapCompat.getAllocationByteCount(value.bitmap)
        }

        /**
         * Locate an existing instance of this Fragment or if not found, create and
         * add it using FragmentManager.
         *
         * @param fm The FragmentManager manager to use.
         * @return The existing instance of the Fragment or the new instance if just
         * created.
         */
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

    /**
     * Create a new ImageCache object using the specified parameters. This should not be
     * called directly by other classes, instead use
     * [ImageCache.getInstance] to
     * fetch an ImageCache instance.
     *
     * @param cacheParams The cache parameters to use to initialize the cache
     */
    init {
        init(cacheParams)
    }
}