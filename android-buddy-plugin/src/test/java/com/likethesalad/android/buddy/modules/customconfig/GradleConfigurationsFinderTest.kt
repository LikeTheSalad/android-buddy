package com.likethesalad.android.buddy.modules.customconfig

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.DummyResourcesFinder
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.junit.Before
import org.junit.Test

class GradleConfigurationsFinderTest : BaseMockable() {

    @MockK
    lateinit var gradleConfigurationsProvider: GradleConfigurationsProvider

    @MockK
    lateinit var configurationContainer: ConfigurationContainer

    private val dummyResourcesFinder = DummyResourcesFinder()
    private lateinit var gradleConfigurationsFinder: GradleConfigurationsFinder

    @Before
    fun setUp() {
        every { gradleConfigurationsProvider.getConfigurationContainer() }.returns(configurationContainer)
        gradleConfigurationsFinder = GradleConfigurationsFinder(gradleConfigurationsProvider)
    }

    @Test
    fun `Get filtered configurations from no flavored set of configurations`() {
        verifyExpectedConfigNames(
            "noFlavors.txt",
            listOf(
                "api",
                "implementation",
                "releaseApi",
                "releaseImplementation",
                "debugImplementation",
                "debugApi"
            )
        )
    }

    @Test
    fun `Get filtered configurations from single flavored set of configurations`() {
        verifyExpectedConfigNames(
            "withFlavors.txt",
            listOf(
                "api",
                "implementation",
                "demoApi",
                "fullApi",
                "fullDebugApi",
                "fullReleaseApi",
                "demoDebugApi",
                "demoReleaseApi",
                "releaseApi",
                "releaseImplementation",
                "debugImplementation",
                "demoImplementation",
                "fullImplementation",
                "demoDebugImplementation",
                "demoReleaseImplementation",
                "fullDebugImplementation",
                "fullReleaseImplementation",
                "debugApi"
            )
        )
    }

    private fun verifyExpectedConfigNames(fileName: String, expectedNames: List<String>) {
        val dummyConfigNames = getDummyConfigs(fileName)
        val dummyConfigs = dummyConfigNames.map { createConfigurationMockWithName(it) }
        val actionCaptor = slot<Action<Configuration>>()
        val capturedNames = mutableListOf<String>()
        every { configurationContainer.all(capture(actionCaptor)) } just Runs

        gradleConfigurationsFinder.searchForAllowedConfigurations {
            capturedNames.add(it.name)
        }
        val action = actionCaptor.captured
        dummyConfigs.forEach {
            action.execute(it)
        }

        Truth.assertThat(capturedNames).containsExactlyElementsIn(expectedNames)
    }

    private fun createConfigurationMockWithName(name: String): Configuration {
        val configuration = mockk<Configuration>()
        every { configuration.name }.returns(name)
        return configuration
    }

    private fun getDummyConfigs(fileName: String): List<String> {
        val file = dummyResourcesFinder.getResourceFile("configurations/$fileName")
        return file.readLines()
    }
}