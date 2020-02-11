package com.sbn.pinboard.shared.data.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.sbn.model.User
import com.sbn.pinboard.shared.domain.home.HomeUseCase
import com.sbn.pinboard.shared.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PagingDataSource @Inject constructor(
    private val useCase: HomeUseCase
) : PageKeyedDataSource<Int, User>() {

    var state = MutableLiveData<Result<Boolean>>()

    @ExperimentalCoroutinesApi
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            state.postValue(Result.Loading)
            useCase(Unit).collect {
                when (val result = it
                    ) {
                    is Result.Success -> {
                        state.postValue(Result.Success(true))
                        callback.onResult(
                            result.data,
                            null,
                            if (result.data.size == params.requestedLoadSize) 1 else 0
                        )
                    }
                    is Result.Error -> {
                        state.postValue(Result.Error(result.exception))
                    }
                    is Result.Loading -> {
                        state.postValue(Result.Loading)
                    }
                }
            }

        }
    }

    @ExperimentalCoroutinesApi
    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, User>
    ) {
        /***
         * Commented this call because the api doesn't support pagination
         * */
        /*GlobalScope.launch(Dispatchers.IO) {
            state.postValue(Result.Loading)
            useCase(Unit).collect {
                when (val result = it
                    ) {
                    is Result.Success -> {
                        state.postValue(Result.Success(true))
                        callback.onResult(
                            result.data,
                            params.requestedLoadSize
                        )
                    }
                    is Result.Error -> {
                        state.postValue(Result.Error(result.exception))
                    }
                    is Result.Loading -> {
                        state.postValue(Result.Loading)
                    }
                }
            }

        }*/

    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, User>
    ) {
    }

}







