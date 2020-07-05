package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.providers.AndroidBootClasspathProvider
import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import dagger.Module
import dagger.Provides

@Module
class AndroidBuddyModule(private val androidBuddyPlugin: AndroidBuddyPlugin) {

    @Provides
    fun providePluginClassNamesProvider(): PluginClassNamesProvider {
        return androidBuddyPlugin
    }

    @Provides
    fun provideFileTreeCreator(): FileTreeIteratorProvider {
        return androidBuddyPlugin
    }

    @Provides
    fun provideAndroidBootClasspathProvider(): AndroidBootClasspathProvider {
        return androidBuddyPlugin
    }
}