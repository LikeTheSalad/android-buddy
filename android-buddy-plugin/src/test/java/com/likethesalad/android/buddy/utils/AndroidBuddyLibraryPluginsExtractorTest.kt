package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.configuration.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.modules.libraries.DuplicateByteBuddyPluginException
import com.likethesalad.android.buddy.modules.libraries.DuplicateLibraryIdException
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.DummyResourcesFinder
import io.mockk.impl.annotations.MockK
import org.junit.Assert.fail
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

    companion object {
        private val androidBuddyLibraryInfo = AndroidBuddyLibraryInfo(
            "some.id",
            "some.group",
            "mylibrary",
            "1.0.2",
            setOf("com.example.mylibrary.HelloWorldPlugin")
        )

        private val androidBuddyLibraryDiffIdSharedPluginName = AndroidBuddyLibraryInfo(
            "some.other.id",
            "some.group",
            "mylibrary2",
            "1.0.2",
            setOf("com.example.mylibrary.HelloWorldPlugin")
        )

        private val androidBuddyLibrarySameId = AndroidBuddyLibraryInfo(
            "some.id",
            "some.group",
            "mylibrary2",
            "1.0.2",
            setOf("com.example.mylibrary2.HelloWorldPlugin")
        )
    }

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
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar())

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(expectedNames)
    }

    @Test
    fun `Fail when finding duplicated plugin names across libraries`() {
        val jars = setOf(
            getAndroidBuddyLibraryJar(),
            getNormalLibraryJar(),
            getAndroidBuddyLibraryJarWithDiffIdButSamePluginName()
        )

        try {
            androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)
            fail("It should have failed above")
        } catch (e: DuplicateByteBuddyPluginException) {
            Truth.assertThat(e.pluginNames).isEqualTo(setOf("com.example.mylibrary.HelloWorldPlugin"))
            Truth.assertThat(e.librariesContainingThePlugin).isEqualTo(
                listOf(
                    androidBuddyLibraryInfo,
                    androidBuddyLibraryDiffIdSharedPluginName
                )
            )
        }
    }

    @Test
    fun `Fail when finding duplicated library IDs`() {
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar(), getAndroidBuddyLibraryJarWithSameId())

        try {
            androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)
            fail("Should have failed above")
        } catch (e: DuplicateLibraryIdException) {
            Truth.assertThat(e.id).isEqualTo("some.id")
            Truth.assertThat(e.librariesWithSameId)
                .isEqualTo(listOf(androidBuddyLibraryInfo, androidBuddyLibrarySameId))
        }
    }

    private fun getAndroidBuddyLibraryJar(): File {
        return getDummyJarFile("android-buddy-library-1.0-SNAPSHOT.jar")
    }

    private fun getNormalLibraryJar(): File {
        return getDummyJarFile("normal-java-library-1.0-SNAPSHOT.jar")
    }

    private fun getAndroidBuddyLibraryJarWithSameId(): File {
        return getDummyJarFile("android-buddy-library-same-id-1.0-SNAPSHOT.jar")
    }

    private fun getAndroidBuddyLibraryJarWithDiffIdButSamePluginName(): File {
        return getDummyJarFile("android-buddy-library-duplicated-plugin-1.0-SNAPSHOT.jar")
    }

    private fun getDummyJarFile(name: String): File {
        return dummyResourcesFinder.getResourceFile("classdirs/$name")
    }
}