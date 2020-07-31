package com.likethesalad.android.buddy.modules.customconfig

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGenerator
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.utils.AndroidVariantDataProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
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
    lateinit var androidVariantDataProvider: AndroidVariantDataProvider

    @MockK
    lateinit var customConfigurationNamesGeneratorFactory: CustomConfigurationNamesGeneratorFactory

    private val variantName = "someName"
    private lateinit var customConfigurationResolver: CustomConfigurationResolver

    @Before
    fun setUp() {
        every { androidVariantDataProvider.variantName }.returns(variantName)
        every { gradleConfigurationsProvider.getConfigurationContainer() }.returns(configurationContainer)
        customConfigurationResolver =
            CustomConfigurationResolver(
                customConfigurationNamesGeneratorFactory,
                gradleConfigurationsProvider,
                androidVariantDataProvider
            )
    }

    @Test
    fun `Get compile configuration`() {
        val configName = "compileResolvable"
        val expectedConfig = createConfiguration(configName, ConfigurationGroup.COMPILE_GROUP)

        val result = customConfigurationResolver.getAndroidBuddyCompileConfiguration()

        Truth.assertThat(result).isEqualTo(expectedConfig)
        verify {
            configurationContainer.getByName(configName)
        }
    }

    @Test
    fun `Get runtime configuration`() {
        val configName = "runtimeResolvable"
        val expectedConfig = createConfiguration(configName, ConfigurationGroup.RUNTIME_GROUP)

        val result = customConfigurationResolver.getAndroidBuddyRuntimeConfiguration()

        Truth.assertThat(result).isEqualTo(expectedConfig)
        verify {
            configurationContainer.getByName(configName)
        }
    }

    private fun createConfiguration(name: String, group: ConfigurationGroup): Configuration {
        val configuration = mockk<Configuration>()
        val namesGenerator = mockk<CustomConfigurationNamesGenerator>()
        every {
            customConfigurationNamesGeneratorFactory.create(group)
        }.returns(namesGenerator)
        every {
            namesGenerator.getResolvableConfigurationName(variantName)
        }.returns(name)
        every {
            configurationContainer.getByName(name)
        }.returns(configuration)

        return configuration
    }
}