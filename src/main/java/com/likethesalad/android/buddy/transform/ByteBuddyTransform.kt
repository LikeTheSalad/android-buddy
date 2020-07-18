package com.likethesalad.android.buddy.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.likethesalad.android.buddy.bytebuddy.ClassFileLocatorMaker
import com.likethesalad.android.buddy.bytebuddy.CompoundSource
import com.likethesalad.android.buddy.bytebuddy.CompoundSourceFactory
import com.likethesalad.android.buddy.bytebuddy.PluginEngineProvider
import com.likethesalad.android.buddy.bytebuddy.PluginFactoriesProvider
import com.likethesalad.android.buddy.bytebuddy.SourceOriginForMultipleFoldersFactory
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.AndroidPluginDataProvider
import com.likethesalad.android.buddy.utils.FilesHolder
import com.likethesalad.android.buddy.utils.TransformInvocationDataExtractorFactory
import net.bytebuddy.build.Plugin
import javax.inject.Inject

@AppScope
class ByteBuddyTransform @Inject constructor(
    private val classFileLocatorMaker: ClassFileLocatorMaker,
    private val pluginFactoriesProvider: PluginFactoriesProvider,
    private val pluginEngineProvider: PluginEngineProvider,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val sourceOriginForMultipleFoldersFactory: SourceOriginForMultipleFoldersFactory,
    private val transformInvocationDataExtractorFactory: TransformInvocationDataExtractorFactory,
    private val androidPluginDataProvider: AndroidPluginDataProvider,
    private val compoundSourceFactory: CompoundSourceFactory
) : Transform() {

    override fun getName(): String = "androidBuddy"

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return setOf(QualifiedContent.DefaultContentType.CLASSES)
    }

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        val variantName = transformInvocation.context.variantName
        val transformInvocationDataExtractor = transformInvocationDataExtractorFactory.create(transformInvocation)
        val scopeClasspath = transformInvocationDataExtractor.getScopeClasspath()
        val outputFolder = transformInvocationDataExtractor.getOutputFolder(scopes)

        pluginEngineProvider.makeEngine(variantName)
            .with(classFileLocatorMaker.make(androidPluginDataProvider.getBootClasspath()))
            .apply(
                getCompoundSource(scopeClasspath),
                byteBuddyClassesInstantiator.makeTargetForFolder(outputFolder),
                pluginFactoriesProvider.getFactories(scopeClasspath)
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
}