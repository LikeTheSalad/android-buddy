package com.likethesalad.android.common.utils

import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.junit.Before
import org.junit.Test

open class DependencyHandlerUtilTest : BaseMockable() {

    @MockK
    lateinit var dependencyHandler: DependencyHandler

    @MockK
    lateinit var gradleApiDependency: Dependency

    @MockK
    lateinit var repositoryHandler: RepositoryHandler

    @MockK
    lateinit var projectDependencyToolsProvider: ProjectDependencyToolsProvider

    @MockK
    lateinit var mavenArtifactRepository: MavenArtifactRepository

    private lateinit var dependencyHandlerUtil: DependencyHandlerUtil

    @Before
    open fun setUp() {
        every { dependencyHandler.add(any(), any()) }.returns(mockk())
        every { dependencyHandler.gradleApi() }.returns(gradleApiDependency)
        every { repositoryHandler.maven(any<Action<MavenArtifactRepository>>()) }.returns(mavenArtifactRepository)
        every { projectDependencyToolsProvider.getDependencyHandler() }.returns(dependencyHandler)
        every { projectDependencyToolsProvider.getRepositoryHandler() }.returns(repositoryHandler)
        dependencyHandlerUtil = initBaseDependencyHandlerUtil()
    }

    open fun initBaseDependencyHandlerUtil(): DependencyHandlerUtil {
        return DependencyHandlerUtil(projectDependencyToolsProvider)
    }

    @Test
    fun `Add slf4j api dependency`() {
        executeAddDependencies()

        verifyDependencyAdded("org.slf4j:slf4j-api:1.7.30")
    }

    protected fun verifyDependencyAdded(dependency: Any) {
        verify {
            dependencyHandler.add("compileOnly", dependency)
        }
    }

    protected fun executeAddDependencies() {
        dependencyHandlerUtil.addDependencies()
    }
}