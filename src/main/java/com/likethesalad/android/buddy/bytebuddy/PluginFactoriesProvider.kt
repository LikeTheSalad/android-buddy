package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import com.likethesalad.android.buddy.utils.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.buddy.utils.FilesHolder
import com.likethesalad.android.common.utils.InstantiatorWrapper
import net.bytebuddy.ByteBuddy
import net.bytebuddy.build.Plugin
import java.io.File
import javax.inject.Inject

@AppScope
class PluginFactoriesProvider
@Inject constructor(
    private val pluginClassNamesProvider: PluginClassNamesProvider,
    private val classLoaderCreator: ClassLoaderCreator,
    private val instantiatorWrapper: InstantiatorWrapper,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor
) {

    fun getFactories(filesHolder: FilesHolder): List<Plugin.Factory> {
        val pluginNames = mutableSetOf<String>()
        pluginNames.addAll(getLocalPluginNames())
        pluginNames.addAll(getLibraryPluginNames(filesHolder.jarFiles))

        val classLoader = classLoaderCreator.create(filesHolder.allFiles, ByteBuddy::class.java.classLoader)
        return pluginNames.map { nameToFactory(it, classLoader) }
    }

    private fun getLocalPluginNames(): Set<String> {
        return pluginClassNamesProvider.getPluginClassNames()
    }

    private fun getLibraryPluginNames(jarFiles: Set<File>): Set<String> {
        return androidBuddyLibraryPluginsExtractor.extractPluginNames(jarFiles)
    }

    private fun nameToFactory(className: String, classLoader: ClassLoader): Plugin.Factory {
        return byteBuddyClassesInstantiator.makeFactoryUsingReflection(
            instantiatorWrapper.getClassForName(className, false, classLoader)
        )
    }
}