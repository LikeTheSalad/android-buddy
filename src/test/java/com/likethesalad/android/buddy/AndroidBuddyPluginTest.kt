package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.TransformationLogger
import com.likethesalad.android.buddy.bytebuddy.TransformationLoggerFactory
import com.likethesalad.android.buddy.di.AppInjector
import com.likethesalad.android.buddy.transform.ByteBuddyTransform
import com.likethesalad.android.common.models.AndroidBuddyExtension
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.SetProperty
import org.junit.Before
import org.junit.Test
import java.io.File

@Suppress("UnstableApiUsage")
class AndroidBuddyPluginTest : BaseMockable() {

    @MockK
    lateinit var androidExtension: AppExtension

    @MockK
    lateinit var project: Project

    @MockK
    lateinit var extensionContainer: ExtensionContainer

    @MockK
    lateinit var androidBuddyExtension: AndroidBuddyExtension

    @MockK
    lateinit var byteBuddyTransform: ByteBuddyTransform

    @MockK
    lateinit var transformationLoggerFactory: TransformationLoggerFactory

    private lateinit var androidBuddyPlugin: AndroidBuddyPlugin

    @Before
    fun setUp() {
        mockkObject(AppInjector)
        every { project.extensions }.returns(extensionContainer)
        every { extensionContainer.getByType(AppExtension::class.java) }.returns(androidExtension)
        every {
            extensionContainer.create("androidBuddy", AndroidBuddyExtension::class.java)
        }.returns(androidBuddyExtension)
        every { AppInjector.getByteBuddyTransform() }.returns(byteBuddyTransform)
        every { AppInjector.getTransformationLoggerFactory() }.returns(transformationLoggerFactory)

        androidBuddyPlugin = AndroidBuddyPlugin()
        androidBuddyPlugin.apply(project)
    }

    @Test
    fun `Check injector is initiated`() {
        verify {
            AppInjector.init(androidBuddyPlugin)
        }
    }

    @Test
    fun `Verify extension is created`() {
        verify {
            extensionContainer.create("androidBuddy", AndroidBuddyExtension::class.java)
        }
    }

    @Test
    fun `Get android booth classpath`() {
        val bootClasspath = mutableListOf<File>(mockk())
        every {
            androidExtension.bootClasspath
        }.returns(bootClasspath)

        Truth.assertThat(androidBuddyPlugin.getBootClasspath()).isEqualTo(bootClasspath.toSet())
    }

    @Test
    fun `Verify byte buddy transform registration`() {
        verify {
            androidExtension.registerTransform(byteBuddyTransform)
        }
    }

    @Test
    fun `Create file tree iterator for folder`() {
        val folder = mockk<File>()
        val fileTree = mockk<ConfigurableFileTree>()
        val expectedIterator = mockk<MutableIterator<File>>()
        every {
            project.fileTree(folder)
        }.returns(fileTree)
        every {
            fileTree.iterator()
        }.returns(expectedIterator)

        val result = androidBuddyPlugin.createFileTreeIterator(folder)

        Truth.assertThat(result).isEqualTo(expectedIterator)
    }

    @Test
    fun `Get plugin class names from extension`() {
        val plugins = setOf("some.class.name", "other.class.name")
        val setProperty = mockk<SetProperty<String>>()
        every { setProperty.get() }.returns(plugins)
        every {
            androidBuddyExtension.pluginNames
        }.returns(setProperty)

        Truth.assertThat(androidBuddyPlugin.getPluginClassNames()).isEqualTo(plugins.toSet())
    }

    @Test
    fun `Provide transformation logger`() {
        val projectLogger = mockk<Logger>()
        val transformationLogger = mockk<TransformationLogger>()
        every { project.logger }.returns(projectLogger)
        every { transformationLoggerFactory.create(projectLogger) }.returns(transformationLogger)

        Truth.assertThat(androidBuddyPlugin.getTransformationLogger()).isEqualTo(transformationLogger)
        verify {
            project.logger
            transformationLoggerFactory.create(projectLogger)
        }
    }
}