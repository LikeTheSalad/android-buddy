package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.plugin.generated.BuildConfig
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

class AppDependencyHandlerUtilTest : BaseMockable() {

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

    private lateinit var dependencyHandlerUtil: AppDependencyHandlerUtil

    @Before
    fun setUp() {
        every { dependencyHandler.add(any(), any()) }.returns(mockk())
        every { dependencyHandler.gradleApi() }.returns(gradleApiDependency)
        every { repositoryHandler.maven(any<Action<MavenArtifactRepository>>()) }.returns(mavenArtifactRepository)
        every { projectDependencyToolsProvider.getDependencyHandler() }.returns(dependencyHandler)
        every { projectDependencyToolsProvider.getRepositoryHandler() }.returns(repositoryHandler)
        dependencyHandlerUtil = AppDependencyHandlerUtil(projectDependencyToolsProvider)
    }

    @Test
    fun `Add android buddy tools dependency with plugin version`() {
        dependencyHandlerUtil.addDependencies()

        verifyAndroidBuddyToolsDependencyAdded()
    }

    @Test
    fun `Add gradle logging dependency`() {
        dependencyHandlerUtil.addDependencies()

        verifyDependencyAdded("org.gradle:gradle-logging:4.10.1")
    }

    @Test
    fun `Add slf4j api dependency`() {
        dependencyHandlerUtil.addDependencies()

        verifyDependencyAdded("org.slf4j:slf4j-api:1.7.30")
    }

    @Test
    fun `Add gradle releases maven repo`() {
        val mavenAction = slot<Action<MavenArtifactRepository>>()

        dependencyHandlerUtil.addDependencies()

        verify {
            repositoryHandler.maven(capture(mavenAction))
        }
        mavenAction.captured.execute(mavenArtifactRepository)
        verify {
            mavenArtifactRepository.setUrl("https://repo.gradle.org/gradle/libs-releases-local/")
        }
    }

    private fun verifyAndroidBuddyToolsDependencyAdded() {
        val dependencyUri = BuildConfig.ANDROID_BUDDY_TOOLS_URI
        Truth.assertThat(dependencyUri).startsWith("com.likethesalad.android:android-buddy-tools:")
        verifyDependencyAdded(dependencyUri)
    }

    private fun verifyDependencyAdded(dependency: Any) {
        verify {
            dependencyHandler.add("compileOnly", dependency)
        }
    }
}