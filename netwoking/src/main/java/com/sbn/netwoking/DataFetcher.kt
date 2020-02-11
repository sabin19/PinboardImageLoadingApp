package com.sbn.netwoking

import com.sbn.netwoking.data.DefaultRequestRepository
import com.sbn.netwoking.data.RemoteData
import com.sbn.netwoking.data.StringRequestRepository
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.domain.string.StringRequestUseCase
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.RequestParam
import com.sbn.netwoking.util.StringRequest
import com.sbn.netwoking.util.StringRequestParam

class DataFetcher : StringRequest {

    private lateinit var useCase: StringRequestUseCase

    public override fun stringRequest(
        params: StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>,
        onSuccess: (String) -> Unit, onErrorWithException: (Exception) -> Unit
    ) {
        val repository: StringRequestRepository = DefaultRequestRepository(RemoteData())
        useCase =
            StringRequestUseCase(repository)
        useCase(params) {
            if (it is Response.Success)
                onSuccess(it.data)
            else if (it is Response.Error) onErrorWithException(it.exception)
        }

    }

    public override fun stringRequest(
        params: StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>,
        onResponse: (Response<String>) -> Unit
    ) {
        val repository: StringRequestRepository = DefaultRequestRepository(RemoteData())
        useCase =
            StringRequestUseCase(repository)
        useCase(params) {
            onResponse(it)
        }

    }
}