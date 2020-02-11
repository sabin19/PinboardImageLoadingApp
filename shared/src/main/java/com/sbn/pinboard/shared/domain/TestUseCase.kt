package com.sbn.pinboard.shared.domain

import androidx.lifecycle.LiveData


/**
 * Executes business logic synchronously or asynchronously using a [Scheduler].
 */
abstract class TestUseCase<in P, R> {
    /*
      *
      * @param parameters the input parameters to run the use case with
      * @param result  where the result is posted to
      *
      */

    /** Executes the use case asynchronously and returns a [Result] in a saveOrUpdate LiveData object.
     *
     * @return an observable [LiveData] with a [Result].
     *
     * @param parameters the input parameters to run the use case with
     */

    suspend operator fun invoke(parameters: P): R {
        return execute(parameters).let { useCaseResult ->
            (useCaseResult)
        }
    }

    /** Executes the use case synchronously  */
    suspend fun executeNow(parameters: P): R {
        return (execute(parameters))
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

suspend operator fun <R> TestUseCase<Unit, R>.invoke(): R = this(Unit)

