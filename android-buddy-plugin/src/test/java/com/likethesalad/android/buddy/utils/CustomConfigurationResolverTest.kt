package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationResolver
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.junit.Before
import org.junit.Test

class CustomConfigurationResolverTest : BaseMockable() {

    @MockK
    lateinit var gradleConfigurationsProvider: GradleConfigurationsProvider

    @MockK
    lateinit var configurationContainer: ConfigurationContainer

    @MockK
    lateinit var androidPluginDataProvider: AndroidPluginDataProvider

    private lateinit var customConfigurationResolver: CustomConfigurationResolver

    @Before
    fun setUp() {
        every { configurationContainer.findByName(any()) }.answers {
            val configMock = mockk<Configuration>()
            every { configMock.name }.returns(firstArg())
            configMock
        }
        every { gradleConfigurationsProvider.getConfigurationContainer() }.returns(configurationContainer)
        customConfigurationResolver =
            CustomConfigurationResolver(
                gradleConfigurationsProvider,
                androidPluginDataProvider
            )
    }

    @Test
    fun `Provide implementation configurations for path`() {
        val variantPath = listOf("demo", "debug", "demoDebug")
        val expectedConfigNames = listOf(
            "androidBuddyImplementation",
            "androidBuddyDemoImplementation",
            "androidBuddyDebugImplementation",
            "androidBuddyDemoDebugImplementation"
        )
        every { androidPluginDataProvider.getVariantPath() }.returns(variantPath)

        val result = customConfigurationResolver.getImplementationConfigurations()

        Truth.assertThat(result.map { it.name }).containsExactlyElementsIn(expectedConfigNames)
    }

    @Test
    fun `Provide api configurations for path`() {
        val variantPath = listOf("full", "release", "fullRelease")
        val expectedConfigNames = listOf(
            "androidBuddyApi",
            "androidBuddyFullApi",
            "androidBuddyReleaseApi",
            "androidBuddyFullReleaseApi"
        )
        every { androidPluginDataProvider.getVariantPath() }.returns(variantPath)

        val result = customConfigurationResolver.getApiConfigurations()

        Truth.assertThat(result.map { it.name }).containsExactlyElementsIn(expectedConfigNames)
    }

    @Test
    fun `Provide only existing implementation configurations, in case some are not found`() {
        val variantPath = listOf("demo", "debug", "demoDebug")
        val expectedConfigNames = listOf(
            "androidBuddyImplementation",
            "androidBuddyDebugImplementation",
            "androidBuddyDemoDebugImplementation"
        )
        every { configurationContainer.findByName("androidBuddyDemoImplementation") }.returns(null)
        every { androidPluginDataProvider.getVariantPath() }.returns(variantPath)

        val result = customConfigurationResolver.getImplementationConfigurations()

        Truth.assertThat(result.map { it.name }).containsExactlyElementsIn(expectedConfigNames)
    }

    @Test
    fun `Provide only existing api configurations, in case some are not found`() {
        val variantPath = listOf("full", "release", "fullRelease")
        val expectedConfigNames = listOf(
            "androidBuddyApi",
            "androidBuddyFullApi",
            "androidBuddyFullReleaseApi"
        )
        every { configurationContainer.findByName("androidBuddyReleaseApi") }.returns(null)
        every { androidPluginDataProvider.getVariantPath() }.returns(variantPath)

        val result = customConfigurationResolver.getApiConfigurations()

        Truth.assertThat(result.map { it.name }).containsExactlyElementsIn(expectedConfigNames)
    }
}