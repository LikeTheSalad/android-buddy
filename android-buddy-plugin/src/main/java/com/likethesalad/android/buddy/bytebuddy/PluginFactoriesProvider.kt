package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
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
    private val pluginClassNamesProvider: PluginClassNamesProvider,
    private val instantiatorWrapper: InstantiatorWrapper,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
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

    fun getFactories(jarLibraries: Set<File>, classLoader: ClassLoader): List<Plugin.Factory> {
        val pluginNames = mutableSetOf<String>()
        pluginNames.addAll(getLocalPluginNames())
        pluginNames.addAll(getLibraryPluginNames(jarLibraries))

        return pluginNames.map { nameToFactory(it, classLoader) }
    }

    private fun getLocalPluginNames(): Set<String> {
        val pluginNames = pluginClassNamesProvider.getPluginClassNames()
        logger.d("Local plugins found: {}", pluginNames)
        return pluginNames
    }

    private fun getLibraryPluginNames(jarFiles: Set<File>): Set<String> {
        val classGraphProvider = classGraphProviderFactory.create(DefaultClassGraphFilesProvider(jarFiles))
        val pluginsFinder = pluginsFinderFactory.create(classGraphProvider)
        val pluginNames = pluginsFinder.findBuiltPluginClassNames()
        logger.d("Dependencies plugins found: {}", pluginNames)
        return pluginNames
    }

    private fun nameToFactory(className: String, classLoader: ClassLoader): Plugin.Factory {
        return byteBuddyClassesInstantiator.makeFactoryUsingReflection(
            instantiatorWrapper.getClassForName(className, false, classLoader)
        ).with(loggerArgumentResolver)
    }
}