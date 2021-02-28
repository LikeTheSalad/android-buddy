package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.configuration.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.DummyResourcesFinder
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidBuddyLibraryPluginsExtractorTest : BaseMockable() {

    private val instantiatorWrapper = InstantiatorWrapper()
    private val dummyResourcesFinder = DummyResourcesFinder()
    private val byteArrayClassLoaderUtil = ByteArrayClassLoaderUtil(ByteBuddyClassesInstantiator())
    private val libraryInfoMapper = LibraryInfoMapper(AndroidBuddyLibraryInfoFqnBuilder(), byteArrayClassLoaderUtil)

    @MockK
    private lateinit var pluginConfiguration: AndroidBuddyPluginConfiguration

    private lateinit var androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor

    @Before
    fun setUp() {
        androidBuddyLibraryPluginsExtractor = AndroidBuddyLibraryPluginsExtractor(
            instantiatorWrapper,
            pluginConfiguration,
            libraryInfoMapper
        )
    }

    @Test
    fun `Get plugin class names from jars created for android buddy only`() {
        val expectedNames = setOf("com.example.mylibrary.HelloWorldPlugin")
        val jars = getDummyJarFiles()

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(expectedNames)
    }

    @Test
    fun `Fail when finding duplicated plugin names across libraries`() {

    }

    private fun getDummyJarFiles(): Set<File> {
        return setOf(
            getDummyJarFile("android-buddy-library-1.0-SNAPSHOT.jar"),
            getDummyJarFile("normal-java-library-1.0-SNAPSHOT.jar")
        )
    }

    private fun getDummyJarFile(name: String): File {
        return dummyResourcesFinder.getResourceFile("classdirs/$name")
    }
}