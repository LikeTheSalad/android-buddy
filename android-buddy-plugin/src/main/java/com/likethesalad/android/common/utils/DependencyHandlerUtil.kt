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
        private const val GRADLE_LOGGING_DEPENDENCY_URI = "org.gradle:gradle-logging:4.10.1"
        private const val SLF4J_API_DEPENDENCY_URI = "org.slf4j:slf4j-api:1.7.30"
    }

    fun addDependencies() {
        addGradleReleasesRepo()
        addCompileOnly(dependencyHandler, Constants.ANDROID_BUDDY_TOOLS_DEPENDENCY_URI)
        addCompileOnly(dependencyHandler, SLF4J_API_DEPENDENCY_URI)
        addCompileOnly(dependencyHandler, GRADLE_LOGGING_DEPENDENCY_URI)
    }

    private fun addCompileOnly(dependencyHandler: DependencyHandler, dependencyUri: String) {
        dependencyHandler.add(
            "compileOnly", dependencyUri
        )
    }

    private fun addGradleReleasesRepo() {
        repositoryHandler.maven {
            it.setUrl("https://repo.gradle.org/gradle/libs-releases-local/")
        }
    }
}