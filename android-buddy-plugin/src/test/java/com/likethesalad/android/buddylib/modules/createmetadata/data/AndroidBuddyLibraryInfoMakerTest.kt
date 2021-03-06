package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.google.common.truth.Truth
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class AndroidBuddyLibraryInfoMakerTest {

    private lateinit var androidBuddyLibraryInfoMaker: AndroidBuddyLibraryInfoMaker

    @Before
    fun setUp() {
        androidBuddyLibraryInfoMaker = AndroidBuddyLibraryInfoMaker()
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
        checkGroupIsValid("some group", true)
        checkGroupIsValid("Some group", true)
        checkGroupIsValid("", false)
        checkGroupIsValid("    ", false)
        checkGroupIsValid("     ", false)
        checkGroupIsValid("[[]]", false)
        checkGroupIsValid("&^$£$^&&", false)
        checkGroupIsValid("&^ab$£$^&&", true)
    }

    @Test
    fun `Check if name is valid`() {
        checkNameIsValid("some.name", true)
        checkNameIsValid("some name", true)
        checkNameIsValid("Some name", true)
        checkNameIsValid("    ", false)
        checkNameIsValid("", false)
        checkNameIsValid("[[]]", false)
        checkNameIsValid("&^$£$^&&", false)
        checkNameIsValid("&^ab$£$^&&", true)
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

    @Test
    fun `Format group if needed`() {
        checkGroupFormatted("some.group", "some.group")
        checkGroupFormatted("some-group", "some.group")
        checkGroupFormatted("some group", "some.group")
        checkGroupFormatted("somegroup", "somegroup")
        checkGroupFormatted("Some group", "some.group")
        checkGroupFormatted("Some group  ", "some.group")
        checkGroupFormatted("Some  group  ", "some.group")
        checkGroupFormatted(" Some  group  ", "some.group")
        checkGroupFormatted(" Some  group", "some.group")
        checkGroupFormatted(" Some group  ", "some.group")
        checkGroupFormatted("-some group", "some.group")
        checkGroupFormatted("-.somegroup", "somegroup")
        checkGroupFormatted("somegroup--", "somegroup")
        checkGroupFormatted("some group-", "some.group")
        checkGroupFormatted("some group.", "some.group")
        checkGroupFormatted("some group-", "some.group")
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
            Truth.assertThat(e.message).isEqualTo("The group provided '$group' is not valid.")
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
            Truth.assertThat(e.message).isEqualTo("The name provided '$name' is not valid.")
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

    private fun checkGroupFormatted(originalGroup: String, expectedGroup: String) {
        val id = "some.id"
        val name = "some.name"
        val version = "1.0.0"
        val pluginNames = setOf("some.Plugin")

        val info = callMake(id, originalGroup, name, version, pluginNames)

        Truth.assertThat(info.group).isEqualTo(expectedGroup)
    }

    private fun callMake(
        id: String,
        group: String,
        name: String,
        version: String,
        pluginNames: Set<String>
    ): AndroidBuddyLibraryInfo {
        return androidBuddyLibraryInfoMaker.make(
            id,
            group, name, version,
            pluginNames
        )
    }
}