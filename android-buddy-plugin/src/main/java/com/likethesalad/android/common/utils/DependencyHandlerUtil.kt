package com.likethesalad.android.common.utils

import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import org.gradle.api.artifacts.dsl.DependencyHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyHandlerUtil @Inject constructor(
    projectDependencyToolsProvider: ProjectDependencyToolsProvider
) {

    private val dependencyHandler = projectDependencyToolsProvider.getDependencyHandler()
    private val repositoryHandler = projectDependencyToolsProvider.getRepositoryHandler()

    companion object {
        private const val BYTE_BUDDY_DEPENDENCY_FORMAT = "net.bytebuddy:byte-buddy:%s"
        private const val BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME = "android.buddy.byteBuddy.version"
        private const val GRADLE_LOGGING_DEPENDENCY_URI = "org.gradle:gradle-logging:4.10.1"
        private const val SLF4J_API_DEPENDENCY_URI = "org.slf4j:slf4j-api:1.7.30"
    }

    fun addDependencies(projectProperties: Map<String, Any?>) {
        addGradleReleasesRepo()
        addCompileOnly(dependencyHandler, BYTE_BUDDY_DEPENDENCY_FORMAT.format(getByteBuddyVersion(projectProperties)))
        addCompileOnly(dependencyHandler, SLF4J_API_DEPENDENCY_URI)
        addCompileOnly(dependencyHandler, GRADLE_LOGGING_DEPENDENCY_URI)
    }

    private fun addCompileOnly(dependencyHandler: DependencyHandler, dependencyUri: String) {
        dependencyHandler.add(
            "compileOnly", dependencyUri
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

    private fun addGradleReleasesRepo() {
        repositoryHandler.maven {
            it.setUrl("https://repo.gradle.org/gradle/libs-releases-local/")
        }
    }
}