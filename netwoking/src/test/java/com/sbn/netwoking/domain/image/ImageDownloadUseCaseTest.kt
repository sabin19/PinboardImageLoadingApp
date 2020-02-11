package com.sbn.netwoking.domain.image

import android.graphics.Bitmap
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.sbn.netwoking.data.ImageRepository
import com.sbn.netwoking.data.TestData
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
class ImageDownloadUseCaseTest {

    private lateinit var repository: ImageRepository
    private lateinit var useCaseTest: ImageDownloadUseCase

    @get:Rule
    var syncExecutorRule = SyncExecutorRule()

    @Mock
    private lateinit var response: Response<Bitmap>

    @Before
    fun setUp() {
        repository = mock {
            on { getBitMapImage(any()) }.thenReturn(response)
        }
    }

    @Test
    fun testImageUseCase() {
        useCaseTest = ImageDownloadUseCase(mock(), repository)
        Assert.assertThat(
            useCaseTest(TestData.dataUrl) {
            }, Is.`is`(
                IsInstanceOf(Unit::class.java)
            )
        )

    }
}