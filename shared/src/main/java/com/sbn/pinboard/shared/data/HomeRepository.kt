package com.sbn.pinboard.shared.data

import com.google.gson.JsonSyntaxException
import com.sbn.model.User
import com.sbn.netwoking.domain.response.Response
import com.sbn.netwoking.util.Method
import com.sbn.netwoking.util.StringRequest
import com.sbn.netwoking.util.StringRequestParam
import com.sbn.pinboard.shared.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface HomeRepository {
    fun homeData(): Flow<Result<List<User>>>
}

class DefaultHomeRepository @Inject constructor(
    private val dataSource: StringRequest
) : HomeRepository {
    @ExperimentalCoroutinesApi
    override fun homeData(): Flow<Result<List<User>>> {
        return callbackFlow {
            val params = StringRequestParam(Constants.HOME_URL, Method.GET, null, null)
            offer(Result.Loading)
            dataSource.stringRequest(params) {
                try {
                    when (it) {
                        is Response.Success -> offer(
                            Result.Success(
                                HomeDataJsonParser.parseHomeData(
                                    it.data
                                )
                            )
                        )
                        is Response.Error -> close(it.exception)
                    }

                } catch (e: JsonSyntaxException) {
                    close(e)
                } catch (e: IllegalStateException) {
                    close(e)
                }
            }
            awaitClose()

        }
    }

}