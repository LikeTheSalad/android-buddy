package com.likethesalad.android.buddy.modules.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.likethesalad.android.buddy.bytebuddy.ClassFileLocatorMaker
import com.likethesalad.android.buddy.bytebuddy.CompoundSource
import com.likethesalad.android.buddy.bytebuddy.PluginEngineProvider
import com.likethesalad.android.buddy.bytebuddy.SourceOriginForMultipleFolders
import com.likethesalad.android.buddy.configuration.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.transform.utils.PluginFactoriesProvider
import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkipPolicy
import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkippedStrategy
import com.likethesalad.android.buddy.providers.impl.DefaultLibrariesJarsProvider
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.buddy.utils.FilesHolder
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import com.likethesalad.android.common.utils.android.AndroidVariantDataProvider
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
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
    private val sourceOriginForMultipleFoldersFactory: SourceOriginForMultipleFolders.Factory,
    private val transformInvocationDataExtractorFactory: TransformInvocationDataExtractor.Factory,
    private val compoundSourceFactory: CompoundSource.Factory,
    private val classLoaderCreator: ClassLoaderCreator,
    private val directoryCleaner: DirectoryCleaner,
    private val androidVariantDataProviderFactory: AndroidVariantDataProvider.Factory,
    private val androidExtensionDataProvider: AndroidExtensionDataProvider,
    private val defaultLibrariesJarsProviderFactory: DefaultLibrariesJarsProvider.Factory,
    private val androidBuddyPluginConfiguration: AndroidBuddyPluginConfiguration,
    private val sourceElementTransformationSkipPolicyFactory: SourceElementTransformationSkipPolicy.Factory,
    private val sourceElementTransformationSkippedStrategyFactory: SourceElementTransformationSkippedStrategy.Factory
) : Transform() {

    override fun getName(): String = "androidBuddy"

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return setOf(QualifiedContent.DefaultContentType.CLASSES)
    }

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return androidBuddyPluginConfiguration.getTransformationScope()
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    override fun getParameterInputs(): MutableMap<String, Any> {
        return mutableMapOf(
            "librariesScopeHash" to androidBuddyPluginConfiguration.getLibrariesScope().hashCode(),
            "transformationScopeHash" to androidBuddyPluginConfiguration.getTransformationScope().hashCode(),
            "excludePrefixesScopeHash" to androidBuddyPluginConfiguration.getExcludePrefixes().hashCode()
        )
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        val variantName = transformInvocation.context.variantName
        val androidDataProvider = androidVariantDataProviderFactory.create(variantName)
        val transformInvocationDataExtractor = transformInvocationDataExtractorFactory.create(transformInvocation)
        val scopeClasspath = transformInvocationDataExtractor.getScopeClasspath()
        val dependencies = transformInvocationDataExtractor.getReferenceClasspath()
        val systemClasspath = androidExtensionDataProvider.getBootClasspath().toSet()
        val outputFolder = transformInvocationDataExtractor.getOutputFolder(scopes)
        val mainClassLoader = createMainClassLoader(scopeClasspath, systemClasspath)

        directoryCleaner.cleanDirectory(outputFolder)
        pluginEngineProvider.makeEngine(androidDataProvider.getJavaTargetCompatibilityVersion())
            .with(classFileLocatorMaker.make(dependencies + systemClasspath))
            .apply(
                getCompoundSource(scopeClasspath, outputFolder),
                byteBuddyClassesInstantiator.makeTargetForFolder(outputFolder),
                pluginFactoriesProvider.getFactories(
                    scopeClasspath.dirFiles,
                    defaultLibrariesJarsProviderFactory.create(dependencies),
                    mainClassLoader
                )
            )
    }

    private fun getCompoundSource(scopeClasspath: FilesHolder, outputFolder: File): CompoundSource {

        val origins = mutableSetOf<Plugin.Engine.Source.Origin>()
        origins.add(sourceOriginForMultipleFoldersFactory.create(scopeClasspath.dirFiles))
        for (jarFile in scopeClasspath.jarFiles) {
            val origin = byteBuddyClassesInstantiator.makeJarFileSourceOrigin(jarFile)
            if (origin.iterator().hasNext()) {
                origins.add(origin)
            }
        }

        return compoundSourceFactory.create(
            origins,
            sourceElementTransformationSkipPolicyFactory.create(androidBuddyPluginConfiguration.getExcludePrefixes()),
            sourceElementTransformationSkippedStrategyFactory.create(outputFolder)
        )
    }


    private fun createMainClassLoader(
        scopeClasspath: FilesHolder,
        systemClasspath: Set<File>
    ): ClassLoader {
        val androidClassLoader = classLoaderCreator.create(
            systemClasspath,
            ByteBuddy::class.java.classLoader
        )
        return classLoaderCreator.create(
            scopeClasspath.allFiles,
            androidClassLoader
        )
    }
}