package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.utils.PluginClassNamesProvider
import net.bytebuddy.ByteBuddy
import net.bytebuddy.build.Plugin
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginFactoriesProvider @Inject constructor(private val pluginClassNamesProvider: PluginClassNamesProvider) {

    fun getFactories(folders: Set<File>): List<Plugin.Factory> {
        val classLoader = getClassloader(folders)
        return pluginClassNamesProvider.getPluginClassNames().map { nameToFactory(it, classLoader) }
    }

    private fun getClassloader(folders: Set<File>): ClassLoader {
        val urls = mutableListOf<URL>()
        for (file in folders) {
            urls.add(file.toURI().toURL())
        }
        return URLClassLoader(urls.toTypedArray(), ByteBuddy::class.java.classLoader)
    }

    @Suppress("UNCHECKED_CAST")
    private fun nameToFactory(className: String, classLoader: ClassLoader): Plugin.Factory {
        return Plugin.Factory.UsingReflection(
            Class.forName(
                className, false, classLoader
            ) as Class<out Plugin?>
        )
    }
}