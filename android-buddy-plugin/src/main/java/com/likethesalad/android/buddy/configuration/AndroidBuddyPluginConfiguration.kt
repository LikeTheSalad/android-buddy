package com.likethesalad.android.buddy.configuration

import com.likethesalad.android.buddy.configuration.libraries.scope.LibrariesScope
import com.likethesalad.android.buddy.configuration.libraries.scope.LibrariesScopeMapper
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.AndroidBuddyExtensionProvider
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@AppScope
class AndroidBuddyPluginConfiguration
@Inject constructor(
    androidBuddyExtensionProvider: AndroidBuddyExtensionProvider,
    private val librariesScopeMapper: LibrariesScopeMapper
) {
    private val extension by lazy { androidBuddyExtensionProvider.getAndroidBuddyExtension() }

    fun getLibrariesScope(): LibrariesScope {
        return librariesScopeMapper.librariesOptionsToLibrariesPolicy(extension.librariesPolicy.scope)
    }
}