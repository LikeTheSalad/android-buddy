package com.likethesalad.android.buddy.modules.customconfig

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGenerator
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import com.likethesalad.android.common.utils.android.AndroidVariantPathResolver
import com.likethesalad.android.common.utils.android.AndroidVariantPathResolverFactory
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.junit.Before
import org.junit.Test

class CustomConfigurationVariantSetupTest : BaseMockable() {

    @MockK
    lateinit var androidExtensionDataProvider: AndroidExtensionDataProvider

    @MockK
    lateinit var androidVariantPathResolverFactory: AndroidVariantPathResolverFactory

    @MockK
    lateinit var customConfigurationNamesGeneratorFactory: CustomConfigurationNamesGeneratorFactory

    @MockK
    lateinit var configurationsProvider: GradleConfigurationsProvider

    @MockK
    lateinit var configurationContainer: ConfigurationContainer

    @MockK
    lateinit var variant: BaseVariant

    @MockK
    lateinit var variantCompileConfig: Configuration

    @MockK
    lateinit var variantRuntimeConfig: Configuration

    @MockK
    lateinit var customCompileResolvableConfig: Configuration

    @MockK
    lateinit var customRuntimeResolvableConfig: Configuration

    @MockK
    lateinit var customCompileResolvableAttributeContainer: AttributeContainer

    @MockK
    lateinit var customRuntimeResolvableAttributeContainer: AttributeContainer

    lateinit var variantProcessActionCaptor: CapturingSlot<(BaseVariant) -> Unit>

    private val variantName = "someVariantName"
    private val variantPath = listOf("some", "variant", "path")
    private val customCompileResolvableName = "someVariantNameCompileClasspath"
    private val customRuntimeResolvableName = "someVariantNameRuntimeClasspath"
    private val variantCompileBucketNames = listOf("api", "someApi", "somOtherApi", "debugApi")
    private val variantRuntimeBucketNames = listOf(
        "implementation", "someImplementation",
        "somOtherImplementation", "debugImplementation"
    )
    private val androidCompileConfigAttributes = mutableMapOf<Attribute<Any>, Any>()
    private val androidRuntimeConfigAttributes = mutableMapOf<Attribute<Any>, Any>()
    private lateinit var variantCompileResolvableActionCaptor: CapturingSlot<Action<Configuration>>
    private lateinit var variantRuntimeResolvableActionCaptor: CapturingSlot<Action<Configuration>>
    private lateinit var customConfigurationVariantSetup: CustomConfigurationVariantSetup

    @Before
    fun setUp() {
        variantProcessActionCaptor = slot()
        variantCompileResolvableActionCaptor = slot()
        variantRuntimeResolvableActionCaptor = slot()
        every { variant.name }.returns(variantName)
        every {
            androidExtensionDataProvider.allVariants(capture(variantProcessActionCaptor))
        } just Runs
        every { configurationsProvider.getConfigurationContainer() }.returns(configurationContainer)
        every { variant.compileConfiguration }.returns(variantCompileConfig)
        every { variant.runtimeConfiguration }.returns(variantRuntimeConfig)
        every {
            configurationContainer.create(
                customCompileResolvableName,
                capture(variantCompileResolvableActionCaptor)
            )
        }.returns(customCompileResolvableConfig)
        every {
            configurationContainer.create(
                customRuntimeResolvableName,
                capture(variantRuntimeResolvableActionCaptor)
            )
        }.returns(customRuntimeResolvableConfig)
        every { customCompileResolvableConfig.attributes }.returns(customCompileResolvableAttributeContainer)
        every { customRuntimeResolvableConfig.attributes }.returns(customRuntimeResolvableAttributeContainer)
        every {
            customCompileResolvableAttributeContainer.attribute((any<Attribute<Any>>()), any())
        }.returns(customCompileResolvableAttributeContainer)
        every {
            customRuntimeResolvableAttributeContainer.attribute((any<Attribute<Any>>()), any())
        }.returns(customRuntimeResolvableAttributeContainer)
        setUpMockConfigsDefaults(variantCompileConfig)
        setUpMockConfigsDefaults(customCompileResolvableConfig)
        setUpMockConfigsDefaults(customRuntimeResolvableConfig)
        setUpVariantPath()
        setUpNamesGenerated()
        setUpAttributes()
        customConfigurationVariantSetup = CustomConfigurationVariantSetup(
            androidExtensionDataProvider,
            androidVariantPathResolverFactory,
            customConfigurationNamesGeneratorFactory,
            configurationsProvider
        )

        customConfigurationVariantSetup.arrangeConfigurationsPerVariant()
    }

    @Test
    fun `Process variant`() {
        val compileConfigs = createConfigurationsForNames("api", "someApi", "somOtherApi", "debugApi")
        val runtimeConfigs = createConfigurationsForNames(
            "implementation", "someImplementation",
            "somOtherImplementation", "debugImplementation"
        )

        processVariant()

        verifyCustomBucketHierarchy(compileConfigs)
        verifyCustomBucketHierarchy(runtimeConfigs)
        verify {
            customCompileResolvableConfig.extendsFrom(compileConfigs.last())
            customRuntimeResolvableConfig.extendsFrom(runtimeConfigs.last())
        }
        verifyAllConfigsAttributesCopied()
        verifyAllCustomResolvableConfigSetUp()
    }

    private fun verifyCustomBucketHierarchy(customBuckets: List<Configuration>) {
        var previous = customBuckets.first()
        customBuckets.drop(1).forEach {
            verify {
                it.extendsFrom(previous)
            }
            previous = it
        }
    }

