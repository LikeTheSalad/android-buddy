package com.likethesalad.android.buddylib

import com.android.build.gradle.LibraryExtension
import com.google.common.truth.Truth
import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.modules.createmetadata.CreateMetadataTaskGenerator
import com.likethesalad.android.buddylib.utils.LibDependencyHandlerUtil
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
import org.junit.Before
import org.junit.Test
import java.io.File

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
    lateinit var dependencyHandlerUtil: LibDependencyHandlerUtil

    @MockK
    lateinit var repositoryHandler: RepositoryHandler

    @MockK
    lateinit var androidExtension: LibraryExtension

    @MockK
    lateinit var createMetadataTaskGenerator: CreateMetadataTaskGenerator

    private lateinit var androidBuddyLibraryPlugin: AndroidBuddyLibraryPlugin

    @Before
    fun setUp() {
        mockkObject(LibraryInjector)
        every { LibraryInjector.getDependencyHandlerUtil() }.returns(dependencyHandlerUtil)
        every { LibraryInjector.getCreateMetadataTaskGenerator() }.returns(createMetadataTaskGenerator)
        every { project.pluginManager }.returns(pluginManager)
        every { project.dependencies }.returns(dependencies)
        every { project.extensions }.returns(extensions)
        every { project.tasks }.returns(tasks)
        every { project.repositories }.returns(repositoryHandler)
        every { dependencies.add(any(), any()) }.returns(mockk())
        every { extensions.create(any(), AndroidBuddyLibExtension::class.java) }.returns(androidBuddyExtension)
        every { extensions.getByType(LibraryExtension::class.java) }.returns(androidExtension)
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
    fun `Verify launched task per variant creation`() {
        verify {
            createMetadataTaskGenerator.createTaskPerVariant()
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
    fun `Get android buddy library extension`() {
        Truth.assertThat(androidBuddyLibraryPlugin.getExtension()).isEqualTo(androidBuddyExtension)
    }

    @Test
    fun `Create incremental dir`() {
        val dirName = "someName"
        val buildDirPath = "some/path"
        val expectedDir = mockk<File>()
        val buildDir = mockk<File>()
        every { buildDir.toString() }.returns(buildDirPath)
        every { project.file(any<String>()) }.returns(expectedDir)
        every { project.buildDir }.returns(buildDir)

        val result = androidBuddyLibraryPlugin.createIncrementalDir(dirName)

        Truth.assertThat(result).isEqualTo(expectedDir)
        verify {
            project.file("some/path/intermediates/incremental/someName")
        }
    }
}