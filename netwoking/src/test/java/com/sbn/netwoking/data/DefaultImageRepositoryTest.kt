package com.sbn.netwoking.data

import android.graphics.Bitmap
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.SyncExecutorRule
import org.hamcrest.core.Is
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DefaultImageRepositoryTest {


    private lateinit var repository: ImageRepository
    lateinit var bitmapRemoteSource: BitmapRemoteSource

    @get:Rule
    var syncExecutorRule = SyncExecutorRule()

    @Mock
    private lateinit var bitmap: Bitmap

    @Before
    fun setup() {
        bitmapRemoteSource = mock {
            on {
                getRemoteData(any())
            }.thenReturn(bitmap)
        }

    }

    @Test
    fun testImageRepository() {
        repository = DefaultImageRepository(bitmapRemoteSource)
        Assert.assertThat(
            repository.getBitMapImage(TestData.url),
            Is.`is`(IsInstanceOf(Response.Success::class.java))
        )
    }

    @Test
    fun testImageRepositoryWithError() {
        bitmapRemoteSource = mock {
            on {
                getRemoteData(any())
            }.thenReturn(bitmap)
        }
        repository = DefaultImageRepository(bitmapRemoteSource)
        Assert.assertThat(
            repository.getBitMapImage(TestData.url),
            Is.`is`(IsInstanceOf(Response.Success::class.java))
        )
    }


}