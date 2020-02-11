package com.sbn.netwoking.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sbn.netwoking.util.ImageCache.ImageCacheParams
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock

/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
abstract class ImageWorker protected constructor(context: Context) {
    /**
     * @return The [ImageCache] object currently being used by this ImageWorker.
     */
    protected var imageCache: ImageCache? = null
    private var mImageCacheParams: ImageCacheParams? = null
    private var mLoadingBitmap: Bitmap? = null
    private var mErrorBitmap: Bitmap? = null
    private var mFadeInBitmap = true
    private var mExitTasksEarly = false
    protected var mPauseWork = false
    //private val mPauseWorkLock = Any()
    private val lock = ReentrantLock()
    private val mPauseWorkLock = lock.newCondition()
    @JvmField
    protected var mResources: Resources = context.resources
    /**
     * Load an image specified by the data parameter into an ImageView (override
     * [ImageWorker.processBitmap] to define the processing logic). A memory and
     * disk cache will be used if an [ImageCache] has been added using
     * [ImageWorker.addImageCache]. If the
     * image is found in the memory cache, it is set immediately, otherwise an
     * will be created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     * @param listener A listener that will be called back once the image has been loaded.
     */
    /**
     * Load an image specified by the data parameter into an ImageView (override
     * [ImageWorker.processBitmap] to define the processing logic). A memory and
     * disk cache will be used if an [ImageCache] has been added using
     * [ImageWorker.addImageCache]. If the
     * image is found in the memory cache, it is set immediately, otherwise an [AsyncTask]
     * will be created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    @JvmOverloads
    fun loadImage(
        data: Any?,
        imageView: ImageView,
        listener: OnImageLoadedListener? = null
    ) {
        if (data == null) {
            return
        }
        var value: BitmapDrawable? = null
        if (imageCache != null) {
            value = imageCache!!.getBitmapFromMemCache(data.toString())
        }
        if (value != null) { // Bitmap found in memory cache
            imageView.setImageResource(0)
            imageView.setImageDrawable(value)
            listener?.onImageLoaded(true)
        } else if (cancelPotentialWork(
                data,
                imageView
            )
        ) { //BEGIN_INCLUDE(execute_background_task)
            val task = BitmapWorkerTask(data, imageView, listener)
            val asyncDrawable = AsyncDrawable(mResources, mLoadingBitmap, task)
            imageView.setImageResource(0)
            imageView.setImageDrawable(asyncDrawable)
            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
// framework and slightly modified. Refer to the docs at the top of the class
// for more info on what was changed.
            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR)
            //END_INCLUDE(execute_background_task)
        }
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    fun setLoadingImage(bitmap: Bitmap?) {
        mLoadingBitmap = bitmap
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    fun setLoadingImage(resId: Int) {
        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId)
    }

    fun setErrorImage(resId: Int) {
        mErrorBitmap = BitmapFactory.decodeResource(mResources, resId)
    }

    /**
     * Adds an [ImageCache] to this [ImageWorker] to handle disk and memory bitmap
     * caching.
     * @param fragmentManager
     * @param cacheParams The cache parameters to use for the image cache.
     */
    fun addImageCache(
        fragmentManager: FragmentManager?,
        cacheParams: ImageCacheParams?
    ) {
        mImageCacheParams = cacheParams
        imageCache =
            fragmentManager?.let {
                mImageCacheParams?.let { it1 ->
                    ImageCache.getInstance(
                        it,
                        it1
                    )
                }
            }
        CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE)
    }

    /**
     * Adds an [ImageCache] to this [ImageWorker] to handle disk and memory bitmap
     * caching.
     * @param activity
     * @param diskCacheDirectoryName See
     */
    private fun addImageCache(
        activity: FragmentActivity
    ) {
        mImageCacheParams = ImageCacheParams()
        imageCache = ImageCache.getInstance(
            activity.supportFragmentManager,
            mImageCacheParams!!
        )
        CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE)
    }

    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     */
    fun setImageFadeIn(fadeIn: Boolean) {
        mFadeInBitmap = fadeIn
    }

    fun setExitTasksEarly(exitTasksEarly: Boolean) {
        mExitTasksEarly = exitTasksEarly
        setPauseWork(false)
    }

    /**
     * Subclasses should override this to define any processing or work that must happen to produce
     * the final bitmap. This will be executed in a background thread and be long running. For
     * example, you could resize a large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by
     * [ImageWorker.loadImage]
     * @return The processed bitmap
     */
    protected abstract fun processBitmap(data: Any?): Bitmap?

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private inner class BitmapWorkerTask :
        AsyncTask<Void?, Void?, BitmapDrawable?> {
        var mData: Any
        private val imageViewReference: WeakReference<ImageView>
        private val mOnImageLoadedListener: OnImageLoadedListener?

        constructor(data: Any, imageView: ImageView) {
            mData = data
            imageViewReference = WeakReference(imageView)
            mOnImageLoadedListener = null
        }

        constructor(
            data: Any,
            imageView: ImageView,
            listener: OnImageLoadedListener?
        ) {
            mData = data
            imageViewReference = WeakReference(imageView)
            mOnImageLoadedListener = listener
        }

        /**
         * Background processing.
         */
        override fun doInBackground(vararg params: Void?): BitmapDrawable? { //BEGIN_INCLUDE(load_bitmap_in_background)
            val dataString = mData.toString()
            var bitmap: Bitmap? = null
            var drawable: BitmapDrawable? = null
            // Wait here if work is paused and the task is not cancelled
            synchronized(mPauseWorkLock) {
                while (mPauseWork && !isCancelled) {
                    try {
                        mPauseWorkLock.await()
                    } catch (e: InterruptedException) {
                    }
                }
            }
            // If the bitmap was not found in the cache and this task has not been cancelled by
// another thread and the ImageView that was originally bound to this task is still
// bound back to this task and our "exit early" flag is not set, then call the main
// process method (as implemented by a subclass)
            if (!isCancelled && attachedImageView != null && !mExitTasksEarly) {
                bitmap = processBitmap(mData)
            }
            // If the bitmap was processed and the image cache is available, then add the processed
// bitmap to the cache for future use. Note we don't check if the task was cancelled
// here, if it was, and the thread is still running, we may as well add the processed
// bitmap to our cache as it might be used again in the future
            if (bitmap != null) {
                drawable = BitmapDrawable(mResources, bitmap)
                if (imageCache != null) {
                    imageCache!!.addBitmapToCache(dataString, drawable)
                }
            }
            return drawable
            //END_INCLUDE(load_bitmap_in_background)
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        override fun onPostExecute(value: BitmapDrawable?) { //BEGIN_INCLUDE(complete_background_work)
            var value: BitmapDrawable? = value
            var success = false
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled || mExitTasksEarly) {
                value = null
            }
            val imageView = attachedImageView
            if (value != null && imageView != null) {
                success = true
                setImageDrawable(imageView, value)
            } else {
                if (mErrorBitmap != null)
                    imageView?.setImageBitmap(mErrorBitmap)
            }
            mOnImageLoadedListener?.onImageLoaded(success)
            //END_INCLUDE(complete_background_work)
        }

        override fun onCancelled(value: BitmapDrawable?) {
            super.onCancelled(value)
            synchronized(mPauseWorkLock) { mPauseWorkLock.signalAll() }
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private val attachedImageView: ImageView?
            private get() {
                val imageView = imageViewReference.get()
                val bitmapWorkerTask =
                    getBitmapWorkerTask(imageView)
                return if (this === bitmapWorkerTask) {
                    imageView
                } else null
            }
    }

    /**
     * Interface definition for callback on image loaded successfully.
     */
    interface OnImageLoadedListener {
        /**
         * Called once the image has been loaded.
         * @param success True if the image was loaded successfully, false if
         * there was an error.
         */
        fun onImageLoaded(success: Boolean)
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    private class AsyncDrawable(
        res: Resources?,
        bitmap: Bitmap?,
        bitmapWorkerTask: BitmapWorkerTask
    ) : BitmapDrawable(res, bitmap) {
        private val bitmapWorkerTaskReference: WeakReference<BitmapWorkerTask> =
            WeakReference(bitmapWorkerTask)
        val bitmapWorkerTask: BitmapWorkerTask?
            get() = bitmapWorkerTaskReference.get()

    }

    /**
     * Called when the processing is complete and the final drawable should be
     * set on the ImageView.
     *
     * @param imageView
     * @param drawable
     */
    private fun setImageDrawable(
        imageView: ImageView,
        drawable: Drawable
    ) {
        if (mFadeInBitmap) { // Transition drawable with a transparent drawable and the final drawable
            val td = TransitionDrawable(
                arrayOf(
                    ColorDrawable(Color.TRANSPARENT),
                    drawable
                )
            )
            // Set background to loading bitmap
            /* imageView.setBackgroundDrawable(
                 BitmapDrawable(mResources, mLoadingBitmap)
             )*/
            imageView.setImageDrawable(td)
            td.startTransition(FADE_IN_TIME)
        } else {
            imageView.setImageDrawable(drawable)
        }
    }

    /**
     * Pause any ongoing background work. This can be used as a temporary
     * measure to improve performance. For example background work could
     * be paused when a ListView or GridView is being scrolled using a
     * [android.widget.AbsListView.OnScrollListener] to keep
     * scrolling smooth.
     *
     *
     * If work is paused, be sure setPauseWork(false) is called again
     * before your fragment or activity is destroyed , or there is a risk the
     * background thread will never finish.
     */
    fun setPauseWork(pauseWork: Boolean) {
        synchronized(mPauseWorkLock) {
            mPauseWork = pauseWork
            if (!mPauseWork) {
                mPauseWorkLock.signal()
            }
        }
    }

    protected inner class CacheAsyncTask :
        AsyncTask<Any?, Void?, Void?>() {
        protected override fun doInBackground(vararg params: Any?): Void? {
            when (params[0] as Int) {
                MESSAGE_CLEAR -> clearCacheInternal()
                MESSAGE_CLOSE -> closeCacheInternal()
            }
            return null
        }
    }

    protected open fun clearCacheInternal() {
        if (imageCache != null) {
            imageCache!!.clearCache()
        }
    }

    protected open fun closeCacheInternal() {
        if (imageCache != null) {
            imageCache = null
        }
    }

    companion object {
        private const val TAG = "ImageWorker"
        private const val FADE_IN_TIME = 200
        private const val MESSAGE_CLEAR = 0
        private const val MESSAGE_INIT_DISK_CACHE = 1
        private const val MESSAGE_FLUSH = 2
        private const val MESSAGE_CLOSE = 3
        /**
         * Cancels any pending work attached to the provided ImageView.
         * @param imageView
         */
        fun cancelWork(imageView: ImageView?) {
            val bitmapWorkerTask =
                getBitmapWorkerTask(imageView)
            bitmapWorkerTask?.cancel(true)
        }

        /**
         * Returns true if the current work has been canceled or if there was no work in
         * progress on this image view.
         * Returns false if the work in progress deals with the same data. The work is not
         * stopped in that case.
         */
        fun cancelPotentialWork(
            data: Any,
            imageView: ImageView?
        ): Boolean { //BEGIN_INCLUDE(cancel_potential_work)
            val bitmapWorkerTask =
                getBitmapWorkerTask(imageView)
            if (bitmapWorkerTask != null) {
                val bitmapData = bitmapWorkerTask.mData
                if (bitmapData == null || bitmapData != data) {
                    bitmapWorkerTask.cancel(true)
                } else { // The same work is already in progress.
                    return false
                }
            }
            return true
            //END_INCLUDE(cancel_potential_work)
        }

        /**
         * @param imageView Any imageView
         * @return Retrieve the currently active work task (if any) associated with this imageView.
         * null if there is no such task.
         */
        private fun getBitmapWorkerTask(imageView: ImageView?): BitmapWorkerTask? {
            if (imageView != null) {
                val drawable = imageView.drawable
                if (drawable is AsyncDrawable) {
                    return drawable.bitmapWorkerTask
                }
            }
            return null
        }
    }

}