package com.sbn.netwoking.data

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.SyncExecutorRule
import org.hamcrest.core.Is
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

class DefaultRequestRepositoryTest {

    private lateinit var repository: StringRequestRepository
    private lateinit var remoteDataSource: RemoteDataSource

    @get:Rule
    var syncExecutorRule = SyncExecutorRule()

    @Mock
    private val result: String = "Test"

    @Before
    fun setup() {
        remoteDataSource = mock {
            on { getRemoteData(any(), any(), any(), any()) }.thenReturn(TestData.stringResponse)
        }

    }

    @Test
    fun testImageRepository() {
        repository = DefaultRequestRepository(remoteDataSource)
        Assert.assertThat(
            repository.stringRequest(TestData.dataUrl, Method.GET, mock(), mock()), Is.`is`(
                IsInstanceOf(Response.Success::class.java)
            )
        )
    }

    @Test
    fun testImageRepositoryWithError() {
        remoteDataSource = mock {
            on {
                getRemoteData(
                    any(),
                    any(),
                    any(),
                    any()
                )
            }.thenReturn(null)
        }
        repository = DefaultRequestRepository(remoteDataSource)
        Assert.assertThat(
            repository.stringRequest(TestData.dataUrl, Method.GET, null, null), Is.`is`(
                IsInstanceOf(Response.Error::class.java)
            )
        )
    }
}