package com.slack.exercise.dagger

import com.slack.exercise.dataprovider.UserSearchResultDataProvider
import com.slack.exercise.ui.dagger.BindingModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Singleton

/**
 * Component providing Application scoped instances.
 */
@Singleton
@Component(modules = [AppModule::class, AndroidInjectionModule::class, BindingModule::class])
interface AppComponent : AndroidInjector<DaggerApplication> {
  fun userSearchResultDataProvider(): UserSearchResultDataProvider
}