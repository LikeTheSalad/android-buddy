package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesMaker
import net.bytebuddy.dynamic.ClassFileLocator
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassFileLocatorMaker @Inject constructor(private val byteBuddyClassesMaker: ByteBuddyClassesMaker) {

    fun make(filesAndDirs: Set<File>): ClassFileLocator {
        val classFileLocators: MutableList<ClassFileLocator> = mutableListOf()

        for (artifact in filesAndDirs) {
            classFileLocators.add(
                if (artifact.isFile) {
                    byteBuddyClassesMaker.makeJarClassFileLocator(artifact)
                } else {
                    byteBuddyClassesMaker.makeFolderClassFileLocator(artifact)
                }
            )
        }

        return byteBuddyClassesMaker.makeCompoundClassFileLocator(classFileLocators)
    }
}