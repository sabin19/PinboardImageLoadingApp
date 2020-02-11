package com.sbn.netwoking.data

import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.RequestParam
import java.net.MalformedURLException
import java.net.URL

interface StringRequestRepository {
    fun stringRequest(
        url: String,
        method: Method,
        parameters: List<RequestParam<String, String>>? = null,
        headers: List<RequestParam<String, String>>? = null
    ): Response<String>
}

class DefaultRequestRepository constructor(
    private val dataSource: RemoteDataSource
) : StringRequestRepository {
    override fun stringRequest(
        url: String,
        method: Method,
        parameters: List<RequestParam<String, String>>?,
        headers: List<RequestParam<String, String>>?
    ): Response<String> {
        return try {
            val data = dataSource.getRemoteData(URL(url), method, parameters, headers)
            if (data != null)
                Response.Success(data)
            else Response.Error(Exception("No data returned"))
        } catch (e: MalformedURLException) {
            Response.Error(e)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

}