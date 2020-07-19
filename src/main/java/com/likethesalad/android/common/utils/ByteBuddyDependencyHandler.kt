package com.likethesalad.android.common.utils

import org.gradle.api.artifacts.dsl.DependencyHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ByteBuddyDependencyHandler @Inject constructor() {

    companion object {
        private const val BYTE_BUDDY_DEPENDENCY_FORMAT = "net.bytebuddy:byte-buddy:%s"
        private const val BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME = "android.buddy.byteBuddy.version"
    }

    fun addDependency(dependencyHandler: DependencyHandler, projectProperties: Map<String, Any?>) {
        dependencyHandler.add(
            "compileOnly",
            BYTE_BUDDY_DEPENDENCY_FORMAT.format(getByteBuddyVersion(projectProperties))
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