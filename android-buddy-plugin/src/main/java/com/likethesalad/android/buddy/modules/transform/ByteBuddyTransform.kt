package com.likethesalad.android.buddy.modules.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.likethesalad.android.buddy.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.bytebuddy.ClassFileLocatorMaker
import com.likethesalad.android.buddy.bytebuddy.CompoundSource
import com.likethesalad.android.buddy.bytebuddy.CompoundSourceFactory
import com.likethesalad.android.buddy.bytebuddy.PluginEngineProvider
import com.likethesalad.android.buddy.bytebuddy.PluginFactoriesProvider
import com.likethesalad.android.buddy.bytebuddy.SourceOriginForMultipleFoldersFactory
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationLibrariesJarsProviderFactory
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.providers.impl.DefaultLibrariesJarsProviderFactory
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.buddy.utils.FilesHolder
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import com.likethesalad.android.common.utils.android.AndroidVariantDataProvider
import com.likethesalad.android.common.utils.android.AndroidVariantDataProviderFactory
import net.bytebuddy.ByteBuddy
import net.bytebuddy.build.Plugin
import java.io.File
import javax.inject.Inject

@AppScope
class ByteBuddyTransform @Inject constructor(
    private val classFileLocatorMaker: ClassFileLocatorMaker,
    private val pluginFactoriesProvider: PluginFactoriesProvider,
    private val pluginEngineProvider: PluginEngineProvider,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val sourceOriginForMultipleFoldersFactory: SourceOriginForMultipleFoldersFactory,
    private val transformInvocationDataExtractorFactory: TransformInvocationDataExtractorFactory,
    private val compoundSourceFactory: CompoundSourceFactory,
    private val classLoaderCreator: ClassLoaderCreator,
    private val directoryCleaner: DirectoryCleaner,
    private val androidVariantDataProviderFactory: AndroidVariantDataProviderFactory,
    private val androidExtensionDataProvider: AndroidExtensionDataProvider,
    private val defaultLibrariesJarsProviderFactory: DefaultLibrariesJarsProviderFactory,
    private val customConfigurationLibrariesJarsProviderFactory: CustomConfigurationLibrariesJarsProviderFactory,
    private val pluginConfiguration: AndroidBuddyPluginConfiguration
) : Transform() {

    override fun getName(): String = "androidBuddy"

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return setOf(QualifiedContent.DefaultContentType.CLASSES)
    }

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(
            QualifiedContent.Scope.PROJECT
        )
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        val variantName = transformInvocation.context.variantName
        val androidDataProvider = androidVariantDataProviderFactory.create(variantName)
        val transformInvocationDataExtractor = transformInvocationDataExtractorFactory.create(transformInvocation)
        val scopeClasspath = transformInvocationDataExtractor.getScopeClasspath()
        val extraClasspath = transformInvocationDataExtractor.getReferenceClasspath() +
                androidExtensionDataProvider.getBootClasspath()
        val outputFolder = transformInvocationDataExtractor.getOutputFolder(scopes)
        val factoriesClassLoader = createFactoriesClassLoader(scopeClasspath, extraClasspath)

        directoryCleaner.cleanDirectory(outputFolder)

        pluginEngineProvider.makeEngine(androidDataProvider.getJavaTargetCompatibilityVersion())
            .with(classFileLocatorMaker.make(extraClasspath))
            .apply(
                getCompoundSource(scopeClasspath),
                byteBuddyClassesInstantiator.makeTargetForFolder(outputFolder),
                pluginFactoriesProvider.getFactories(
                    scopeClasspath.dirFiles,
                    getLibrariesJarsProvider(androidDataProvider, extraClasspath),
                    factoriesClassLoader
                )
            )
    }

    private fun getCompoundSource(scopeClasspath: FilesHolder): CompoundSource {
        val origins = mutableSetOf<Plugin.Engine.Source.Origin>()
        origins.add(sourceOriginForMultipleFoldersFactory.create(scopeClasspath.dirFiles))
        for (jarFile in scopeClasspath.jarFiles) {
            origins.add(byteBuddyClassesInstantiator.makeJarFileSourceOrigin(jarFile))
        }

        return compoundSourceFactory.create(origins)
    }

    private fun createFactoriesClassLoader(
        scopeClasspath: FilesHolder,
        extraClasspath: Set<File>
    ): ClassLoader {
        return classLoaderCreator.create(
            scopeClasspath.allFiles + extraClasspath,
            ByteBuddy::class.java.classLoader
        )
    }

    private fun getLibrariesJarsProvider(
        androidVariantDataProvider: AndroidVariantDataProvider,
        extraClasspath: Set<File>
    ): LibrariesJarsProvider {
        return if (pluginConfiguration.useOnlyAndroidBuddyImplementations()) {
            customConfigurationLibrariesJarsProviderFactory.create(androidVariantDataProvider)
        } else {
            defaultLibrariesJarsProviderFactory.create(extraClasspath)
        }
    }
}