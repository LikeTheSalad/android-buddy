package com.likethesalad.android.buddy.bytebuddy

import net.bytebuddy.dynamic.ClassFileLocator
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassFileLocatorMaker @Inject constructor() {

    fun make(filesAndDirs: Set<File>): ClassFileLocator {
        val classFileLocators: MutableList<ClassFileLocator> = mutableListOf()

        for (artifact in filesAndDirs) {
            classFileLocators.add(
                if (artifact.isFile) {
                    ClassFileLocator.ForJarFile.of(artifact)
                } else {
                    ClassFileLocator.ForFolder(artifact)
                }
            )
        }

        return ClassFileLocator.Compound(classFileLocators)
    }
}