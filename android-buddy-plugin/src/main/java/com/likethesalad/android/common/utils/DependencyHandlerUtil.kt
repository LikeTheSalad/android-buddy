package com.likethesalad.android.common.utils

import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DependencyHandlerUtil @Inject constructor(
    projectDependencyToolsProvider: ProjectDependencyToolsProvider
) {

    private val dependencyHandler = projectDependencyToolsProvider.getDependencyHandler()

    companion object {
        private const val SLF4J_API_DEPENDENCY_URI = "org.slf4j:slf4j-api:1.7.30"
    }

    open fun addDependencies() {
        addCompileOnly(SLF4J_API_DEPENDENCY_URI)
    }

    protected fun addCompileOnly(dependencyUri: String) {
        dependencyHandler.add(
            "compileOnly", dependencyUri
        )
    }
}