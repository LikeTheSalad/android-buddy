package com.likethesalad.android.buddy.modules.transform.utils

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import net.bytebuddy.build.Plugin
import java.io.File
import javax.inject.Inject

@AppScope
class PluginFactoriesProvider
@Inject constructor(
    private val instantiatorWrapper: InstantiatorWrapper,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor,
    private val localPluginsExtractor: LocalPluginsExtractor,
    private val classLoaderCreator: ClassLoaderCreator,
    private val logger: Logger,
    projectLoggerProvider: ProjectLoggerProvider
) {

    private val loggerArgumentResolver by lazy {
        byteBuddyClassesInstantiator.makeFactoryArgumentResolverFor(
            org.slf4j.Logger::class.java,
            projectLoggerProvider.getLogger()
        )
    }

    fun getFactories(
        localDirs: Set<File>,
        librariesJarsProvider: LibrariesJarsProvider,
        parentClassLoader: ClassLoader
    ): List<Plugin.Factory> {
        val pluginNames = mutableSetOf<String>()
        val allLibraries = librariesJarsProvider.getLibrariesJars()
        pluginNames.addAll(getLocalPluginNames(localDirs))

        val libraryPlugins = androidBuddyLibraryPluginsExtractor.extractLibraryPlugins(allLibraries)
        if (libraryPlugins.pluginNames.isNotEmpty()) {
            logger.debug("Dependencies transformations found: {}", libraryPlugins.pluginNames)
            pluginNames.addAll(libraryPlugins.pluginNames)
        }

        val factoriesClassloader = classLoaderCreator.create(allLibraries, parentClassLoader)

        return pluginNames.map { nameToFactory(it, factoriesClassloader) }
    }

    private fun getLocalPluginNames(dirFiles: Set<File>): Set<String> {
        val pluginNames = localPluginsExtractor.getLocalPluginNames(dirFiles)
        logger.debug("Local transformations found: {}", pluginNames)
        return pluginNames
    }

    private fun nameToFactory(className: String, classLoader: ClassLoader): Plugin.Factory {
        return byteBuddyClassesInstantiator.makeFactoryUsingReflection(
            instantiatorWrapper.getClassForName(className, false, classLoader)
        ).with(loggerArgumentResolver)
    }
}