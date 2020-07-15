package com.likethesalad.android.buddy.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.likethesalad.android.buddy.bytebuddy.ClassFileLocatorMaker
import com.likethesalad.android.buddy.bytebuddy.PluginEngineProvider
import com.likethesalad.android.buddy.bytebuddy.PluginFactoriesProvider
import com.likethesalad.android.buddy.bytebuddy.SourceForMultipleFoldersFactory
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.utils.FilesHolderFactory
import com.likethesalad.android.buddy.utils.TransformInvocationDataExtractorFactory
import javax.inject.Inject

@AppScope
class ByteBuddyTransform @Inject constructor(
    private val classFileLocatorMaker: ClassFileLocatorMaker,
    private val pluginFactoriesProvider: PluginFactoriesProvider,
    private val pluginEngineProvider: PluginEngineProvider,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val sourceForMultipleFoldersFactory: SourceForMultipleFoldersFactory,
    private val transformInvocationDataExtractorFactory: TransformInvocationDataExtractorFactory,
    private val filesHolderFactory: FilesHolderFactory
) : Transform() {

    override fun getName(): String = "Android ByteBuddy Transform"

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

        val transformInvocationDataExtractor = transformInvocationDataExtractorFactory.create(transformInvocation)
        val classpath = filesHolderFactory.create(transformInvocationDataExtractor.getClasspath())
        val outputFolder = transformInvocationDataExtractor.getOutputFolder()

        pluginEngineProvider.makeEngine(transformInvocation.context.variantName)
            .with(classFileLocatorMaker.make(classpath.allFiles))
            .apply(
                sourceForMultipleFoldersFactory.create(classpath.dirs),
                byteBuddyClassesInstantiator.makeTargetForFolder(outputFolder),
                pluginFactoriesProvider.getFactories(classpath)
            )
    }
}