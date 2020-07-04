package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.utils.AndroidBootClasspathProvider
import com.likethesalad.android.buddy.utils.FileTreeIteratorProvider
import com.likethesalad.android.buddy.utils.PluginClassNamesProvider
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