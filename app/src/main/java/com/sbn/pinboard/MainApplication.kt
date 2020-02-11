package com.sbn.pinboard

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import com.sbn.pinboard.di.DaggerAppComponent;

class MainApplication: DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}