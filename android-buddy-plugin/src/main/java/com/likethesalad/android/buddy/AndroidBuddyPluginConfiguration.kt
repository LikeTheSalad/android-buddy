package com.likethesalad.android.buddy

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.AndroidBuddyExtensionProvider
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@AppScope
class AndroidBuddyPluginConfiguration
@Inject constructor(androidBuddyExtensionProvider: AndroidBuddyExtensionProvider) {

    private val extension by lazy { androidBuddyExtensionProvider.getAndroidBuddyExtension() }

    fun alwaysLogDependenciesTransformationNames(): Boolean {
        return extension.dependenciesConfig.alwaysLogTransformationNames.get()
    }

    fun useDependenciesTransformations(): Boolean {
        return !extension.dependenciesConfig.disableAllTransformations.get()
    }
}