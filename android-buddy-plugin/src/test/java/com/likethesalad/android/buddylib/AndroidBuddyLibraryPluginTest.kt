package com.likethesalad.android.buddylib

import com.android.build.gradle.LibraryExtension
import com.google.common.truth.Truth
import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
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
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskContainer
import org.junit.Assert.fail
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
    lateinit var extensions: ExtensionContainer

    @MockK
    lateinit var androidBuddyExtension: AndroidBuddyLibExtension

    @MockK
    lateinit var tasks: TaskContainer

    @MockK
    lateinit var dependencyHandlerUtil: DependencyHandlerUtil

    @MockK
    lateinit var repositoryHandler: RepositoryHandler

    @MockK
    lateinit var androidExtension: LibraryExtension

    private lateinit var androidBuddyLibraryPlugin: AndroidBuddyLibraryPlugin

    @Before
    fun setUp() {
        mockkObject(LibraryInjector)
        every {
            LibraryInjector.getDependencyHandlerUtil()
        }.returns(dependencyHandlerUtil)
        every { project.pluginManager }.returns(pluginManager)
        every { project.dependencies }.returns(dependencies)
        every { project.extensions }.returns(extensions)
        every { project.tasks }.returns(tasks)
        every { project.repositories }.returns(repositoryHandler)
        every { dependencies.add(any(), any()) }.returns(mockk())
        every { extensions.create(any(), AndroidBuddyLibExtension::class.java) }.returns(androidBuddyExtension)
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
    fun `Create android buddy extension`() {
        verify {
            extensions.create("androidBuddyLibrary", AndroidBuddyLibExtension::class.java)
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

    @Test
    fun `Get android extension`() {
        Truth.assertThat(androidBuddyLibraryPlugin.getAndroidExtension()).isEqualTo(androidExtension)
    }

    @Test
    fun `Get TaskContainer`() {
        Truth.assertThat(androidBuddyLibraryPlugin.getTaskContainer()).isEqualTo(tasks)
    }

    @Test
    fun `Get library extension`() {
        fail()
    }
}