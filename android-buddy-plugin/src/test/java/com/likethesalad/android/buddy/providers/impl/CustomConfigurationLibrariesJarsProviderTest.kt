package com.likethesalad.android.buddy.providers.impl

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationLibrariesJarsProvider
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationResolver
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationResolverFactory
import com.likethesalad.android.buddy.utils.AndroidPluginDataProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.gradle.api.artifacts.Configuration
import org.junit.Before
import org.junit.Test
import java.io.File

class CustomConfigurationLibrariesJarsProviderTest : BaseMockable() {

    @MockK
    lateinit var customConfigurationResolverFactory: CustomConfigurationResolverFactory

    @MockK
    lateinit var customConfigurationResolver: CustomConfigurationResolver

    @MockK
    lateinit var androidPluginDataProvider: AndroidPluginDataProvider

    private lateinit var customConfigurationLibrariesJarsProvider: CustomConfigurationLibrariesJarsProvider

    @Before
    fun setUp() {
        every {
            customConfigurationResolverFactory.create(androidPluginDataProvider)
        }.returns(customConfigurationResolver)
        customConfigurationLibrariesJarsProvider =
            CustomConfigurationLibrariesJarsProvider(
                customConfigurationResolverFactory,
                androidPluginDataProvider
            )
    }

    @Test
    fun `Provide concatenated files`() {
        val implementationConfiguration1 = mockk<Configuration>()
        val implementationConfiguration2 = mockk<Configuration>()
        val apiConfiguration1 = mockk<Configuration>()
        val apiConfiguration2 = mockk<Configuration>()
        val allImplConfig = mockk<Configuration>()
        val allApiConfig = mockk<Configuration>()
        val allConfigsMerged = mockk<Configuration>()
        val implConfigs = listOf(implementationConfiguration1, implementationConfiguration2)
        val apiConfigs = listOf(apiConfiguration1, apiConfiguration2)
        val file1 = mockk<File>()
        val file2 = mockk<File>()
        val file3 = mockk<File>()
        val file4 = mockk<File>()
        val allFiles = setOf(file1, file2, file3, file4)
        every { customConfigurationResolver.getApiConfigurations() }.returns(apiConfigs)
        every { customConfigurationResolver.getImplementationConfigurations() }.returns(implConfigs)
        every { implementationConfiguration1.plus(implementationConfiguration2) }.returns(allImplConfig)
        every { apiConfiguration1.plus(apiConfiguration2) }.returns(allApiConfig)
        every { allApiConfig.plus(allImplConfig) }.returns(allConfigsMerged)
        every { allConfigsMerged.files }.returns(allFiles)

        val result = customConfigurationLibrariesJarsProvider.getLibrariesJars()

        Truth.assertThat(result).containsExactlyElementsIn(allFiles)
        verify {
            implementationConfiguration1.plus(implementationConfiguration2)
            apiConfiguration1.plus(apiConfiguration2)
            allApiConfig.plus(allImplConfig)
        }
    }
}