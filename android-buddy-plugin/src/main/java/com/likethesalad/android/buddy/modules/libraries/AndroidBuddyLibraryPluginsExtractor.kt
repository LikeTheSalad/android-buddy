package com.likethesalad.android.buddy.modules.libraries

import com.likethesalad.android.buddy.configuration.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.configuration.libraries.LibrariesPolicy
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.libraries.exceptions.AndroidBuddyLibraryNotFoundException
import com.likethesalad.android.buddy.modules.libraries.exceptions.DuplicateByteBuddyPluginException
import com.likethesalad.android.buddy.modules.libraries.exceptions.DuplicateLibraryIdException
import com.likethesalad.android.buddy.modules.libraries.models.LibraryPluginsExtracted
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

    fun extractPluginNames(jarFiles: Set<File>): LibraryPluginsExtracted {
        val policy = pluginConfiguration.getLibrariesPolicy()

        if (policy == LibrariesPolicy.IgnoreAll) {
            return LibraryPluginsExtracted.EMPTY
        }

        val scanResult = getClassGraphScan(jarFiles)
        val librariesFound = mutableListOf<AndroidBuddyLibraryInfo>()

        scanResult.getResourcesMatchingPattern(RESOURCE_DEFINITION_PATTERN)
            .forEachInputStreamIgnoringIOException { resource, inputStream ->
                librariesFound.add(getLibraryInfoFromPropertiesFile(extractNameFromPath(resource.path), inputStream))
            }

        val libraries = when (policy) {
            LibrariesPolicy.UseAll -> librariesFound
            is LibrariesPolicy.UseOnly -> getLibrariesByIds(librariesFound, policy.libraryIds)
            else -> throw IllegalArgumentException()
        }

        validateNoDuplicateLibraryIds(libraries)
        validateNoSharedPluginNamesAcrossLibraries(libraries)

        val pluginNames = libraries.map { it.pluginNames }.flatten().toSet()
        return LibraryPluginsExtracted(pluginNames, emptyList(), emptyList())
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

    private fun getLibrariesByIds(
        allLibraries: List<AndroidBuddyLibraryInfo>,
        requestedIds: Set<String>
    ): List<AndroidBuddyLibraryInfo> {
        val librariesFound = allLibraries.filter { it.id in requestedIds }
        val librariesFoundIds = librariesFound.map { it.id }
        val notFoundIds = requestedIds.filter { it !in librariesFoundIds }

        if (notFoundIds.isNotEmpty()) {
            throw AndroidBuddyLibraryNotFoundException(notFoundIds)
        }

        return librariesFound
    }

    private fun validateNoDuplicateLibraryIds(libraries: List<AndroidBuddyLibraryInfo>) {
        val librariesById = libraries.groupBy { it.id }
        librariesById.forEach { (libraryId, infoList) ->
            if (infoList.size > 1) {
                throw DuplicateLibraryIdException(libraryId, infoList)
            }
        }
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
                    throw DuplicateByteBuddyPluginException(commonPluginNames, listOf(info, otherInfo))
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