package com.sbn.netwoking.util

import com.sbn.netwoking.domain.response.Response

interface StringRequest {
    public fun stringRequest(
        params: StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>,
        onSuccess: (String) -> Unit, onErrorWithException: (Exception) -> Unit
    )

    public fun stringRequest(
        params: StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>,
        onResponse: (Response<String>) -> Unit
    )
}