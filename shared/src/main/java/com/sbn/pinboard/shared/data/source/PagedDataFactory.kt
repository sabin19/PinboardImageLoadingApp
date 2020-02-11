package com.sbn.pinboard.shared.data.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.sbn.model.User
import javax.inject.Inject


class PagingDataFactory @Inject constructor(private val newDataSource: PagingDataSource) :
    DataSource.Factory<Int, User>() {
    val dataSourceLiveData = MutableLiveData<PagingDataSource>()
    override fun create(): DataSource<Int, User> {
        dataSourceLiveData.postValue(newDataSource)
        return newDataSource
    }
}


