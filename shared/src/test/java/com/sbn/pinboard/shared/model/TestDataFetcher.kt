package com.sbn.pinboard.shared.model

import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.RequestParam
import com.sbn.netwoking.util.StringRequest
import com.sbn.netwoking.util.StringRequestParam
import com.sbn.pinboard.shared.data.TestData

object TestDataFetcher : StringRequest {
    override fun stringRequest(
        params: StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>,
        onSuccess: (String) -> Unit,
        onErrorWithException: (Exception) -> Unit
    ) {
        onSuccess(TestData.stringResponse)
    }

    override fun stringRequest(
        params: StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>,
        onResponse: (Response<String>) -> Unit
    ) {
        onResponse(Response.Success(TestData.stringResponse))
    }
}