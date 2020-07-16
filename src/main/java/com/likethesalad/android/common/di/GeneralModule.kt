package com.likethesalad.android.common.di

import com.likethesalad.android.common.base.BuddyPlugin
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GeneralModule(private val buddyPlugin: BuddyPlugin) {

    @Provides
    @Singleton
    fun provideProjectLoggerProvider(): ProjectLoggerProvider {
        return buddyPlugin
    }
}