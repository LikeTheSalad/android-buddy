package com.likethesalad.android.buddy.modules.customconfig

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.utils.ConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.utils.AndroidVariantDataProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer

@AutoFactory
class CustomConfigurationResolver(
    @Provided private val configurationNamesGeneratorFactory: ConfigurationNamesGeneratorFactory,
    @Provided gradleConfigurationsProvider: GradleConfigurationsProvider,
    androidVariantDataProvider: AndroidVariantDataProvider
) {
    private val buildTypeName by lazy { androidVariantDataProvider.getVariantBuildTypeName() }
    private val configurationContainer: ConfigurationContainer by lazy {
        gradleConfigurationsProvider.getConfigurationContainer()
    }

    fun getApiConfiguration(): Configuration {
        val configNames = configurationNamesGeneratorFactory.create(ConfigurationGroup.API_GROUP, buildTypeName)

        return configurationContainer.getByName(configNames.getResolvableName())
    }

    fun getImplementationConfiguration(): Configuration {
        val configNames = configurationNamesGeneratorFactory.create(ConfigurationGroup.RUNTIME_GROUP, buildTypeName)

        return configurationContainer.getByName(configNames.getResolvableName())
    }
}