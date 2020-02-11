package com.sbn.pinboard.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sbn.model.User
import com.sbn.pinboard.shared.data.source.PagingDataFactory
import com.sbn.pinboard.shared.data.source.PagingDataSource
import com.sbn.pinboard.shared.result.Event
import com.sbn.pinboard.shared.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class HomeViewModel @Inject constructor(val dataFactory: PagingDataFactory) : ViewModel(),
    OnUserItemClickedListener {
    private val _onUser = MutableLiveData<Event<User>>()
    val onUser: LiveData<Event<User>> get() = _onUser


    private val pagedSize = 4

    val list: LiveData<PagedList<User>>
    init {
        val config = PagedList.Config.Builder()
            .setPageSize(pagedSize)
            .setInitialLoadSizeHint(pagedSize)
            .setPrefetchDistance(pagedSize)
            .setEnablePlaceholders(false)
            .build()
        list = LivePagedListBuilder(dataFactory, config).build()
    }

    val state: LiveData<Result<Boolean>> = Transformations.switchMap<PagingDataSource,
            Result<Boolean>>(
        dataFactory.dataSourceLiveData,
        PagingDataSource::state
    )


    override fun onClick(item: User) {
        _onUser.value = Event(item)
    }

}
