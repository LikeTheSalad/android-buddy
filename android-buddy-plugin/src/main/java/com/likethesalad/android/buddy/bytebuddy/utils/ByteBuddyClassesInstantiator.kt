package com.likethesalad.android.buddy.bytebuddy.utils

import com.likethesalad.android.buddy.di.AppScope
import net.bytebuddy.ClassFileVersion
import net.bytebuddy.build.EntryPoint
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer
import java.io.File
import java.util.jar.JarFile
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

    fun makeFactoryUsingReflection(type: Class<out Plugin>): Plugin.Factory.UsingReflection {
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

    fun makeJarFileSourceOrigin(file: File): Plugin.Engine.Source.Origin.ForJarFile {
        return Plugin.Engine.Source.Origin.ForJarFile(JarFile(file))
    }

    fun <T> makeFactoryArgumentResolverFor(type: Class<T>, value: T)
            : Plugin.Factory.UsingReflection.ArgumentResolver {
        return Plugin.Factory.UsingReflection.ArgumentResolver.ForType.of(type, value)
    }

    fun makePluginEngineOf(
        entryPoint: EntryPoint,
        classFileVersion: ClassFileVersion,
        methodNameTransformer: MethodNameTransformer
    ): Plugin.Engine {
        return Plugin.Engine.Default.of(entryPoint, classFileVersion, methodNameTransformer)
    }

    fun makeByteArrayClassLoader(classes: Map<String, ByteArray>): ByteArrayClassLoader {
        return ByteArrayClassLoader(javaClass.classLoader, classes)
    }
}