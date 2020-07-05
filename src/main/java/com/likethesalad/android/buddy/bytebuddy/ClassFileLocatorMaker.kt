package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import net.bytebuddy.dynamic.ClassFileLocator
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassFileLocatorMaker
@Inject constructor(private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator) {

    fun make(filesAndDirs: Set<File>): ClassFileLocator {
        val classFileLocators: MutableList<ClassFileLocator> = mutableListOf()

        for (artifact in filesAndDirs) {
            classFileLocators.add(
                if (artifact.isFile) {
                    byteBuddyClassesInstantiator.makeJarClassFileLocator(artifact)
                } else {
                    byteBuddyClassesInstantiator.makeFolderClassFileLocator(artifact)
                }
            )
        }

        return byteBuddyClassesInstantiator.makeCompoundClassFileLocator(classFileLocators)
    }
}