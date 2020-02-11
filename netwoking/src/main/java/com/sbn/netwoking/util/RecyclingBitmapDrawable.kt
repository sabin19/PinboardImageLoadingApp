package com.sbn.netwoking.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable

/**
 * A BitmapDrawable that keeps track of whether it is being displayed or cached.
 * When the drawable is no longer being displayed or cached,
 * [recycle()][Bitmap.recycle] will be called on this drawable's bitmap.
 */
class RecyclingBitmapDrawable(res: Resources?, bitmap: Bitmap?) :
    BitmapDrawable(res, bitmap) {
    private var mCacheRefCount = 0
    private var mDisplayRefCount = 0
    private var mHasBeenDisplayed = false
    /**
     * Notify the drawable that the displayed state has changed. Internally a
     * count is kept so that the drawable knows when it is no longer being
     * displayed.
     *
     * @param isDisplayed - Whether the drawable is being displayed or not
     */
    fun setIsDisplayed(isDisplayed: Boolean) { //BEGIN_INCLUDE(set_is_displayed)
        synchronized(this) {
            if (isDisplayed) {
                mDisplayRefCount++
                mHasBeenDisplayed = true
            } else {
                mDisplayRefCount--
            }
        }
        // Check to see if recycle() can be called
        checkState()
        //END_INCLUDE(set_is_displayed)
    }

    /**
     * Notify the drawable that the cache state has changed. Internally a count
     * is kept so that the drawable knows when it is no longer being cached.
     *
     * @param isCached - Whether the drawable is being cached or not
     */
    fun setIsCached(isCached: Boolean) { //BEGIN_INCLUDE(set_is_cached)
        synchronized(this) {
            if (isCached) {
                mCacheRefCount++
            } else {
                mCacheRefCount--
            }
        }
        // Check to see if recycle() can be called
        checkState()
        //END_INCLUDE(set_is_cached)
    }

    @Synchronized
    private fun checkState() { //BEGIN_INCLUDE(check_state)
// If the drawable cache and display ref counts = 0, and this drawable
// has been displayed, then recycle
        if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed
            && hasValidBitmap()
        ) {
            bitmap.recycle()
        }
        //END_INCLUDE(check_state)
    }

    @Synchronized
    private fun hasValidBitmap(): Boolean {
        val bitmap = bitmap
        return bitmap != null && !bitmap.isRecycled
    }

    companion object {
        const val TAG = "CountingBitmapDrawable"
    }
}