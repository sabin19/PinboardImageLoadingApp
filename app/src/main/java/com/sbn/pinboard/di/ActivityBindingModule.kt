package com.sbn.pinboard.di

import com.sbn.pinboard.MainActivity
import com.sbn.pinboard.MainActivityModule
import com.sbn.pinboard.shared.di.ActivityScoped
import com.sbn.pinboard.ui.details.DetailsModule
import com.sbn.pinboard.ui.home.HomeModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainActivityModule::class, HomeModule::class, DetailsModule::class])
    abstract fun contributeMainActivity(): MainActivity
}