package com.sbn.pinboard.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.sbn.netwoking.ImageFetcher


object ViewBindingUtil {
    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "loader"], requireAll = true)
    fun imageLoader(
        view: ImageView,
        imageUrl: String?,
        loader: ImageFetcher?
    ) {
        imageUrl?.let { url ->
            loader?.loadImage(url, view)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["shapableImageUrl", "loaderShapable"], requireAll = true)
    fun imageLoaderWithShapableImageView(
        view: ImageView,
        imageUrl: String?,
        loader: ImageFetcher?
    ) {

        imageUrl?.let { url ->
            loader?.loadImage(url, view)
        }
    }
}