package com.sbn.netwoking.data

import android.R.attr
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.RequestParam
import java.io.*
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection


class RemoteData : RemoteDataSource {
    override fun getRemoteData(
        url: URL,
        method: Method,
        parameters: List<RequestParam<String, String>>?,
        headers: List<RequestParam<String, String>>?
    ): String? {
        var stream: InputStream? = null
        var result: String? = null
        var connection: HttpsURLConnection? = null
        try {
            connection = url.openConnection() as HttpsURLConnection?
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection?.readTimeout = 3000
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection?.connectTimeout = 3000
            // For this use case, set HTTP method to GET.
            connection?.requestMethod = method.method
            connection?.useCaches = true
            connection?.setRequestProperty("Accept-Charset", "UTF-8");
            // Already true by default but setting just in case; needs to be true since this request
// is carrying an input (response) body.
            connection?.doInput = true
            var data: String? = null
            parameters?.let {
                it.forEach { params ->
                    if (data == null)
                        data = (URLEncoder.encode(params.key, "UTF-8").toString() + "="
                                + URLEncoder.encode(params.value, "UTF-8"))
                    else
                        data += ("&" + URLEncoder.encode(params.key, "UTF-8").toString() + "="
                                + URLEncoder.encode(params.value, "UTF-8"))

                }
            }
            headers?.let {
                it.forEach { params ->
                    connection?.setRequestProperty(params.key, params.value)
                }
            }
            if (data != null) {
                connection?.doOutput = true
                connection?.outputStream?.let {
                    val wr = OutputStreamWriter(it)
                    wr.write(attr.data)
                    wr.flush()
                }
            }
            connection?.connect()
            val responseCode = connection?.responseCode
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw IOException("HTTP error code: $responseCode")
            }
            // Retrieve the response body as an InputStream.
            stream = connection?.inputStream
            if (stream != null) { // Converts Stream to String with max length of 500.
                result = readStream(stream)
            }
        } finally { // Close Stream and disconnect HTTPS connection.
            stream?.close()
            connection?.disconnect()
        }
        return result
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    @Throws(IOException::class)
    private fun readStream(stream: InputStream): String? {
        // Create temporary buffer to hold Stream data
        val bufferedReader = BufferedReader(
            InputStreamReader(stream, StandardCharsets.UTF_8)
        )
        var inputLine: String?
        val response = StringBuffer()

        while (bufferedReader.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        return response.toString()
    }

}