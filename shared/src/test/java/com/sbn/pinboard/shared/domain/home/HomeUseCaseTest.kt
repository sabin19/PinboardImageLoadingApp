package com.sbn.pinboard.shared.domain.home

import com.sbn.model.User
import com.sbn.pinboard.shared.data.DefaultHomeRepository
import com.sbn.pinboard.shared.model.TestDataFetcher
import com.sbn.pinboard.shared.result.Result
import com.sbn.test.data.MainCoroutineRule
import com.sbn.test.data.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.hamcrest.core.Is
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class HomeUseCaseTest {
    // Overrides Dispatchers.Main used in Coroutines
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Test
    fun testHomeUseCase() = coroutineRule.runBlockingTest {
        val repository = DefaultHomeRepository(TestDataFetcher)
        val useCase =
            HomeUseCase(coroutineDispatcher = coroutineRule.testDispatcher, repository = repository)
        val result = useCase(Unit)
        Assert.assertThat(result.first(), Is.`is`(IsInstanceOf(Result.Loading::class.java)))
    }

    @Test
    fun testSuccessHomeUseCase() = coroutineRule.runBlockingTest {
        val repository = DefaultHomeRepository(TestDataFetcher)
        val useCase =
            HomeUseCase(coroutineDispatcher = coroutineRule.testDispatcher, repository = repository)
        val result = useCase(Unit)
        Assert.assertThat(
            result.first { it is Result.Success },
            Is.`is`(IsInstanceOf(Result.Success::class.java))
        )
        val data = result.first { it is Result.Success }
        Assert.assertThat((data as Result.Success).data[0], Is.`is`(IsInstanceOf(User::class.java)))
    }

}