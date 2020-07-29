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
        return extension.dependencies.get().alwaysLogTransformationNames.get()
    }

    fun useOnlyAndroidBuddyImplementations(): Boolean {
        return extension.dependencies.get().strictModeEnabled.get()
    }

    fun useDependenciesTransformations(): Boolean {
        return !extension.dependencies.get().disableAllTransformations.get()
    }
}