package com.sbn.pinboard.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.sbn.model.User
import com.sbn.pinboard.shared.data.HomeRepository
import com.sbn.pinboard.shared.data.source.PagingDataSource
import com.sbn.pinboard.shared.domain.home.HomeUseCase
import com.sbn.pinboard.shared.result.Result
import com.sbn.pinboard.ui.util.LiveDataTestUtil
import com.sbn.pinboard.ui.util.SyncTaskExecutorRule
import com.sbn.pinboard.ui.util.getUser
import com.sbn.test.data.MainCoroutineRule
import com.sbn.test.data.runBlockingTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.hamcrest.core.Is
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()


    @Test
    fun testUserListObject(){
    val list = listOf(getUser())
    val pagedList = pagedList(list)?.toList()
            Assert.assertThat(
        pagedList[0], Is.`is`(
        IsInstanceOf(User::class.java)
    ))
    }

    @Test
    fun testUserList(){
    val list = listOf(getUser())
    val pagedList = pagedList(list)?.toList()
        assertEquals(list, pagedList)
    }

}


fun pagedList(list: List<User>): PagedList<User> {
    val pagedSize = 10

    val config = PagedList.Config.Builder()
        .setPageSize(pagedSize)
        .setInitialLoadSizeHint(pagedSize)
        .setPrefetchDistance(pagedSize)
        .setEnablePlaceholders(false)
        .build()

    return LivePagedListBuilder(createMockDataSourceFactory(list), config).build().getOrAwaitValue()
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    afterObserve.invoke()
    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}


private fun  createMockDataSourceFactory(itemList: List<User>): DataSource.Factory<Int, User> =
    object : DataSource.Factory<Int, User>() {
        val dataSourceLiveData = MutableLiveData<MockLimitDataSource>()
        override fun create(): DataSource<Int, User> {
            dataSourceLiveData.postValue(MockLimitDataSource(itemList))
            return MockLimitDataSource(itemList)
        }

    }

class MockLimitDataSource(private val itemList: List<User>) :PageKeyedDataSource<Int, User>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {
        callback.onResult(
            itemList,
            null,
            1
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        callback.onResult(
            itemList,
            params.requestedLoadSize
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {

    }


}

class FakeHomeRepository : HomeRepository {
    override fun homeData(): Flow<Result<List<User>>> {
        return flow {
            emit(Result.Success(listOf(getUser())))
        }
    }

}




