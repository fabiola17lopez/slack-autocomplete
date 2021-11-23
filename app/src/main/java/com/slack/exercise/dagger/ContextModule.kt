package com.slack.exercise.dagger

import android.app.Application
import android.content.Context
import com.slack.exercise.api.SlackApi
import com.slack.exercise.api.SlackApiImpl
import com.slack.exercise.dataprovider.UserSearchResultDataProvider
import com.slack.exercise.dataprovider.UserSearchResultDataProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * Module to setup Application scoped instances that require providers.
 */
@Module
class ContextModule(private val application: Application) {

    @Provides
    fun providesApplicationContext(): Context = application
}
