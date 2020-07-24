package com.likethesalad.android.buddy.modules.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.utils.FilesHolder
import java.io.File

@AutoFactory
class TransformInvocationDataExtractor(
    private val transformInvocation: TransformInvocation
) {

    companion object {
        private const val OUTPUT_DIR_NAME = "androidBuddyTransform"
    }

    fun getOutputFolder(scopes: MutableSet<in QualifiedContent.Scope>): File {
        return transformInvocation.outputProvider.getContentLocation(
            OUTPUT_DIR_NAME,
            setOf(QualifiedContent.DefaultContentType.CLASSES),
            scopes,
            Format.DIRECTORY
        )
    }

    fun getScopeClasspath(): FilesHolder {
        val dirFiles = mutableSetOf<File>()
        val jarFiles = mutableSetOf<File>()
        val allFiles = mutableSetOf<File>()

        transformInvocation.inputs.forEach {
            it.directoryInputs.forEach { dir ->
                val dirFile = dir.file
                dirFiles.add(dirFile)
                allFiles.add(dirFile)
            }
            it.jarInputs.forEach { jar ->
                val jarFile = jar.file
                jarFiles.add(jarFile)
                allFiles.add(jarFile)
            }
        }

        return FilesHolder(dirFiles, jarFiles, allFiles)
    }
}