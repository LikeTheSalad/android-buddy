package com.likethesalad.android.buddylib

import com.google.common.truth.Truth
import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.models.CreateJarDescriptionPropertiesArgs
import com.likethesalad.android.buddylib.tasks.CreateJarDescriptionProperties
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
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
    lateinit var createJarDescriptionProperties: CreateJarDescriptionProperties

    @MockK
    lateinit var createJarDescriptionPropertiesProvider: TaskProvider<CreateJarDescriptionProperties>

    @MockK
    lateinit var createJarDescriptionPropertiesArgs: CreateJarDescriptionPropertiesArgs

    @MockK
    lateinit var dependencyHandlerUtil: DependencyHandlerUtil

    @MockK
    lateinit var repositoryHandler: RepositoryHandler

    private val createJarDescriptionPropertiesName = "createJarDescriptionProperties"

    private lateinit var androidBuddyLibraryPlugin: AndroidBuddyLibraryPlugin

    @Before
    fun setUp() {
        mockkObject(LibraryInjector)
        every {
            LibraryInjector.getCreateJarDescriptionPropertiesArgs()
        }.returns(createJarDescriptionPropertiesArgs)
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
        every {
            tasks.register(
                createJarDescriptionPropertiesName,
                CreateJarDescriptionProperties::class.java,
                createJarDescriptionPropertiesArgs
            )
        }.returns(createJarDescriptionPropertiesProvider)
        every {
            createJarDescriptionPropertiesProvider.hint(CreateJarDescriptionProperties::class).get()
        }.returns(createJarDescriptionProperties)
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
    fun `Create jar description properties generator task`() {
        val configureActionCaptor = slot<Action<CreateJarDescriptionProperties>>()
        val pluginNames = mockk<SetProperty<String>>()
        val outputDir = mockk<DirectoryProperty>()
        val projectBuildDir = mockk<File>()
        val projectBuildDirPath = "some/path"
        val expectedOutputDirPath = "$projectBuildDirPath/$createJarDescriptionPropertiesName"
        val expectedOutputDir = mockk<File>()
        every { project.buildDir }.returns(projectBuildDir)
        every { project.file(expectedOutputDirPath) }.returns(expectedOutputDir)
        every { createJarDescriptionProperties.name }.returns(createJarDescriptionPropertiesName)
        every { projectBuildDir.toString() }.returns(projectBuildDirPath)
        every { createJarDescriptionProperties.outputDir }.returns(outputDir)
        every { androidBuddyExtension.pluginNames }.returns(pluginNames)
        verify {
            tasks.register(
                createJarDescriptionPropertiesName,
                CreateJarDescriptionProperties::class.java,
                createJarDescriptionPropertiesArgs
            )
            createJarDescriptionPropertiesProvider.configure(capture(configureActionCaptor))
        }

        configureActionCaptor.captured.execute(createJarDescriptionProperties)
        verify {
            createJarDescriptionProperties.inputClassNames = pluginNames
            outputDir.set(expectedOutputDir)
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