package com.likethesalad.android.buddy.utils

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.utils.Constants
import com.likethesalad.android.common.utils.InstantiatorWrapper
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import java.io.File
import java.io.InputStream
import java.util.Properties
import javax.inject.Inject

@AppScope
class AndroidBuddyLibraryPluginsExtractor
@Inject constructor(private val instantiatorWrapper: InstantiatorWrapper) {

    fun extractPluginNames(jarFiles: Set<File>): Set<String> {
        val scanResult = getClassGraphScan(jarFiles)
        val names = mutableSetOf<String>()

        scanResult.getResourcesWithLeafName(Constants.PLUGINS_PROPERTIES_FILE_NAME)
            .forEachInputStreamIgnoringIOException { _, inputStream ->
                names.addAll(getPluginNamesFromPropertiesFile(inputStream))
            }

        return names
    }

    private fun getClassGraphScan(jarFiles: Set<File>): ScanResult {
        return instantiatorWrapper.getClassGraph()
            .overrideClassLoaders(jarFilesToClassLoader(jarFiles))
            .acceptPaths(Constants.LIBRARY_PROPERTIES_DIR)
            .scan()
    }

    private fun jarFilesToClassLoader(jarFiles: Set<File>): ClassLoader {
        return instantiatorWrapper.getUrlClassLoader(
            jarFiles.map { it.toURI().toURL() }.toTypedArray(),
            ClassGraph::class.java.classLoader
        )
    }

    private fun getPluginNamesFromPropertiesFile(inputStream: InputStream): Set<String> {
        val properties = Properties()
        properties.load(inputStream)
        val classNames = properties.getProperty(Constants.PLUGINS_PROPERTIES_CLASSES_KEY, "")

        return classNames.split(",").toSet()
    }
}