package com.likethesalad.android.buddy.utils

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.providers.AndroidPluginDataProvider
import java.io.File

@AutoFactory
class TransformInvocationDataExtractor(
    @Provided private val androidPluginDataProvider: AndroidPluginDataProvider,
    private val transformInvocation: TransformInvocation
) {

    companion object {
        private const val OUTPUT_DIR_NAME = "androidBuddyTransform"
    }

    fun getOutputFolder(): File {
        return transformInvocation.outputProvider.getContentLocation(
            OUTPUT_DIR_NAME,
            setOf(QualifiedContent.DefaultContentType.CLASSES),
            mutableSetOf(QualifiedContent.Scope.PROJECT),
            Format.DIRECTORY
        )
    }

    fun getClasspath(): Set<File> {
        val classpath = mutableSetOf<File>()

        transformInvocation.inputs.forEach {
            it.directoryInputs.forEach { dir ->
                classpath.add(dir.file)
            }
            it.jarInputs.forEach { jar ->
                classpath.add(jar.file)
            }
        }

        classpath.addAll(androidPluginDataProvider.getBootClasspath())

        return classpath
    }
}