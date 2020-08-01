package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.utils.Logger
import net.bytebuddy.build.Plugin
import javax.inject.Inject

@AppScope
class PluginEngineProvider
@Inject constructor(
    private val instantiator: ByteBuddyClassesInstantiator,
    private val transformationLogger: TransformationLogger,
    private val logger: Logger
) {

    fun makeEngine(javaTargetCompatibilityVersion: Int): Plugin.Engine {
        val entryPoint = instantiator.makeDefaultEntryPoint()
        val classFileVersion = instantiator
            .makeClassFileVersionOfJavaVersion(javaTargetCompatibilityVersion)
        val methodNameTransformer = instantiator.makeDefaultMethodNameTransformer()

        return instantiator.makePluginEngineOf(entryPoint, classFileVersion, methodNameTransformer)
            .withParallelTransformation(getThreadCount())
            .with(transformationLogger)
    }

    private fun getThreadCount(): Int {
        val count = getJvmAvailableProcessors() + 1
        logger.debug("Using {} threads for transformation", count)
        return count
    }

    internal fun getJvmAvailableProcessors(): Int {
        return Runtime.getRuntime().availableProcessors()
    }
}