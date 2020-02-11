/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmName("UseCaseClass")

package com.sbn.netwoking.domain


import com.sbn.netwoking.domain.internal.DefaultScheduler
import com.sbn.netwoking.domain.internal.Scheduler
import com.sbn.netwoking.domain.response.Response

/**
 * Executes business logic synchronously or asynchronously using a [Scheduler].
 */
abstract class UseCase<in P, R> {
    protected var taskScheduler: Scheduler = DefaultScheduler

    /** Executes the use case asynchronously and places the [Result] in a Callback
     *
     * @param parameters the input parameters to run the use case with
     * @param result the closure where the result is posted to
     *
     */
    operator fun invoke(parameters: P, result: (Response<R>) -> Unit) {
        try {
            taskScheduler.execute {
                try {
                    execute(parameters).let {
                        taskScheduler.postToMainThread {
                            result(Response.Success(it))
                        }
                    }
                } catch (e: Exception) {
                    taskScheduler.postToMainThread {
                        result(Response.Error(e))
                    }
                }
            }
        } catch (e: Exception) {
            taskScheduler.postToMainThread {
                result(Response.Error(e))
            }
        }
    }


    /** Executes the use case synchronously  */
    fun executeNow(parameters: P): Response<R> {
        return try {
            Response.Success(execute(parameters))
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract fun execute(parameters: P): R


}

operator fun <R> UseCase<Unit, R>.invoke(result: (Response<R>) -> Unit) = this(Unit, result)
