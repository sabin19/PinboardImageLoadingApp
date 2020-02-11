package com.sbn.pinboard.di

import android.content.Context
import com.sbn.pinboard.MainApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideContext(application: MainApplication): Context {
        return application
    }
}