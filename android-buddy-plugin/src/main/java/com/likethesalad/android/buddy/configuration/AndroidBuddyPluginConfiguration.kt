package com.likethesalad.android.buddy.configuration

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.internal.utils.toImmutableSet
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
    private val lazyLibrariesScope: LibrariesScope by lazy {
        librariesScopeMapper.librariesScopeExtensionToLibrariesScope(extension.librariesPolicy.scope)
    }

    private val transformationScope by lazy { extension.transformationScope }

    fun getLibrariesScope(): LibrariesScope {
        return lazyLibrariesScope
    }

    fun getTransformationScope(): MutableSet<in QualifiedContent.Scope> {
        val scopes = transformationScope.scope.getOrElse(mutableSetOf(QualifiedContent.Scope.PROJECT.name))
        return scopes.map { scopeName -> QualifiedContent.Scope.valueOf(scopeName) }.toImmutableSet()
    }

    fun getExcludePrefixes(): Set<String> {
        return transformationScope.excludePrefixes.getOrElse(emptySet())
    }
}