package com.likethesalad.android.common.utils

import org.gradle.api.Project
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ByteBuddyDependencyHandler @Inject constructor() {

    companion object {
        private const val BYTE_BUDDY_DEPENDENCY_FORMAT = "net.bytebuddy:byte-buddy:%s"
        private const val BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME = "android.buddy.byteBuddy.version"
    }

    fun addDependency(project: Project) {
        project.dependencies.add(
            "compileOnly",
            BYTE_BUDDY_DEPENDENCY_FORMAT.format(getByteBuddyVersion(project))
        )
    }

    private fun getByteBuddyVersion(project: Project): String {
        return getPropertyByteBuddyVersion(project) ?: Constants.BYTE_BUDDY_DEPENDENCY_VERSION
    }

    private fun getPropertyByteBuddyVersion(project: Project): String? {
        val propertyVersion = project.properties[BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME]

        if (propertyVersion != null && propertyVersion is String) {
            return propertyVersion.trim()
        }

        return null
    }
}