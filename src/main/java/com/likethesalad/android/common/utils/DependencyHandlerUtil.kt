package com.likethesalad.android.common.utils

import org.gradle.api.artifacts.dsl.DependencyHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyHandlerUtil @Inject constructor() {

    companion object {
        private const val BYTE_BUDDY_DEPENDENCY_FORMAT = "net.bytebuddy:byte-buddy:%s"
        private const val BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME = "android.buddy.byteBuddy.version"
    }

    fun addDependencies(dependencyHandler: DependencyHandler, projectProperties: Map<String, Any?>) {
        addCompileOnly(dependencyHandler, BYTE_BUDDY_DEPENDENCY_FORMAT.format(getByteBuddyVersion(projectProperties)))
        addCompileOnly(dependencyHandler, dependencyHandler.gradleApi())
    }

    private fun addCompileOnly(dependencyHandler: DependencyHandler, dependency: Any) {
        dependencyHandler.add(
            "compileOnly", dependency
        )
    }

    private fun getByteBuddyVersion(projectProperties: Map<String, Any?>): String {
        return getPropertyByteBuddyVersion(projectProperties) ?: Constants.BYTE_BUDDY_DEPENDENCY_VERSION
    }

    private fun getPropertyByteBuddyVersion(projectProperties: Map<String, Any?>): String? {
        val propertyVersion = projectProperties[BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME]

        if (propertyVersion != null && propertyVersion is String) {
            return propertyVersion.trim()
        }

        return null
    }
}