    private fun verifyAllCustomResolvableConfigSetUp() {
        verifyCustomResolvableConfigSetUp(customCompileResolvableConfig, variantCompileResolvableActionCaptor.captured)
        verifyCustomResolvableConfigSetUp(customRuntimeResolvableConfig, variantRuntimeResolvableActionCaptor.captured)
    }

    private fun verifyCustomResolvableConfigSetUp(
        configuration: Configuration,
        capturedAction: Action<Configuration>
    ) {
        capturedAction.execute(configuration)
        verify {
            configuration.isCanBeResolved = true
            configuration.isCanBeConsumed = false
        }
    }

    private fun verifyAllConfigsAttributesCopied() {
        verifyAttributesCopied(customCompileResolvableAttributeContainer, androidCompileConfigAttributes)
        verifyAttributesCopied(customRuntimeResolvableAttributeContainer, androidRuntimeConfigAttributes)
    }

    private fun verifyAttributesCopied(into: AttributeContainer, attributes: Map<Attribute<Any>, Any>) {
        attributes.forEach { (attr, value) ->
            verify {
                into.attribute(attr, value)
            }
        }
    }

    private fun createConfigurationsForNames(vararg names: String): List<Configuration> {
        val configs = mutableListOf<Configuration>()
        for (it in names) {
            configs.add(mockk<Configuration>().apply {
                every { name }.returns(it)
                every { configurationContainer.findByName(it) }.returns(this)
                every { extendsFrom(any()) }.returns(this)
            })
        }

        return configs
    }

    private fun processVariant() {
        variantProcessActionCaptor.captured.invoke(variant)
    }

    private fun setUpNamesGenerated() {
        val customCompileNamesGenerator = mockk<CustomConfigurationNamesGenerator>()
        val customRuntimeNamesGenerator = mockk<CustomConfigurationNamesGenerator>()
        every {
            customConfigurationNamesGeneratorFactory.create(ConfigurationGroup.COMPILE_GROUP)
        }.returns(customCompileNamesGenerator)
        every {
            customConfigurationNamesGeneratorFactory.create(ConfigurationGroup.RUNTIME_GROUP)
        }.returns(customRuntimeNamesGenerator)

        every {
            customCompileNamesGenerator.getResolvableConfigurationName(variantName)
        }.returns(customCompileResolvableName)
        every {
            customRuntimeNamesGenerator.getResolvableConfigurationName(variantName)
        }.returns(customRuntimeResolvableName)
        every {
            customCompileNamesGenerator.getSortedBucketConfigNames(variantPath)
        }.returns(variantCompileBucketNames)
        every {
            customRuntimeNamesGenerator.getSortedBucketConfigNames(variantPath)
        }.returns(variantRuntimeBucketNames)
    }

    private fun setUpVariantPath() {
        val variantFlavorName = "someFlavorName"
        val variantBuildTypeName = "someBuildTypeName"
        val flavorName1 = "some"
        val flavorName2 = "flavor"
        val flavor1 = mockk<ProductFlavor>()
        val flavor2 = mockk<ProductFlavor>()
        val buildType = mockk<BuildType>()
        val variantProductFlavors = listOf(flavor1, flavor2)
        val androidVariantPathResolver = mockk<AndroidVariantPathResolver>()
        every { flavor1.name }.returns(flavorName1)
        every { flavor2.name }.returns(flavorName2)
        every { buildType.name }.returns(variantBuildTypeName)
        every { variant.buildType }.returns(buildType)
        every { variant.flavorName }.returns(variantFlavorName)
        every { variant.productFlavors }.returns(variantProductFlavors)

        every {
            androidVariantPathResolverFactory.create(
                variantName, variantFlavorName, variantBuildTypeName,
                listOf(flavorName1, flavorName2)
            )
        }.returns(androidVariantPathResolver)

        every { androidVariantPathResolver.getTopBottomPath() }.returns(variantPath)
    }

    private fun setUpAttributes() {
        val compileAttributeContainer = mockk<AttributeContainer>()
        val runtimeAttributeContainer = mockk<AttributeContainer>()
        setUpConfigAttributes(compileAttributeContainer, androidCompileConfigAttributes)
        setUpConfigAttributes(runtimeAttributeContainer, androidRuntimeConfigAttributes)

        every { variantCompileConfig.attributes }.returns(compileAttributeContainer)
        every { variantRuntimeConfig.attributes }.returns(runtimeAttributeContainer)
    }

    private fun setUpConfigAttributes(
        androidConfigurationAttributes: AttributeContainer,
        attributes: MutableMap<Attribute<Any>, Any>
    ) {
        val attr1 = mockk<Attribute<Any>>()
        val attr2 = mockk<Attribute<Any>>()
        val attr3 = mockk<Attribute<Any>>()
        val value1 = mockk<Any>()
        val value2 = mockk<Any>()
        val value3 = mockk<Any>()
        attributes[attr1] = value1
        attributes[attr2] = value2
        attributes[attr3] = value3
        every { androidConfigurationAttributes.getAttribute(attr1) }.returns(value1)
        every { androidConfigurationAttributes.getAttribute(attr2) }.returns(value2)
        every { androidConfigurationAttributes.getAttribute(attr3) }.returns(value3)

        every { androidConfigurationAttributes.keySet() }.returns(attributes.keys as Set<Attribute<*>>)
    }

    private fun setUpMockConfigsDefaults(configuration: Configuration) {
        every { configuration.extendsFrom(any()) }.returns(configuration)
        every { configuration.extendsFrom(any()) }.returns(configuration)
        every { configuration.setTransitive(any()) }.returns(configuration)
    }
}