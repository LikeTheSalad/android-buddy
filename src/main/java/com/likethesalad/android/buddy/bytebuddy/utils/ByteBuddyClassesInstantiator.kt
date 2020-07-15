package com.likethesalad.android.buddy.bytebuddy.utils

import com.likethesalad.android.buddy.di.AppScope
import net.bytebuddy.ClassFileVersion
import net.bytebuddy.build.EntryPoint
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer
import java.io.File
import javax.inject.Inject

@AppScope
class ByteBuddyClassesInstantiator @Inject constructor() {

    fun makeJarClassFileLocator(jarFile: File): ClassFileLocator {
        return ClassFileLocator.ForJarFile.of(jarFile)
    }

    fun makeFolderClassFileLocator(folder: File): ClassFileLocator {
        return ClassFileLocator.ForFolder(folder)
    }

    fun makeCompoundClassFileLocator(locators: List<ClassFileLocator>): ClassFileLocator {
        return ClassFileLocator.Compound(locators)
    }

    fun makeSourceElementForFile(root: File, file: File): Plugin.Engine.Source.Element {
        return Plugin.Engine.Source.Element.ForFile(root, file)
    }

    fun makeFactoryUsingReflection(type: Class<out Plugin>): Plugin.Factory {
        return Plugin.Factory.UsingReflection(type)
    }

    fun makeTargetForFolder(folder: File): Plugin.Engine.Target {
        return Plugin.Engine.Target.ForFolder(folder)
    }

    fun makeDefaultEntryPoint(): EntryPoint.Default {
        return EntryPoint.Default.REBASE
    }

    fun makeClassFileVersionOfJavaVersion(javaVersion: Int): ClassFileVersion {
        return ClassFileVersion.ofJavaVersion(javaVersion)
    }

    fun makeDefaultMethodNameTransformer(): MethodNameTransformer {
        return MethodNameTransformer.Suffixing.withRandomSuffix()
    }

    fun makePluginEngineOf(
        entryPoint: EntryPoint,
        classFileVersion: ClassFileVersion,
        methodNameTransformer: MethodNameTransformer
    ): Plugin.Engine {
        return Plugin.Engine.Default.of(entryPoint, classFileVersion, methodNameTransformer)
    }
}