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
        checkIdIsValid("one id", false)
        checkIdIsValid("one   id", false)
        checkIdIsValid("one@id", false)
        checkIdIsValid("one.id.", false)
        checkIdIsValid("one.id-", false)
        checkIdIsValid("  one.id", false)
        checkIdIsValid("", false)
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
            Truth.assertThat(e.message).isEqualTo("Id provided '$id' has not a valid format:" +
                    "\n -Only lowercase letters, numbers, '.' and '-' are allowed." +
                    "\n -It must start with a letter." +
                    "\n -It cannot end neither with '-' nor with '.'." +
                    "\n -There cannot be a consecutive '-' or '.' after a '-' and/or '.'.")
        }
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