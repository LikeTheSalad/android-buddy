package com.likethesalad.android.common.providers

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler

interface ProjectDependencyToolsProvider {
    fun getDependencyHandler(): DependencyHandler

    fun getRepositoryHandler(): RepositoryHandler
}