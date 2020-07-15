package com.likethesalad.android.buddylib.actions

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.PluginsFinder
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.fail
import org.junit.Test

class VerifyPluginClassesProvidedActionTest : BaseMockable() {

    @MockK
    lateinit var pluginsFinder: PluginsFinder

    @Test
    fun `Check provided plugin names are found in actual built plugins`() {
        try {
            val providedNames = setOf("some.provided.Name", "another.provided.Name")
            val actualNames = setOf(
                "some.provided.Name", "another.provided.Name",
                "some.ignored.Name"
            )
            every { pluginsFinder.findBuiltPluginClassNames() }.returns(actualNames)

            createInstance(providedNames).execute()
        } catch (e: Exception) {
            fail("Should not have crashed")
        }
    }

    @Test
    fun `Crash when provided plugin names are not found in actual built plugins`() {
        try {
            val providedNames = setOf(
                "some.provided.Name", "another.provided.Name",
                "some.nonexisting.Name"
            )
            val actualNames = setOf(
                "some.provided.Name", "another.provided.Name",
                "some.ignored.Name"
            )
            every { pluginsFinder.findBuiltPluginClassNames() }.returns(actualNames)

            createInstance(providedNames).execute()

            fail("Should have crashed")
        } catch (e: IllegalArgumentException) {
            Truth.assertThat(e.message)
                .isEqualTo("Plugin(s) not found: [some.nonexisting.Name]")
        }
    }

    private fun createInstance(pluginNames: Set<String>): VerifyPluginClassesProvidedAction {
        return VerifyPluginClassesProvidedAction(
            pluginNames,
            pluginsFinder
        )
    }
}