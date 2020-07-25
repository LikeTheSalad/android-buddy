package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.providers.AndroidExtensionProvider
import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val androidBuddyPlugin: AndroidBuddyPlugin) {

    @Provides
    @AppScope
    fun provideFileTreeCreator(): FileTreeIteratorProvider {
        return androidBuddyPlugin
    }

    @Provides
    @AppScope
    fun provideAndroidExtensionProvider(): AndroidExtensionProvider {
        return androidBuddyPlugin
    }

    @Provides
    @AppScope
    fun provideGradleConfigurationsProvider(): GradleConfigurationsProvider {
        return androidBuddyPlugin
    }
}