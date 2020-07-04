package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesMaker
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.buddy.utils.PluginClassNamesProvider
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
    private val byteBuddyClassesMaker: ByteBuddyClassesMaker
) {

    fun getFactories(folders: Set<File>): List<Plugin.Factory> {
        val classLoader = classLoaderCreator.create(folders, ByteBuddy::class.java.classLoader)
        return pluginClassNamesProvider.getPluginClassNames().map { nameToFactory(it, classLoader) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun nameToFactory(className: String, classLoader: ClassLoader): Plugin.Factory {
        return byteBuddyClassesMaker.makeFactoryUsingReflection(
            Class.forName(
                className, false, classLoader
            ) as Class<out Plugin>
        )
    }
}