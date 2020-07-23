package com.likethesalad.android.buddylib

import com.google.common.truth.Truth
import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.PluginManager
import org.junit.Before
import org.junit.Test

class AndroidBuddyLibraryPluginTest : BaseMockable() {

    @MockK
    lateinit var project: Project

    @MockK
    lateinit var pluginManager: PluginManager

    @MockK
    lateinit var dependencies: DependencyHandler

    @MockK
    lateinit var dependencyHandlerUtil: DependencyHandlerUtil

    @MockK
    lateinit var repositoryHandler: RepositoryHandler

    private lateinit var androidBuddyLibraryPlugin: AndroidBuddyLibraryPlugin

    @Before
    fun setUp() {
        mockkObject(LibraryInjector)
        every {
            LibraryInjector.getDependencyHandlerUtil()
        }.returns(dependencyHandlerUtil)
        every { project.pluginManager }.returns(pluginManager)
        every { project.dependencies }.returns(dependencies)
        every { project.repositories }.returns(repositoryHandler)
        every { dependencies.add(any(), any()) }.returns(mockk())
        androidBuddyLibraryPlugin = AndroidBuddyLibraryPlugin()
        androidBuddyLibraryPlugin.apply(project)
    }

    @Test
    fun `Initiate injection on apply`() {
        verify {
            LibraryInjector.init(androidBuddyLibraryPlugin)
        }
    }

    @Test
    fun `Apply dependencies`() {
        verify {
            dependencyHandlerUtil.addDependencies()
        }
    }

    @Test
    fun `Get project logger`() {
        val logger = mockk<Logger>()
        every { project.logger }.returns(logger)

        Truth.assertThat(androidBuddyLibraryPlugin.getLogger()).isEqualTo(logger)
    }

    @Test
    fun `Provide DependencyHandler`() {
        Truth.assertThat(androidBuddyLibraryPlugin.getDependencyHandler()).isEqualTo(dependencies)
    }

    @Test
    fun `Provide RepositoryHandler`() {
        Truth.assertThat(androidBuddyLibraryPlugin.getRepositoryHandler()).isEqualTo(repositoryHandler)
    }
}