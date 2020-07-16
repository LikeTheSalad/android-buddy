package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.providers.AndroidPluginDataProvider
import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import com.likethesalad.android.buddy.providers.impl.AppAndroidPluginDataProviderFactory
import com.likethesalad.android.common.utils.Logger
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val androidBuddyPlugin: AndroidBuddyPlugin) {

    @Provides
    @AppScope
    fun providePluginClassNamesProvider(): PluginClassNamesProvider {
        return androidBuddyPlugin
    }

    @Provides
    @AppScope
    fun provideFileTreeCreator(): FileTreeIteratorProvider {
        return androidBuddyPlugin
    }

    @Provides
    @AppScope
    fun provideAndroidPluginDataProvider(
        appFactory: AppAndroidPluginDataProviderFactory,
        logger: Logger
    ): AndroidPluginDataProvider {
        return appFactory.create(androidBuddyPlugin.appExtension!!, logger)
    }
}