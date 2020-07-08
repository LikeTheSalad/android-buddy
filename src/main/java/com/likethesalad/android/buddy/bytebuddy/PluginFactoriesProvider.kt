package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.common.utils.InstantiatorWrapper
import net.bytebuddy.ByteBuddy
import net.bytebuddy.build.Plugin
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginFactoriesProvider
@Inject constructor(
    private val pluginClassNamesProvider: PluginClassNamesProvider,
    private val classLoaderCreator: ClassLoaderCreator,
    private val instantiatorWrapper: InstantiatorWrapper,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator
) {

    fun getFactories(folders: Set<File>): List<Plugin.Factory> {
        val classLoader = classLoaderCreator.create(folders, ByteBuddy::class.java.classLoader)
        return pluginClassNamesProvider.getPluginClassNames().map { nameToFactory(it, classLoader) }
    }

    private fun nameToFactory(className: String, classLoader: ClassLoader): Plugin.Factory {
        return byteBuddyClassesInstantiator.makeFactoryUsingReflection(
            instantiatorWrapper.getClassForName(className, false, classLoader)
        )
    }
}