package com.likethesalad.android.buddy.modules.libraries

import com.likethesalad.android.buddy.configuration.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.Constants
import com.likethesalad.android.common.utils.Constants.PLUGINS_METADATA_CLASS_NAME
import com.likethesalad.android.common.utils.InstantiatorWrapper
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@AppScope
class AndroidBuddyLibraryPluginsExtractor
@Inject constructor(
    private val instantiatorWrapper: InstantiatorWrapper,
    private val byteArrayClassLoaderUtil: ByteArrayClassLoaderUtil,
    private val pluginConfiguration: AndroidBuddyPluginConfiguration
) {

    fun extractPluginNames(jarFiles: Set<File>): Set<String> {
        val scanResult = getClassGraphScan(jarFiles)
        val names = mutableSetOf<String>()

        scanResult.getResourcesWithLeafName(Constants.PLUGINS_METADATA_FILE_EXT)
            .forEachInputStreamIgnoringIOException { _, inputStream ->
                names.addAll(getPluginNamesFromPropertiesFile(inputStream))
            }

        return names
    }

    private fun getClassGraphScan(jarFiles: Set<File>): ScanResult {
        return instantiatorWrapper.getClassGraph()
            .overrideClassLoaders(jarFilesToClassLoader(jarFiles))
            .acceptPaths(Constants.LIBRARY_METADATA_DIR)
            .scan()
    }

    private fun jarFilesToClassLoader(jarFiles: Set<File>): ClassLoader {
        return instantiatorWrapper.getUrlClassLoader(
            jarFiles.map { it.toURI().toURL() }.toTypedArray(),
            ClassGraph::class.java.classLoader
        )
    }

    private fun getPluginNamesFromPropertiesFile(stream: InputStream): Set<String> {
        val classLoaded = byteArrayClassLoaderUtil.loadClass(PLUGINS_METADATA_CLASS_NAME, stream.readBytes())
        val classNames = classLoaded.newInstance().toString()

        return classNames.split(",").toSet()
    }
}