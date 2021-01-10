package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.testutils.DummyResourcesFinder
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File

class AndroidBuddyLibraryPluginsExtractorTest {

    private val instantiatorWrapper = InstantiatorWrapper()
    private val dummyResourcesFinder = DummyResourcesFinder()
    private val byteArrayClassLoaderUtil = ByteArrayClassLoaderUtil(ByteBuddyClassesInstantiator())

    private lateinit var androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor

    @Before
    fun setUp() {
        androidBuddyLibraryPluginsExtractor = AndroidBuddyLibraryPluginsExtractor(
            instantiatorWrapper,
            byteArrayClassLoaderUtil
        )
    }

    @Ignore
    @Test
    fun `Get plugin class names from jars created for android buddy only`() {
        val expectedNames = setOf("com.my.test.lib.SomeExamplePlugin")
        val jars = getDummyJarFiles()

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(expectedNames)
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