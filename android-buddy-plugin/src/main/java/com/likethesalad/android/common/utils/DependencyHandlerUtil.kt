package com.likethesalad.android.common.utils

import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DependencyHandlerUtil @Inject constructor(
    projectDependencyToolsProvider: ProjectDependencyToolsProvider
) {

    private val dependencyHandler = projectDependencyToolsProvider.getDependencyHandler()
    private val repositoryHandler = projectDependencyToolsProvider.getRepositoryHandler()

    companion object {
        private const val GRADLE_LOGGING_DEPENDENCY_URI = "org.gradle:gradle-logging:4.10.1"
        private const val SLF4J_API_DEPENDENCY_URI = "org.slf4j:slf4j-api:1.7.30"
    }

    open fun addDependencies() {
        addGradleReleasesRepo()
        addImplementation(SLF4J_API_DEPENDENCY_URI)
        addImplementation(GRADLE_LOGGING_DEPENDENCY_URI)
    }

    protected fun addImplementation(dependencyUri: String) {
        dependencyHandler.add(
            "implementation", dependencyUri // todo use compileOnly
        )
    }

    private fun addGradleReleasesRepo() {
        repositoryHandler.maven {
            it.setUrl("https://repo.gradle.org/gradle/libs-releases-local/")
        }
    }
}