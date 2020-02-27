package com.sbn.pinboard.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sbn.pinboard.ui.home.getOrAwaitValue
import com.sbn.pinboard.ui.util.SyncTaskExecutorRule
import com.sbn.pinboard.ui.util.getUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DetailsViewModelTest{

    private lateinit var viewModel:DetailsViewModel
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @Before
    fun setup(){
        viewModel =  DetailsViewModel()
        viewModel.user.value = getUser()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userLiveData(){
        assertEquals(viewModel.user.getOrAwaitValue(), getUser())

    }

}