package com.sbn.pinboard.shared.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sbn.pinboard.shared.model.TestDataFetcher
import com.sbn.pinboard.shared.result.Result
import com.sbn.pinboard.shared.util.SyncExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.Is
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class DefaultHomeRepositoryTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Executes tasks in a synchronous [TaskScheduler]
    @get:Rule
    var syncExecutorRule = SyncExecutorRule()

    private val repository = DefaultHomeRepository(TestDataFetcher)

    @Test
    fun homeLoadingDataTest() = runBlocking {
        val response = repository.homeData().first()
        Assert.assertThat(response, Is.`is`(IsInstanceOf(Result.Loading::class.java)))
    }


    @Test
    fun homeDataTest() = runBlocking {
        val response = repository.homeData().first { it is Result.Success }
        Assert.assertThat(response, Is.`is`(IsInstanceOf(Result.Success::class.java)))
        assertEquals((response as Result.Success).data.size, 10)

    }
}