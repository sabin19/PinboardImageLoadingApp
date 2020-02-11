package com.sbn.pinboard.ui.details


import androidx.lifecycle.ViewModel
import com.sbn.pinboard.shared.di.FragmentScoped
import com.sbn.pinboard.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
internal abstract class DetailsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun detailsFragment(): DetailsFragment

    @ExperimentalCoroutinesApi
    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    internal abstract fun bindDetailsViewModel(viewModel: DetailsViewModel): ViewModel

}