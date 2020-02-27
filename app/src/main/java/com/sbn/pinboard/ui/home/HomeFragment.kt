package com.sbn.pinboard.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbn.model.User
import com.sbn.netwoking.ImageFetcher
import com.sbn.netwoking.util.ImageCache
import com.sbn.pinboard.R
import com.sbn.pinboard.databinding.HomeFragmentBinding
import com.sbn.pinboard.shared.result.EventObserver
import com.sbn.pinboard.shared.result.Result
import com.sbn.pinboard.util.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class HomeFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @ExperimentalCoroutinesApi
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding
    private val imageThumbSize by lazy { resources.getDimensionPixelSize(R.dimen.image_size) }
    private val imageFetcher: ImageFetcher? by lazy {
        context?.let { ImageFetcher(it, imageThumbSize) }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater,container,false)
        val cacheParams = ImageCache.ImageCacheParams()
        cacheParams.setMemCacheSizePercent(0.25f) // Set memory cache to 25% of app memory

        imageFetcher?.addImageCache(requireActivity().supportFragmentManager, cacheParams)
        imageFetcher?.setLoadingImage(R.drawable.empty_photo)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelProvider(viewModelFactory)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.imageLoader = imageFetcher

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 1)
        binding.recyclerView.addItemDecoration(CommonItemSpaceDecoration(16))
        val adapter = imageFetcher?.let { HomeAdapter(it, viewModel) }
        binding.recyclerView.adapter = adapter
        viewModel.list.observe(viewLifecycleOwner, Observer { items ->
            adapter?.submitList(items)
        })


        val connectivity = context?.let { InternetConnectivity(it) }
        connectivity?.skip(1)?.distinctUntilChanged()?.observe(viewLifecycleOwner, Observer {
            if (!it) {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.onUser.throttle(600L, viewModel.viewModelScope.coroutineContext)
            .observe(viewLifecycleOwner, EventObserver {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                        it
                    )
                )
            })




    }

}



@BindingAdapter(value = ["userList", "imageLoader", "listener"], requireAll = false)
fun RecyclerView.setAdapter(
    list: LiveData<PagedList<User>>?,
    imageLoader: ImageFetcher, listener: OnUserItemClickedListener?
) {
    if (this.adapter == null) {
        this.setHasFixedSize(true)
        this.layoutManager = GridLayoutManager(this.context, 1)
        this.addItemDecoration(CommonItemSpaceDecoration(16))
        this.adapter = HomeAdapter(imageLoader, listener)
    }

    (this.adapter as HomeAdapter).apply {
        submitList(list?.value)
    }
}

@BindingAdapter("loadingState")
fun loadingState(
    recyclerView: RecyclerView,
    state: Result<Boolean>?
) {
    recyclerView.adapter?.let {
        (it as HomeAdapter).apply {
            state?.let {
                setState(it)
            }
        }
    }

}

