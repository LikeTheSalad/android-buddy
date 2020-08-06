package com.likethesalad.android.buddy.transform

import com.android.build.api.transform.Context
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.bytebuddy.ClassFileLocatorMaker
import com.likethesalad.android.buddy.bytebuddy.CompoundSource
import com.likethesalad.android.buddy.bytebuddy.CompoundSourceFactory
import com.likethesalad.android.buddy.bytebuddy.PluginEngineProvider
import com.likethesalad.android.buddy.bytebuddy.PluginFactoriesProvider
import com.likethesalad.android.buddy.bytebuddy.SourceOriginForMultipleFolders
import com.likethesalad.android.buddy.bytebuddy.SourceOriginForMultipleFoldersFactory
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationLibrariesJarsProvider
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationLibrariesJarsProviderFactory
import com.likethesalad.android.buddy.modules.transform.ByteBuddyTransform
import com.likethesalad.android.buddy.modules.transform.TransformInvocationDataExtractor
import com.likethesalad.android.buddy.modules.transform.TransformInvocationDataExtractorFactory
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.providers.impl.DefaultLibrariesJarsProvider
import com.likethesalad.android.buddy.providers.impl.DefaultLibrariesJarsProviderFactory
import com.likethesalad.android.buddy.utils.AndroidVariantDataProviderFactory
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.buddy.utils.FilesHolder
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import com.likethesalad.android.common.utils.android.AndroidVariantDataProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.ByteBuddy
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
    lateinit var sourceOriginForMultipleFoldersFactory: SourceOriginForMultipleFoldersFactory

    @MockK
    lateinit var transformInvocationDataExtractorFactory: TransformInvocationDataExtractorFactory

    @MockK
    lateinit var androidVariantDataProviderFactory: AndroidVariantDataProviderFactory

    @MockK
    lateinit var compoundSourceFactory: CompoundSourceFactory

    @MockK
    lateinit var directoryCleaner: DirectoryCleaner

    @MockK
    lateinit var classLoaderCreator: ClassLoaderCreator

    @MockK
    lateinit var androidExtensionDataProvider: AndroidExtensionDataProvider

    @MockK
    lateinit var defaultLibrariesJarsProviderFactory: DefaultLibrariesJarsProviderFactory

    @MockK
    lateinit var customConfigurationLibrariesJarsProviderFactory: CustomConfigurationLibrariesJarsProviderFactory

    @MockK
    lateinit var pluginConfiguration: AndroidBuddyPluginConfiguration

    @MockK
    lateinit var transformInvocation: TransformInvocation

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var transformInvocationDataExtractor: TransformInvocationDataExtractor

    @MockK
    lateinit var androidVariantDataProvider: AndroidVariantDataProvider

    @MockK
    lateinit var pluginEngine: Plugin.Engine

    private val variantName = "someName"
    private val javaTargetVersion = 8
    private lateinit var androidBoothClasspath: List<File>
    private lateinit var byteBuddyTransform: ByteBuddyTransform

    @Before
    fun setUp() {
        androidBoothClasspath = listOf(mockk(), mockk())
        every {
            transformInvocationDataExtractorFactory.create(transformInvocation)
        }.returns(transformInvocationDataExtractor)
        every { context.variantName }.returns(variantName)
        every { androidVariantDataProviderFactory.create(variantName) }.returns(androidVariantDataProvider)
        every { androidExtensionDataProvider.getBootClasspath() }.returns(androidBoothClasspath)
        every { androidVariantDataProvider.getJavaTargetCompatibilityVersion() }.returns(javaTargetVersion)
        every { pluginEngineProvider.makeEngine(javaTargetVersion) }.returns(pluginEngine)
        byteBuddyTransform = ByteBuddyTransform(
            classFileLocatorMaker,
            pluginFactoriesProvider,
            pluginEngineProvider,
            byteBuddyClassesInstantiator,
            sourceOriginForMultipleFoldersFactory,
            transformInvocationDataExtractorFactory,
            compoundSourceFactory,
            classLoaderCreator,
            directoryCleaner,
            androidVariantDataProviderFactory,
            androidExtensionDataProvider,
            defaultLibrariesJarsProviderFactory,
            customConfigurationLibrariesJarsProviderFactory,
            pluginConfiguration
        )
    }

    @Test
    fun `Get name`() {
        Truth.assertThat(byteBuddyTransform.name).isEqualTo("androidBuddy")
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
            QualifiedContent.Scope.PROJECT
        )
    }

    @Test
    fun `Do transform with default libraries jar provider`() {
        every { pluginConfiguration.useOnlyAndroidBuddyImplementations() }.returns(false)

        verifyTransform(true)
    }

    @Test
    fun `Do transform with custom config libraries jar provider`() {
        every { pluginConfiguration.useOnlyAndroidBuddyImplementations() }.returns(true)

        verifyTransform(false)
    }

    private fun verifyTransform(withAllLibrariesJarsProvider: Boolean) {
        val factoriesClassLoader = mockk<ClassLoader>()
        val folder1 = mockk<File>()
        val folders = setOf(folder1)
        val jarFile1 = mockk<File>()
        val jarFile2 = mockk<File>()
        val jarFiles = setOf(jarFile1, jarFile2)
        val allFiles = folders + jarFiles
        val outputFolder = mockk<File>()
        val filesHolder = mockk<FilesHolder>()
        val transformScopes = mutableSetOf(QualifiedContent.Scope.PROJECT)
        val classFileLocator = mockk<ClassFileLocator>()
        val compoundSource = mockk<CompoundSource>()
        val jarOrigin1 = mockk<Plugin.Engine.Source.Origin.ForJarFile>()
        val jarOrigin2 = mockk<Plugin.Engine.Source.Origin.ForJarFile>()
        val foldersOrigin = mockk<SourceOriginForMultipleFolders>()
        val javaClasspathFile1 = mockk<File>()
        val javaClasspathFile2 = mockk<File>()
        val javaClasspath = setOf(javaClasspathFile1, javaClasspathFile2, folder1)
        val extraClasspath = mutableSetOf(javaClasspathFile1, javaClasspathFile2)
        extraClasspath.addAll(androidBoothClasspath)
        val target = mockk<Plugin.Engine.Target>()
        val factories = listOf<Plugin.Factory>()
        val librariesJarsProvider: LibrariesJarsProvider = if (withAllLibrariesJarsProvider) {
            val response = mockk<DefaultLibrariesJarsProvider>()
            every {
                defaultLibrariesJarsProviderFactory.create(extraClasspath)
            }.returns(response)
            response
        } else {
            val response = mockk<CustomConfigurationLibrariesJarsProvider>()
            every {
                customConfigurationLibrariesJarsProviderFactory.create(androidVariantDataProvider)
            }.returns(response)
            response
        }
        every {
            classLoaderCreator.create(allFiles + extraClasspath, ByteBuddy::class.java.classLoader)
        }.returns(factoriesClassLoader)
        every { androidVariantDataProvider.getJavaClassPath() }.returns(javaClasspath)
        every {
            transformInvocationDataExtractor.getScopeClasspath()
        }.returns(filesHolder)
        every {
            transformInvocationDataExtractor.getOutputFolder(transformScopes)
        }.returns(outputFolder)
        every { filesHolder.dirFiles }.returns(folders)
        every { filesHolder.jarFiles }.returns(jarFiles)
        every { filesHolder.allFiles }.returns(allFiles)
        every { classFileLocatorMaker.make(extraClasspath) }.returns(classFileLocator)
        every { sourceOriginForMultipleFoldersFactory.create(folders) }.returns(foldersOrigin)
        every { byteBuddyClassesInstantiator.makeJarFileSourceOrigin(jarFile1) }.returns(jarOrigin1)
        every { byteBuddyClassesInstantiator.makeJarFileSourceOrigin(jarFile2) }.returns(jarOrigin2)
        every {
            compoundSourceFactory.create(setOf(foldersOrigin, jarOrigin1, jarOrigin2))
        }.returns(compoundSource)
        every { byteBuddyClassesInstantiator.makeTargetForFolder(outputFolder) }.returns(target)
        every { pluginEngine.with(any<ClassFileLocator>()) }.returns(pluginEngine)
        every {
            pluginFactoriesProvider.getFactories(
                folders,
                librariesJarsProvider,
                factoriesClassLoader
            )
        }.returns(factories)
        every {
            pluginEngine.apply(any(), any<Plugin.Engine.Target>(), any<List<Plugin.Factory>>())
        }.returns(mockk())
        every { transformInvocation.context }.returns(context)
        every { transformInvocation.inputs }.returns(mockk())
        every { transformInvocation.referencedInputs }.returns(mockk())
        every { transformInvocation.outputProvider }.returns(mockk())
        every { transformInvocation.isIncremental }.returns(false)

        byteBuddyTransform.transform(transformInvocation)

        verify {
            directoryCleaner.cleanDirectory(outputFolder)
            pluginEngine.with(classFileLocator)
            pluginEngine.apply(compoundSource, target, factories)
        }
    }
}