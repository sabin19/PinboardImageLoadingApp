package com.sbn.netwoking.domain.string

import com.sbn.netwoking.data.StringRequestRepository
import com.sbn.netwoking.domain.UseCase
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.RequestParam
import com.sbn.netwoking.util.StringRequestParam

class StringRequestUseCase constructor(private val repository: StringRequestRepository) :
    UseCase<StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>, String>() {
    override fun execute(parameters: StringRequestParam<Method, List<RequestParam<String, String>>, List<RequestParam<String, String>>>): String {
        return when (val res = repository.stringRequest(
            parameters.url,
            parameters.method,
            parameters.parameters,
            parameters.headers
        )) {
            is Response.Success -> res.data
            is Response.Error -> throw res.exception
        }
    }
}
