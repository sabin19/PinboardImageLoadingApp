package com.sbn.pinboard

import androidx.lifecycle.ViewModel
import com.sbn.pinboard.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindHomeActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}