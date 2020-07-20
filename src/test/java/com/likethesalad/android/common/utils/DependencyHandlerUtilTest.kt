package com.likethesalad.android.common.utils

import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.junit.Before
import org.junit.Test

class DependencyHandlerUtilTest : BaseMockable() {

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
    fun setUp() {
        every { dependencyHandler.add(any(), any()) }.returns(mockk())
        every { dependencyHandler.gradleApi() }.returns(gradleApiDependency)
        every { repositoryHandler.maven(any<Action<MavenArtifactRepository>>()) }.returns(mavenArtifactRepository)
        every { projectDependencyToolsProvider.getDependencyHandler() }.returns(dependencyHandler)
        every { projectDependencyToolsProvider.getRepositoryHandler() }.returns(repositoryHandler)
        dependencyHandlerUtil = DependencyHandlerUtil(projectDependencyToolsProvider)
    }

    @Test
    fun `Add byte buddy dependency with default version`() {
        dependencyHandlerUtil.addDependencies(mapOf())

        verifyByteBuddyDependencyAdded(Constants.BYTE_BUDDY_DEPENDENCY_VERSION)
    }

    @Test
    fun `Add byte buddy dependency with properties version`() {
        val propertiesVersion = "someVersion"
        val properties = mapOf<String, Any?>("android.buddy.byteBuddy.version" to propertiesVersion)

        dependencyHandlerUtil.addDependencies(properties)

        verifyByteBuddyDependencyAdded(propertiesVersion)
    }

    @Test
    fun `Add gradle logging dependency`() {
        dependencyHandlerUtil.addDependencies(emptyMap())

        verifyGradleLoggingDependencyAdded()
    }

    @Test
    fun `Add gradle releases maven repo`() {
        val mavenAction = slot<Action<MavenArtifactRepository>>()

        dependencyHandlerUtil.addDependencies(emptyMap())

        verify {
            repositoryHandler.maven(capture(mavenAction))
        }
        mavenAction.captured.execute(mavenArtifactRepository)
        verify {
            mavenArtifactRepository.setUrl("https://repo.gradle.org/gradle/libs-releases-local/")
        }
    }

    private fun verifyByteBuddyDependencyAdded(version: String) {
        verifyDependencyAdded("net.bytebuddy:byte-buddy:$version")
    }

    private fun verifyGradleLoggingDependencyAdded() {
        verifyDependencyAdded("org.gradle:gradle-logging:4.10.1")
    }

    private fun verifyDependencyAdded(dependency: Any) {
        verify {
            dependencyHandler.add("compileOnly", dependency)
        }
    }
}