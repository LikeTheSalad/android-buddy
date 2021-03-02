package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.configuration.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.configuration.libraries.LibrariesPolicy
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.modules.libraries.exceptions.AndroidBuddyLibraryNotFoundException
import com.likethesalad.android.buddy.modules.libraries.exceptions.DuplicateByteBuddyPluginException
import com.likethesalad.android.buddy.modules.libraries.exceptions.DuplicateLibraryIdException
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.DummyResourcesFinder
import io.mockk.every
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
    private val defaultLibraryPolicy = LibrariesPolicy.UseAll

    companion object {
        private val androidBuddyLibraryInfo = AndroidBuddyLibraryInfo(
            "some.id",
            "some.group",
            "mylibrary",
            "1.0.2",
            setOf("com.example.mylibrary.HelloWorldPlugin")
        )

        private val androidBuddyLibrary2Info = AndroidBuddyLibraryInfo(
            "some.other.id",
            "some.group",
            "mylibrary2",
            "1.0.2",
            setOf("com.example.mylibrary2.HelloWorldPlugin")
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
        every { pluginConfiguration.getLibrariesPolicy() }.returns(defaultLibraryPolicy)

        androidBuddyLibraryPluginsExtractor = AndroidBuddyLibraryPluginsExtractor(
            instantiatorWrapper,
            pluginConfiguration,
            libraryInfoMapper
        )
    }

    @Test
    fun `Get plugin class names from jar created for android buddy only`() {
        val expectedNames = setOf("com.example.mylibrary.HelloWorldPlugin")
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar())

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(expectedNames)
    }

    @Test
    fun `Get plugin class names from multiple jars created for android buddy only`() {
        val expectedNames = setOf(
            "com.example.mylibrary.HelloWorldPlugin",
            "com.example.mylibrary2.HelloWorldPlugin"
        )
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar(), getAndroidBuddyLibrary2Jar())

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(expectedNames)
    }

    @Test
    fun `Get plugin class names from android buddy libraries allowed by IDs in UseOnly`() {
        val expectedNames = setOf("com.example.mylibrary2.HelloWorldPlugin")
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar(), getAndroidBuddyLibrary2Jar())
        every { pluginConfiguration.getLibrariesPolicy() }.returns(LibrariesPolicy.UseOnly(setOf("some.other.id")))

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(expectedNames)
    }

    @Test
    fun `Fail when using policy UseOnly and any id specified isn't found`() {
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar(), getAndroidBuddyLibrary2Jar())
        every { pluginConfiguration.getLibrariesPolicy() }.returns(LibrariesPolicy.UseOnly(setOf("some.nonexistent.id")))

        try {
            androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)
            fail("It should have failed above")
        } catch (e: AndroidBuddyLibraryNotFoundException) {
            Truth.assertThat(e.idsNotFound).isEqualTo(listOf("some.nonexistent.id"))
        }
    }

    @Test
    fun `Get no plugin class names from any android buddy library when policy is IgnoreAll`() {
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar(), getAndroidBuddyLibrary2Jar())
        every { pluginConfiguration.getLibrariesPolicy() }.returns(LibrariesPolicy.IgnoreAll)

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(emptySet<String>())
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
    fun `Do not fail when finding duplicated plugin names on non selected libraries in UseOnly policy`() {
        // TODO ensure to use only the JAR for the selected library ID later on ByteBuddy
        val expectedNames = setOf("com.example.mylibrary.HelloWorldPlugin")
        val jars = setOf(
            getAndroidBuddyLibraryJar(),
            getNormalLibraryJar(),
            getAndroidBuddyLibraryJarWithDiffIdButSamePluginName()
        )
        every { pluginConfiguration.getLibrariesPolicy() }.returns(LibrariesPolicy.UseOnly(setOf("some.id")))

        val result = androidBuddyLibraryPluginsExtractor.extractPluginNames(jars)

        Truth.assertThat(result).isEqualTo(expectedNames)
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

    @Test
    fun `Fail when finding duplicated library IDs on selected libraries in UseOnly policy`() {
        val jars = setOf(getAndroidBuddyLibraryJar(), getNormalLibraryJar(), getAndroidBuddyLibraryJarWithSameId())
        every { pluginConfiguration.getLibrariesPolicy() }.returns(LibrariesPolicy.UseOnly(setOf("some.id")))

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

    private fun getAndroidBuddyLibrary2Jar(): File {
        return getDummyJarFile("android-buddy-library2-1.0-SNAPSHOT.jar")
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