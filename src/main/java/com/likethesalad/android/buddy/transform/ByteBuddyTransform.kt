package com.likethesalad.android.buddy.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.likethesalad.android.buddy.bytebuddy.ClassFileLocatorMaker
import com.likethesalad.android.buddy.bytebuddy.PluginEngineProvider
import com.likethesalad.android.buddy.bytebuddy.PluginFactoriesProvider
import com.likethesalad.android.buddy.bytebuddy.SourceAndTargetProvider
import com.likethesalad.android.buddy.utils.FilesHolderFactory
import com.likethesalad.android.buddy.utils.TransformInvocationDataExtractorFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ByteBuddyTransform @Inject constructor(
    private val classFileLocatorMaker: ClassFileLocatorMaker,
    private val pluginFactoriesProvider: PluginFactoriesProvider,
    private val pluginEngineProvider: PluginEngineProvider,
    private val sourceAndTargetProvider: SourceAndTargetProvider,
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
        val folders = classpath.dirs

        pluginEngineProvider.makeEngine()
            .with(classFileLocatorMaker.make(classpath.allFiles))
            .apply(
                sourceAndTargetProvider.getSource(folders),
                sourceAndTargetProvider.getTarget(transformInvocationDataExtractor.getOutputFolder()),
                pluginFactoriesProvider.getFactories(folders)
            )
    }
}