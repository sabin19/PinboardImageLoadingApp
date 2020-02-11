

package com.sbn.pinboard.shared.di

import com.sbn.netwoking.DataFetcher
import com.sbn.netwoking.util.StringRequest
import com.sbn.pinboard.shared.data.DefaultHomeRepository
import com.sbn.pinboard.shared.data.HomeRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module where classes created in the shared module are created.
 */
@Module
class SharedModule {
    @Singleton
    @Provides
    fun provideDataFetcher(): StringRequest {
        return DataFetcher()
    }


    @Singleton
    @Provides
    fun providerHomeRepository(dataFetcher: StringRequest): HomeRepository =
        DefaultHomeRepository(dataFetcher)

}
