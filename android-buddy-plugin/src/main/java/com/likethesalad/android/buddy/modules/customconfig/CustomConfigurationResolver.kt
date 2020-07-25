package com.likethesalad.android.buddy.modules.customconfig

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.utils.AndroidPluginDataProvider
import com.likethesalad.android.common.utils.Constants
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer

@AutoFactory
class CustomConfigurationResolver(
    @Provided gradleConfigurationsProvider: GradleConfigurationsProvider,
    androidPluginDataProvider: AndroidPluginDataProvider
) {
    private val variantPath by lazy { androidPluginDataProvider.getVariantPath() }
    private val configurationContainer: ConfigurationContainer by lazy {
        gradleConfigurationsProvider.getConfigurationContainer()
    }

    fun getApiConfigurations(): List<Configuration> {
        val configs = mutableListOf(getConfigurationNameFor("", Type.API))
        configs.addAll(variantPath.map { getConfigurationNameFor(it, Type.API) })

        return convertNamesToConfigurations(configs)
    }

    fun getImplementationConfigurations(): List<Configuration> {
        val configs = mutableListOf(getConfigurationNameFor("", Type.IMPLEMENTATION))
        configs.addAll(variantPath.map { getConfigurationNameFor(it, Type.IMPLEMENTATION) })

        return convertNamesToConfigurations(configs)
    }

    private fun convertNamesToConfigurations(names: List<String>): List<Configuration> {
        val configs = mutableListOf<Configuration>()

        for (name in names) {
            configurationContainer.findByName(name)?.let {
                configs.add(it)
            }
        }

        return configs
    }

    private fun getConfigurationNameFor(middle: String, type: Type): String {
        return "${Constants.CUSTOM_CONFIGURATIONS_PREFIX}${middle.capitalize()}${type.suffix}"
    }

    enum class Type(val suffix: String) {
        IMPLEMENTATION(Constants.IMPLEMENTATION_DEPENDENCY_SUFFIX),
        API(Constants.API_DEPENDENCY_SUFFIX)
    }
}