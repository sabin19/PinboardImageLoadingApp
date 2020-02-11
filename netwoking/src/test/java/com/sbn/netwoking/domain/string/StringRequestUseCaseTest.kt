package com.sbn.netwoking.domain.string

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.sbn.netwoking.data.StringRequestRepository
import com.sbn.netwoking.data.TestData
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.StringRequestParam
import com.sbn.test.util.SyncExecutorRule
import org.hamcrest.core.Is
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StringRequestUseCaseTest {

    private lateinit var repository: StringRequestRepository
    private lateinit var useCaseTest: StringRequestUseCase

    @get:Rule
    var syncExecutorRule = SyncExecutorRule()


    @Before
    fun setUp() {
        repository = mock {
            on {
                stringRequest(
                    any(),
                    any(),
                    any(),
                    any()
                )
            }.thenReturn(Response.Success(TestData.stringResponse))
        }
    }

    @Test
    fun testImageUseCase() {
        useCaseTest = StringRequestUseCase(repository)
        Assert.assertThat(
            useCaseTest(StringRequestParam(TestData.url, Method.GET, mock(), mock())) {
            }, Is.`is`(
                IsInstanceOf(Unit::class.java)
            )
        )

    }
}