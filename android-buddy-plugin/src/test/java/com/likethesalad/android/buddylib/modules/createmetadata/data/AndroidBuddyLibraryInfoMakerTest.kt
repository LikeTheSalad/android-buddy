package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.google.common.truth.Truth
import com.likethesalad.android.buddylib.providers.ProjectInfoProvider
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class AndroidBuddyLibraryInfoMakerTest : BaseMockable() {

    @MockK
    lateinit var projectInfoProvider: ProjectInfoProvider

    private lateinit var androidBuddyLibraryInfoMaker: AndroidBuddyLibraryInfoMaker

    @Before
    fun setUp() {
        androidBuddyLibraryInfoMaker = AndroidBuddyLibraryInfoMaker(projectInfoProvider)
    }

    @Test
    fun `Create android buddy library info`() {
        val id = "some-id"
        val group = "some.group"
        val name = "some.name"
        val version = "1.0.0"
        val pluginNames = setOf("one.Plugin")

        val info = callMake(id, group, name, version, pluginNames)

        Truth.assertThat(info).isEqualTo(AndroidBuddyLibraryInfo(id, group, name, version, pluginNames))
    }

    @Test
    fun `Validate ID format when creating info`() {
        checkIdIsValid("one.id", true)
        checkIdIsValid("one-id", true)
        checkIdIsValid("one-id-something", true)
        checkIdIsValid("one-id.something", true)
        checkIdIsValid("one.id-something", true)
        checkIdIsValid("One.id-something", false)
        checkIdIsValid("1one.id-something", false)
        checkIdIsValid("one_id-something", false)
        checkIdIsValid("one--id-something", false)
        checkIdIsValid("one..id-something", false)
        checkIdIsValid("one.-id-something", false)
        checkIdIsValid("one-.id-something", false)
        checkIdIsValid("one id", false)
        checkIdIsValid("one   id", false)
        checkIdIsValid("one@id", false)
        checkIdIsValid("one.id.", false)
        checkIdIsValid("one.id-", false)
        checkIdIsValid("  one.id", false)
        checkIdIsValid("", false)
    }

    @Test
    fun `Check if group is valid`() {
        checkGroupIsValid("some.group", true)
        checkGroupIsValid("some group", false)
        checkGroupIsValid("", false)
    }

    @Test
    fun `Check if name is valid`() {
        checkNameIsValid("some.name", true)
        checkNameIsValid("some name", true)
        checkNameIsValid("Some name", true)
        checkNameIsValid("    ", false)
        checkNameIsValid("", false)
    }

    @Test
    fun `Format name if needed`() {
        checkNameFormatted("some.name", "some.name")
        checkNameFormatted("some-name", "some-name")
        checkNameFormatted("some name", "some_name")
        checkNameFormatted("somename", "somename")
        checkNameFormatted("Some name", "some_name")
        checkNameFormatted("Some name  ", "some_name")
        checkNameFormatted("Some  name  ", "some_name")
        checkNameFormatted(" Some  name  ", "some_name")
        checkNameFormatted(" Some  name", "some_name")
        checkNameFormatted(" Some name  ", "some_name")
        checkNameFormatted("-some name", "some_name")
        checkNameFormatted("-.somename", "somename")
        checkNameFormatted("somename--", "somename")
        checkNameFormatted("some name-", "some_name")
        checkNameFormatted("some name.", "some_name")
        checkNameFormatted("some name-", "some_name")
    }

    private fun checkIdIsValid(id: String, shouldBeValid: Boolean) {
        val group = "some.group"
        val name = "some.name"
        val version = "1.0.1"
        val pluginNames = setOf("some.Plugin")

        try {
            callMake(id, group, name, version, pluginNames)
            if (!shouldBeValid) {
                fail("Id '$id' should not be valid")
            }
        } catch (e: IllegalArgumentException) {
            if (shouldBeValid) {
                throw e
            }
            Truth.assertThat(e.message).isEqualTo(
                "Id provided '$id' has not a valid format:" +
                        "\n -Only lowercase letters, numbers, '.' and '-' are allowed." +
                        "\n -It must start with a letter." +
                        "\n -It cannot end neither with '-' nor with '.'." +
                        "\n -There cannot be a consecutive '-' or '.' after a '-' and/or '.'."
            )
        }
    }

    private fun checkGroupIsValid(group: String, shouldBeValid: Boolean) {
        val id = "some.id"
        val name = "some.name"
        val version = "1.0.0"
        val pluginNames = setOf("some.Plugin")

        try {
            callMake(id, group, name, version, pluginNames)
            if (!shouldBeValid) {
                fail("The group '$group' should not be valid")
            }
        } catch (e: IllegalArgumentException) {
            if (shouldBeValid) {
                throw e
            }
            Truth.assertThat(e.message).isEqualTo("The group provided '$group' has not a valid format")
        }
    }

    private fun checkNameIsValid(name: String, shouldBeValid: Boolean) {
        val id = "some.id"
        val group = "some.group"
        val version = "1.0.0"
        val pluginNames = setOf("some.Plugin")

        try {
            callMake(id, group, name, version, pluginNames)
            if (!shouldBeValid) {
                fail("The name '$name' should not be valid")
            }
        } catch (e: IllegalArgumentException) {
            if (shouldBeValid) {
                throw e
            }
            Truth.assertThat(e.message).isEqualTo("The name provided '$name' is not valid, it cannot be empty")
        }
    }

    private fun checkNameFormatted(originalName: String, expectedName: String) {
        val id = "some.id"
        val group = "some.group"
        val version = "1.0.0"
        val pluginNames = setOf("some.Plugin")

        val info = callMake(id, group, originalName, version, pluginNames)

        Truth.assertThat(info.name).isEqualTo(expectedName)
    }

    private fun callMake(
        id: String,
        group: String,
        name: String,
        version: String,
        pluginNames: Set<String>
    ): AndroidBuddyLibraryInfo {
        every { projectInfoProvider.getGroup() }.returns(group)
        every { projectInfoProvider.getName() }.returns(name)
        every { projectInfoProvider.getVersion() }.returns(version)
        return androidBuddyLibraryInfoMaker.make(id, pluginNames)
    }
}