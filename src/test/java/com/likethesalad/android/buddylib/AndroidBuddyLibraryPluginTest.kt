package com.likethesalad.android.buddylib

import com.google.common.truth.Truth
import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.models.CreateJarDescriptionPropertiesArgs
import com.likethesalad.android.buddylib.tasks.CreateJarDescriptionProperties
import com.likethesalad.android.common.models.AndroidBuddyExtension
import com.likethesalad.android.common.utils.Constants
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.SourceSetOutput
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
    lateinit var androidBuddyExtension: AndroidBuddyExtension

    @MockK
    lateinit var sourceSetContainer: SourceSetContainer

    @MockK
    lateinit var mainSourceSet: SourceSet

    @MockK
    lateinit var mainSourceSetOutput: SourceSetOutput

    @MockK
    lateinit var tasks: TaskContainer

    @MockK
    lateinit var copyDescriptionPropertiesTask: Copy

    @MockK
    lateinit var copyDescriptionPropertiesTaskProvider: TaskProvider<Copy>

    @MockK
    lateinit var createJarDescriptionProperties: CreateJarDescriptionProperties

    @MockK
    lateinit var createJarDescriptionPropertiesProvider: TaskProvider<CreateJarDescriptionProperties>

    @MockK
    lateinit var createJarDescriptionPropertiesArgs: CreateJarDescriptionPropertiesArgs

    @MockK
    lateinit var javaProcessResourcesTask: Copy

    @MockK
    lateinit var javaProcessResourcesTaskProvider: TaskProvider<Copy>

    @MockK
    lateinit var jarTask: Task

    @MockK
    lateinit var jarTaskProvider: TaskProvider<Task>

    private val properties = mutableMapOf<String, Any?>()
    private val copyDescriptionPropertiesTaskRegisterActionCaptor = slot<Action<Copy>>()
    private val jarTaskNamedActionCaptor = slot<Action<Task>>()
    private val createJarDescriptionPropertiesName = "createJarDescriptionProperties"
    private val copyDescriptionTaskName = "copyDescriptionProperties"

    private lateinit var androidBuddyLibraryPlugin: AndroidBuddyLibraryPlugin

    @Before
    fun setUp() {
        mockkObject(LibraryInjector)
        every {
            LibraryInjector.getCreateJarDescriptionPropertiesArgs()
        }.returns(createJarDescriptionPropertiesArgs)
        every { project.pluginManager }.returns(pluginManager)
        every { project.dependencies }.returns(dependencies)
        every { project.properties }.returns(properties)
        every { project.extensions }.returns(extensions)
        every { project.tasks }.returns(tasks)
        every { dependencies.add(any(), any()) }.returns(mockk())
        every { extensions.create(any(), AndroidBuddyExtension::class.java) }.returns(androidBuddyExtension)
        every { extensions.getByType(SourceSetContainer::class.java) }.returns(sourceSetContainer)
        every {
            tasks.register(
                createJarDescriptionPropertiesName,
                CreateJarDescriptionProperties::class.java,
                createJarDescriptionPropertiesArgs
            )
        }.returns(createJarDescriptionPropertiesProvider)
        every {
            tasks.register(
                copyDescriptionTaskName,
                Copy::class.java,
                capture(copyDescriptionPropertiesTaskRegisterActionCaptor)
            )
        }.returns(copyDescriptionPropertiesTaskProvider)
        every { tasks.named("processResources", Copy::class.java) }.returns(javaProcessResourcesTaskProvider)
        every { tasks.named("jar", capture(jarTaskNamedActionCaptor)) }.returns(jarTaskProvider)
        every { jarTaskProvider.get() }.returns(jarTask)
        every { javaProcessResourcesTaskProvider.hint(Copy::class).get() }.returns(javaProcessResourcesTask)
        every {
            copyDescriptionPropertiesTaskProvider.hint(Copy::class).get()
        }.returns(copyDescriptionPropertiesTask)
        every {
            createJarDescriptionPropertiesProvider.hint(CreateJarDescriptionProperties::class).get()
        }.returns(createJarDescriptionProperties)
        every { sourceSetContainer.getByName("main") }.returns(mainSourceSet)
        every { mainSourceSet.output }.returns(mainSourceSetOutput)
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
    fun `Add java library plugin on apply`() {
        verify {
            pluginManager.apply(JavaLibraryPlugin::class.java)
        }
    }

    @Test
    fun `Apply bytebuddy implementation`() {
        verify {
            dependencies.add(
                "implementation",
                "net.bytebuddy:byte-buddy:${Constants.BYTE_BUDDY_DEPENDENCY_VERSION}"
            )
        }
    }

    @Test
    fun `Create android buddy extension`() {
        verify {
            extensions.create("androidBuddyLibrary", AndroidBuddyExtension::class.java)
        }
    }

    @Test
    fun `Create jar description properties generator task`() {
        val configureActionCaptor = slot<Action<CreateJarDescriptionProperties>>()
        val pluginNames = mockk<SetProperty<String>>()
        val outputClassesDirs = mockk<FileCollection>()
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
        every { mainSourceSetOutput.classesDirs }.returns(outputClassesDirs)
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
            createJarDescriptionProperties.inputClassPaths = outputClassesDirs
            outputDir.set(expectedOutputDir)
        }
    }

    @Test
    fun `Create copy description properties to generated resources task`() {
        val configurationAction = copyDescriptionPropertiesTaskRegisterActionCaptor.captured
        val javaPropertiesDestinationDir = mockk<File>()
        val javaPropertiesDestinationPath = "some/path"
        val expectedCopyDestinationPath = "$javaPropertiesDestinationPath/META-INF/android-buddy-plugins"
        val lambdaCaptor = slot<() -> String>()
        every { javaPropertiesDestinationDir.toString() }.returns(javaPropertiesDestinationPath)
        every { javaProcessResourcesTask.destinationDir }.returns(javaPropertiesDestinationDir)
        every { copyDescriptionPropertiesTask.from(any()) }.returns(copyDescriptionPropertiesTask)
        every { copyDescriptionPropertiesTask.into(any()) }.returns(copyDescriptionPropertiesTask)
        every { copyDescriptionPropertiesTask.dependsOn(any()) }.returns(mockk())

        configurationAction.execute(copyDescriptionPropertiesTask)

        verify {
            copyDescriptionPropertiesTask.from(createJarDescriptionPropertiesProvider)
            copyDescriptionPropertiesTask.into(capture(lambdaCaptor))
            copyDescriptionPropertiesTask.dependsOn(javaProcessResourcesTaskProvider)
        }

        Truth.assertThat(lambdaCaptor.captured.invoke()).isEqualTo(expectedCopyDestinationPath)
    }

    @Test
    fun `Add dependency for Jar task on the new copy description properties task`() {
        val jarConfiguration = jarTaskNamedActionCaptor.captured
        every { jarTask.dependsOn(any()) }.returns(jarTask)

        jarConfiguration.execute(jarTask)

        verify {
            jarTask.dependsOn(copyDescriptionPropertiesTaskProvider)
        }
    }

    @Test
    fun `Get project logger`() {
        val logger = mockk<Logger>()
        every { project.logger }.returns(logger)

        Truth.assertThat(androidBuddyLibraryPlugin.getLogger()).isEqualTo(logger)
    }
}