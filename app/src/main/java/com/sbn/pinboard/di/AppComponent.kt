package com.sbn.pinboard.di

import com.sbn.pinboard.MainApplication
import com.sbn.pinboard.shared.di.SharedModule
import com.sbn.pinboard.shared.di.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBindingModule::class,
    ViewModelModule::class, SharedModule::class, CoroutinesModule::class]
)
interface AppComponent : AndroidInjector<MainApplication> {
    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<MainApplication>
}