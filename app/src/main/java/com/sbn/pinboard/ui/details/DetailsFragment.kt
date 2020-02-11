package com.sbn.pinboard.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sbn.netwoking.ImageFetcher
import com.sbn.netwoking.util.ImageCache
import com.sbn.pinboard.R
import com.sbn.pinboard.databinding.FragmentDetailsBinding
import com.sbn.pinboard.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : DaggerFragment() {

    private lateinit var binding: FragmentDetailsBinding
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @ExperimentalCoroutinesApi
    private lateinit var viewModel: DetailsViewModel

    private val imageThumbSize by lazy { resources.getDimensionPixelSize(R.dimen.image_size) }

    private val imageFetcher: ImageFetcher? by lazy {
        context?.let { ImageFetcher(it, imageThumbSize) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val cacheParams = ImageCache.ImageCacheParams()
        cacheParams.setMemCacheSizePercent(0.25f) // Set memory cache to 25% of app memory
        imageFetcher?.addImageCache(activity!!.supportFragmentManager, cacheParams)
        imageFetcher?.setLoadingImage(R.drawable.empty_photo)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelProvider(viewModelFactory)
        binding.viewModel = viewModel
        binding.loader = imageFetcher
        binding.lifecycleOwner = this
        arguments?.let { bundle ->
            DetailsFragmentArgs.fromBundle(bundle).let {
                viewModel.user.postValue(it.user)
            }
        }


    }

}
