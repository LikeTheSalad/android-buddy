package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.common.providers.impl.DefaultClassGraphFilesProvider
import com.likethesalad.android.common.utils.ClassGraphProviderFactory
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.common.utils.PluginsFinderFactory
import net.bytebuddy.build.Plugin
import java.io.File
import javax.inject.Inject

@AppScope
class PluginFactoriesProvider
@Inject constructor(
    private val instantiatorWrapper: InstantiatorWrapper,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor,
    private val pluginsFinderFactory: PluginsFinderFactory,
    private val classGraphProviderFactory: ClassGraphProviderFactory,
    private val logger: Logger,
    projectLoggerProvider: ProjectLoggerProvider
) {

    private val loggerArgumentResolver by lazy {
        byteBuddyClassesInstantiator.makeFactoryArgumentResolverFor(
            org.gradle.api.logging.Logger::class.java,
            projectLoggerProvider.getLogger()
        )
    }

    fun getFactories(
        localDirs: Set<File>,
        librariesJarsProvider: LibrariesJarsProvider,
        classLoader: ClassLoader
    ): List<Plugin.Factory> {
        val pluginNames = mutableSetOf<String>()

        pluginNames.addAll(getLocalPluginNames(localDirs))
        pluginNames.addAll(getLibraryPluginNames(librariesJarsProvider.getLibrariesJars()))

        return pluginNames.map { nameToFactory(it, classLoader) }
    }

    private fun getLocalPluginNames(dirFiles: Set<File>): Set<String> {
        val pluginNames = getPluginNamesFrom(dirFiles)
        logger.debug("Local transformations found: {}", pluginNames)
        return pluginNames
    }

    private fun getLibraryPluginNames(jarFiles: Set<File>): Set<String> {
        val pluginNames = androidBuddyLibraryPluginsExtractor.extractPluginNames(jarFiles).pluginNames
        if (pluginNames.isNotEmpty()) {
            val text = "Dependencies transformations found: {}"
            logger.debug(text, pluginNames)
        }
        return pluginNames
    }

    private fun nameToFactory(className: String, classLoader: ClassLoader): Plugin.Factory {
        return byteBuddyClassesInstantiator.makeFactoryUsingReflection(
            instantiatorWrapper.getClassForName(className, false, classLoader)
        ).with(loggerArgumentResolver)
    }

    private fun getPluginNamesFrom(files: Set<File>): Set<String> {
        if (files.isEmpty()) {
            return emptySet()
        }
        val classGraphProvider = classGraphProviderFactory.create(DefaultClassGraphFilesProvider(files))
        val pluginsFinder = pluginsFinderFactory.create(classGraphProvider)
        return pluginsFinder.findBuiltPluginClassNames()
    }
}