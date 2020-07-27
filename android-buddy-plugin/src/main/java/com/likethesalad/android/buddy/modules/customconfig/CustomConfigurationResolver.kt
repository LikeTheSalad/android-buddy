package com.likethesalad.android.buddy.modules.customconfig

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.utils.ConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.utils.AndroidVariantDataProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer

@AutoFactory
class CustomConfigurationResolver(
    @Provided private val configurationNamesGeneratorFactory: ConfigurationNamesGeneratorFactory,
    @Provided gradleConfigurationsProvider: GradleConfigurationsProvider,
    private val androidVariantDataProvider: AndroidVariantDataProvider
) {
    private val buildTypeName by lazy { androidVariantDataProvider.getVariantBuildTypeName() }
    private val configurationContainer: ConfigurationContainer by lazy {
        gradleConfigurationsProvider.getConfigurationContainer()
    }

    fun getApiConfiguration(): Configuration {
        return getConfigurationFor(
            androidVariantDataProvider.getVariantCompileConfiguration(),
            ConfigurationGroup.COMPILE_GROUP
        )
    }

    fun getImplementationConfiguration(): Configuration {
        return getConfigurationFor(
            androidVariantDataProvider.getVariantRuntimeConfiguration(),
            ConfigurationGroup.RUNTIME_GROUP
        )
    }

    private fun getConfigurationFor(
        androidConfiguration: Configuration,
        configurationGroup: ConfigurationGroup
    ): Configuration {
        val configNames = configurationNamesGeneratorFactory.create(configurationGroup, buildTypeName)
        val customConfiguration = configurationContainer.getByName(configNames.getAndroidBuddyResolvableName())
        copyAttributes(androidConfiguration.attributes, customConfiguration.attributes)
        return customConfiguration
    }

    @Suppress("UNCHECKED_CAST", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun copyAttributes(from: AttributeContainer, to: AttributeContainer) {
        from.keySet().forEach { key ->
            to.attribute(key as Attribute<Any>, from.getAttribute(key))
        }
    }
}