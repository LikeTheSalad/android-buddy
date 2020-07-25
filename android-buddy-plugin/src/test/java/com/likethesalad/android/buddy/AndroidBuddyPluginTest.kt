package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.di.AppInjector
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationCreator
import com.likethesalad.android.buddy.modules.transform.ByteBuddyTransform
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
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
    lateinit var byteBuddyTransform: ByteBuddyTransform

    @MockK
    lateinit var dependencyHandlerUtil: DependencyHandlerUtil

    @MockK
    lateinit var dependencyHandler: DependencyHandler

    @MockK
    lateinit var repositoryHandler: RepositoryHandler

    @MockK
    lateinit var configurationContainer: ConfigurationContainer

    @MockK
    lateinit var customConfigurationCreator: CustomConfigurationCreator

    private lateinit var androidBuddyPlugin: AndroidBuddyPlugin

    @Before
    fun setUp() {
        mockkObject(AppInjector)
        every { project.extensions }.returns(extensionContainer)
        every { project.dependencies }.returns(dependencyHandler)
        every { project.repositories }.returns(repositoryHandler)
        every { project.configurations }.returns(configurationContainer)
        every { extensionContainer.getByType(AppExtension::class.java) }.returns(androidExtension)
        every { AppInjector.getByteBuddyTransform() }.returns(byteBuddyTransform)
        every { AppInjector.getDependencyHandlerUtil() }.returns(dependencyHandlerUtil)
        every { AppInjector.getCustomConfigurationCreator() }.returns(customConfigurationCreator)

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
    fun `Provide project logger`() {
        val projectLogger = mockk<Logger>()
        every { project.logger }.returns(projectLogger)

        Truth.assertThat(androidBuddyPlugin.getLogger()).isEqualTo(projectLogger)
        verify {
            project.logger
        }
    }

    @Test
    fun `Apply bytebuddy dependency`() {
        verify {
            dependencyHandlerUtil.addDependencies()
        }
    }

    @Test
    fun `Provide DependencyHandler`() {
        Truth.assertThat(androidBuddyPlugin.getDependencyHandler()).isEqualTo(dependencyHandler)
    }

    @Test
    fun `Provide RepositoryHandler`() {
        Truth.assertThat(androidBuddyPlugin.getRepositoryHandler()).isEqualTo(repositoryHandler)
    }

    @Test
    fun `Provide android extension`() {
        Truth.assertThat(androidBuddyPlugin.getAndroidExtension()).isEqualTo(androidExtension)
    }

    @Test
    fun `Provide dependency configurations container`() {
        Truth.assertThat(androidBuddyPlugin.getConfigurationContainer()).isEqualTo(configurationContainer)
    }
}