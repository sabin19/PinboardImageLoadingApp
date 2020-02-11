package com.sbn.netwoking.domain.response


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Response<out R> {

    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val exception: Exception) : Response<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}

/**
 * `true` if [Response] is of type [Success] & holds non-null [Success.data].
 */
val Response<*>.succeeded
    get() = this is Response.Success && data != null

fun <T> Response<T>.successOr(fallback: T): T {
    return (this as? Response.Success<T>)?.data ?: fallback
}
