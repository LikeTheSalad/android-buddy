package com.likethesalad.android.buddy.configuration

import com.likethesalad.android.buddy.configuration.libraries.LibrariesOptionsMapper
import com.likethesalad.android.buddy.configuration.libraries.LibrariesPolicy
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.AndroidBuddyExtensionProvider
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@AppScope
class AndroidBuddyPluginConfiguration
@Inject constructor(
    androidBuddyExtensionProvider: AndroidBuddyExtensionProvider,
    private val librariesOptionsMapper: LibrariesOptionsMapper
) {
    private val extension by lazy { androidBuddyExtensionProvider.getAndroidBuddyExtension() }
    
    fun getLibrariesPolicy(): LibrariesPolicy {
        return librariesOptionsMapper.librariesOptionsToLibrariesPolicy(extension.librariesOptions)
    }
}