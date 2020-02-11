package com.sbn.pinboard.shared.domain.home

import com.sbn.model.User
import com.sbn.pinboard.shared.data.HomeRepository
import com.sbn.pinboard.shared.domain.TestUseCase
import com.sbn.pinboard.shared.result.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TestHomeUseCase @Inject constructor(private val repository: HomeRepository) :
    TestUseCase<Unit, Result<List<User>>>() {
    override suspend fun execute(parameters: Unit): Result<List<User>> {
        return (repository.homeData().first { it is Result.Success } as Result.Success)
    }
}