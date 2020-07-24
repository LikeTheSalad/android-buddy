package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import net.bytebuddy.build.Plugin
import javax.inject.Inject

@AppScope
class PluginEngineProvider
@Inject constructor(
    private val instantiator: ByteBuddyClassesInstantiator,
    private val transformationLogger: TransformationLogger
) {

    fun makeEngine(javaTargetCompatibilityVersion: Int): Plugin.Engine {
        val entryPoint = instantiator.makeDefaultEntryPoint()
        val classFileVersion = instantiator
            .makeClassFileVersionOfJavaVersion(javaTargetCompatibilityVersion)
        val methodNameTransformer = instantiator.makeDefaultMethodNameTransformer()

        return instantiator.makePluginEngineOf(entryPoint, classFileVersion, methodNameTransformer)
            .with(transformationLogger)
    }
}