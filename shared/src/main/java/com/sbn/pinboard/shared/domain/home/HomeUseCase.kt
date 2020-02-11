package com.sbn.pinboard.shared.domain.home

import com.sbn.model.User
import com.sbn.pinboard.shared.data.HomeRepository
import com.sbn.pinboard.shared.di.IoDispatcher
import com.sbn.pinboard.shared.domain.FlowUseCase
import com.sbn.pinboard.shared.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher, val repository: HomeRepository
) :
    FlowUseCase<Unit, List<User>>(coroutineDispatcher) {
    override fun execute(parameters: Unit): Flow<Result<List<User>>> {
        return repository.homeData()
    }


}