package com.sbn.pinboard.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sbn.model.User
import com.sbn.pinboard.shared.data.HomeRepository
import com.sbn.pinboard.shared.domain.home.HomeUseCase
import com.sbn.pinboard.shared.result.Result
import com.sbn.pinboard.ui.util.SyncTaskExecutorRule
import com.sbn.test.data.MainCoroutineRule
import com.sbn.test.data.runBlockingTest
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
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Overrides Dispatchers.Main used in Coroutines

    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @get:Rule
    var coroutineRule = MainCoroutineRule()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        /*viewModel = mock {
            on { list.value }.thenReturn(listOf(user))
        }*/
    }

    @Test
    fun testLoadUserList() = coroutineRule.runBlockingTest {
        val homeUseCase = HomeUseCase(coroutineRule.testDispatcher, FakeHomeRepository())
        viewModel = HomeViewModel(homeUseCase)
        val data = viewModel.homeUseCase(Unit).first()
        Assert.assertThat(
            data, Is.`is`(
                IsInstanceOf(Result.Success::class.java)
            )
        )
    }
}

class FakeHomeRepository : HomeRepository {
    override fun homeData(): Flow<Result<List<User>>> {
        return flow {
            emit(Result.Success(listOf(user)))
        }
    }

}

val links = User.Category.Links(
    "photos", "self"
)

val category = User.Category(
    1,
    links,
    2,
    "title"
)

val newList = User.Links(
    "download", "html", "self"
)

val urls = User.Urls(
    "full",
    "raw",
    "regular",
    "small",
    "thumb"
)

val userLinks = User.User.Links("", "", "", "")
val profileImages = User.User.ProfileImage("", "", "")

val users = User.User(
    "id",
    userLinks,
    "",
    profileImages,
    ""
)


val user = User(
    listOf(category), "#ffffff", "11-02-2020", 10, "1212132323", false, 12,
    newList,
    urls, users, 1
)


