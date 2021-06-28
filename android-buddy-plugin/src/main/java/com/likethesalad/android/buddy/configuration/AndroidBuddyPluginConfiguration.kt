package com.likethesalad.android.buddy.configuration

import com.android.build.api.transform.QualifiedContent
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
    private val lazyTransformationScope: MutableSet<in QualifiedContent.Scope> by lazy {
        val scope = extension.transformationScope.scope.getOrElse(TransformationScopeType.PROJECT.name)
        when (scope.toUpperCase()) {
            TransformationScopeType.ALL.name -> mutableSetOf(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
            )
            else -> mutableSetOf(QualifiedContent.Scope.PROJECT)
        }
    }
    private val lazyExcludePrefixes: Set<String> by lazy {
        extension.transformationScope.excludePrefixes.getOrElse(emptySet())
    }

    fun getLibrariesScope(): LibrariesScope {
        return lazyLibrariesScope
    }

    fun getTransformationScope(): MutableSet<in QualifiedContent.Scope> {
        return lazyTransformationScope
    }

    fun getExcludePrefixes(): Set<String> {
        return lazyExcludePrefixes
    }
}