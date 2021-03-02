package com.likethesalad.android.buddy.modules.libraries

import com.likethesalad.android.buddy.configuration.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.models.libinfo.NamedClassInfo
import com.likethesalad.android.common.utils.Constants
import com.likethesalad.android.common.utils.Constants.PLUGINS_METADATA_FILE_EXT
import com.likethesalad.android.common.utils.Constants.PLUGINS_METADATA_FILE_NAME
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
    private val pluginConfiguration: AndroidBuddyPluginConfiguration,
    private val libraryInfoMapper: LibraryInfoMapper
) {

    companion object {
        private val RESOURCE_DEFINITION_PATTERN =
            Regex(".+$PLUGINS_METADATA_FILE_NAME\\.$PLUGINS_METADATA_FILE_EXT").toPattern()
    }

    fun extractPluginNames(jarFiles: Set<File>): Set<String> {
        val scanResult = getClassGraphScan(jarFiles)
        val libraries = mutableListOf<AndroidBuddyLibraryInfo>()

        scanResult.getResourcesMatchingPattern(RESOURCE_DEFINITION_PATTERN)
            .forEachInputStreamIgnoringIOException { resource, inputStream ->
                libraries.add(getLibraryInfoFromPropertiesFile(extractNameFromPath(resource.path), inputStream))
            }

        validateNoSharedPluginNamesAcrossLibraries(libraries)
        return libraries.map { it.pluginNames }.flatten().toSet()
    }

    private fun extractNameFromPath(path: String): String {
        return File(path).nameWithoutExtension
    }

    private fun getClassGraphScan(jarFiles: Set<File>): ScanResult {
        return instantiatorWrapper.getClassGraph()
            .overrideClassLoaders(jarFilesToClassLoader(jarFiles))
            .acceptPaths(Constants.LIBRARY_METADATA_DIR)
            .scan()
    }

    private fun validateNoSharedPluginNamesAcrossLibraries(libraries: List<AndroidBuddyLibraryInfo>) {
        val size = libraries.size
        val lastIndex = size - 1
        libraries.forEachIndexed { index, info ->
            if (index == lastIndex) {
                return
            }
            for (otherIndex in index + 1 until size) {
                val otherInfo = libraries[otherIndex]
                val commonPluginNames = info.pluginNames.intersect(otherInfo.pluginNames)
                if (commonPluginNames.isNotEmpty()) {
                    throw DuplicatedByteBuddyPluginException(commonPluginNames, listOf(info, otherInfo))
                }
            }
        }
    }

    private fun jarFilesToClassLoader(jarFiles: Set<File>): ClassLoader {
        return instantiatorWrapper.getUrlClassLoader(
            jarFiles.map { it.toURI().toURL() }.toTypedArray(),
            ClassGraph::class.java.classLoader
        )
    }

    private fun getLibraryInfoFromPropertiesFile(name: String, stream: InputStream): AndroidBuddyLibraryInfo {
        return libraryInfoMapper.convertToAndroidBuddyLibraryInfo(NamedClassInfo(name, stream.readBytes()))
    }
}