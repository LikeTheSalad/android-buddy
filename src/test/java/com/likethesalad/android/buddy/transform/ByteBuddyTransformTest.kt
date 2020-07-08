package com.likethesalad.android.buddy.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.ClassFileLocatorMaker
import com.likethesalad.android.buddy.bytebuddy.PluginEngineProvider
import com.likethesalad.android.buddy.bytebuddy.PluginFactoriesProvider
import com.likethesalad.android.buddy.bytebuddy.SourceForMultipleFolders
import com.likethesalad.android.buddy.bytebuddy.SourceForMultipleFoldersFactory
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.utils.FilesHolder
import com.likethesalad.android.buddy.utils.FilesHolderFactory
import com.likethesalad.android.buddy.utils.TransformInvocationDataExtractor
import com.likethesalad.android.buddy.utils.TransformInvocationDataExtractorFactory
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import org.junit.Before
import org.junit.Test
import java.io.File

class ByteBuddyTransformTest : BaseMockable() {

    @MockK
    lateinit var classFileLocatorMaker: ClassFileLocatorMaker

    @MockK
    lateinit var pluginFactoriesProvider: PluginFactoriesProvider

    @MockK
    lateinit var pluginEngineProvider: PluginEngineProvider

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var sourceForMultipleFoldersFactory: SourceForMultipleFoldersFactory

    @MockK
    lateinit var transformInvocationDataExtractorFactory: TransformInvocationDataExtractorFactory

    @MockK
    lateinit var filesHolderFactory: FilesHolderFactory

    private lateinit var byteBuddyTransform: ByteBuddyTransform

    @Before
    fun setUp() {
        byteBuddyTransform = ByteBuddyTransform(
            classFileLocatorMaker, pluginFactoriesProvider, pluginEngineProvider,
            byteBuddyClassesInstantiator, sourceForMultipleFoldersFactory,
            transformInvocationDataExtractorFactory, filesHolderFactory
        )
    }

    @Test
    fun `Get name`() {
        Truth.assertThat(byteBuddyTransform.name).isEqualTo("Android ByteBuddy Transform")
    }

    @Test
    fun `Get input types`() {
        Truth.assertThat(byteBuddyTransform.inputTypes).containsExactly(
            QualifiedContent.DefaultContentType.CLASSES
        )
    }

    @Test
    fun `Get scopes`() {
        Truth.assertThat(byteBuddyTransform.scopes).containsExactly(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    @Test
    fun `Do transform`() {
        val transformInvocation = mockk<TransformInvocation>()
        val transformInvocationDataExtractor = mockk<TransformInvocationDataExtractor>()
        val classpath = setOf<File>()
        val folders = setOf<File>()
        val outputFolder = mockk<File>()
        val filesHolder = mockk<FilesHolder>()
        val pluginEngine = mockk<Plugin.Engine>()
        val classFileLocator = mockk<ClassFileLocator>()
        val source = mockk<SourceForMultipleFolders>()
        val target = mockk<Plugin.Engine.Target>()
        val factories = listOf<Plugin.Factory>()
        every {
            transformInvocationDataExtractorFactory.create(transformInvocation)
        }.returns(transformInvocationDataExtractor)
        every {
            transformInvocationDataExtractor.getClasspath()
        }.returns(classpath)
        every {
            transformInvocationDataExtractor.getOutputFolder()
        }.returns(outputFolder)
        every { filesHolderFactory.create(classpath) }.returns(filesHolder)
        every { filesHolder.dirs }.returns(folders)
        every { filesHolder.allFiles }.returns(classpath)
        every { pluginEngineProvider.makeEngine() }.returns(pluginEngine)
        every { classFileLocatorMaker.make(classpath) }.returns(classFileLocator)
        every { sourceForMultipleFoldersFactory.create(folders) }.returns(source)
        every { byteBuddyClassesInstantiator.makeTargetForFolder(outputFolder) }.returns(target)
        every { pluginEngine.with(any<ClassFileLocator>()) }.returns(pluginEngine)
        every { pluginFactoriesProvider.getFactories(folders) }.returns(factories)
        every {
            pluginEngine.apply(any(), any<Plugin.Engine.Target>(), any<List<Plugin.Factory>>())
        }.returns(mockk())
        every { transformInvocation.context }.returns(mockk())
        every { transformInvocation.inputs }.returns(mockk())
        every { transformInvocation.referencedInputs }.returns(mockk())
        every { transformInvocation.outputProvider }.returns(mockk())
        every { transformInvocation.isIncremental }.returns(false)

        byteBuddyTransform.transform(transformInvocation)

        verify {
            pluginEngine.with(classFileLocator)
            pluginEngine.apply(source, target, factories)
        }
    }
}