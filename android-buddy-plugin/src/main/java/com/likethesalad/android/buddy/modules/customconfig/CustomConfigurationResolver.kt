package com.likethesalad.android.buddy.modules.customconfig

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.common.utils.android.AndroidVariantDataProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer

@AutoFactory
class CustomConfigurationResolver(
    @Provided private val customConfigurationNamesGeneratorFactory: CustomConfigurationNamesGeneratorFactory,
    @Provided gradleConfigurationsProvider: GradleConfigurationsProvider,
    androidVariantDataProvider: AndroidVariantDataProvider
) {
    private val variantName by lazy { androidVariantDataProvider.variantName }
    private val configurationContainer: ConfigurationContainer by lazy {
        gradleConfigurationsProvider.getConfigurationContainer()
    }

    fun getAndroidBuddyCompileConfiguration(): Configuration {
        return getConfigurationFor(ConfigurationGroup.COMPILE_GROUP)
    }

    fun getAndroidBuddyRuntimeConfiguration(): Configuration {
        return getConfigurationFor(ConfigurationGroup.RUNTIME_GROUP)
    }

    private fun getConfigurationFor(configurationGroup: ConfigurationGroup): Configuration {
        val configNames = customConfigurationNamesGeneratorFactory.create(configurationGroup)
        return configurationContainer.getByName(configNames.getResolvableConfigurationName(variantName))
    }
}