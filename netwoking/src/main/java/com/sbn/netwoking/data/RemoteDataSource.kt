package com.sbn.netwoking.data

import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.RequestParam
import java.net.URL

interface RemoteDataSource {
    fun getRemoteData(
        url: URL,
        method: Method,
        parameters: List<RequestParam<String, String>>? = null,
        headers: List<RequestParam<String, String>>? = null
    ): String?
}