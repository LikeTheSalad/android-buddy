package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.AndroidPluginDataProvider
import net.bytebuddy.build.Plugin
import javax.inject.Inject

@AppScope
class PluginEngineProvider
@Inject constructor(
    private val instantiator: ByteBuddyClassesInstantiator,
    private val androidPluginDataProvider: AndroidPluginDataProvider,
    private val transformationLogger: TransformationLogger
) {

    fun makeEngine(variantName: String): Plugin.Engine {
        val entryPoint = instantiator.makeDefaultEntryPoint()
        val classFileVersion = instantiator
            .makeClassFileVersionOfJavaVersion(androidPluginDataProvider.getJavaTargetCompatibilityVersion(variantName))
        val methodNameTransformer = instantiator.makeDefaultMethodNameTransformer()

        return instantiator.makePluginEngineOf(entryPoint, classFileVersion, methodNameTransformer)
            .with(transformationLogger)
    }
}