package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationType
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationsHolder
import com.likethesalad.android.buddy.modules.customconfig.utils.ConfigurationBuildTypeNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.utils.AndroidPluginDataProvider
import com.likethesalad.android.common.utils.Constants
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

@AppScope
class CustomConfigurationCreator
@Inject constructor(
    private val androidPluginDataProvider: AndroidPluginDataProvider,
    private val gradleConfigurationsProvider: GradleConfigurationsProvider,
    private val configurationBuildTypeNamesGeneratorFactory: ConfigurationBuildTypeNamesGeneratorFactory
) {

    private val configurationContainer by lazy { gradleConfigurationsProvider.getConfigurationContainer() }

    companion object {
        private val AS_CONSUMABLE = Action<Configuration> {
            it.isCanBeResolved = false
            it.isCanBeConsumed = true
        }
        private val AS_BUCKET = Action<Configuration> {
            it.isCanBeResolved = false
            it.isCanBeConsumed = false
        }
        private val AS_RESOLVABLE = Action<Configuration> {
            it.isCanBeConsumed = false
            it.isCanBeResolved = true
        }
    }

    fun createAndroidBuddyConfigurations() {
        val mainApiBucket = createBucket(getCustomConfigurationName(ConfigurationType.API.capitalizedName))
        val mainImplementationBucket =
            createBucket(getCustomConfigurationName(ConfigurationType.IMPLEMENTATION.capitalizedName))
        val buildTypeNames = androidPluginDataProvider.getBuildTypeNames()

        buildTypeNames.forEach { name ->
            createConfigurationsForBuildType(name, ConfigurationGroup.API_GROUP, mainApiBucket)
            createConfigurationsForBuildType(name, ConfigurationGroup.RUNTIME_GROUP, mainImplementationBucket)
        }

        attachToAndroidConfigurations(mainApiBucket, mainImplementationBucket)
    }

    private fun attachToAndroidConfigurations(apiConfig: Configuration, implementationConfig: Configuration) {
        configurationContainer.named(ConfigurationType.API.configurationName) {
            it.extendsFrom(apiConfig)
        }
        configurationContainer.named(ConfigurationType.IMPLEMENTATION.configurationName) {
            it.extendsFrom(implementationConfig)
        }
    }

    private fun createConfigurationsForBuildType(
        buildTypeName: String,
        configurationGroup: ConfigurationGroup,
        mainBucket: Configuration
    ) {
        val buildTypeConfigs = createConfigurationsFor(buildTypeName, configurationGroup)
        buildTypeConfigs.bucket.extendsFrom(mainBucket)
    }

    private fun createConfigurationsFor(
        buildTypeName: String,
        configurationGroup: ConfigurationGroup
    ): ConfigurationsHolder {
        val generator = configurationBuildTypeNamesGeneratorFactory.create(configurationGroup, buildTypeName)
        val bucket = createBucket(getCustomConfigurationName(generator.getCapitalizedBucketName()))
        val consumable = createConsumable(getCustomConfigurationName(generator.getCapitalizedConsumableName()))
        val resolvable = createResolvable(getCustomConfigurationName(generator.getCapitalizedResolvableName()))

        consumable.extendsFrom(bucket)
        resolvable.extendsFrom(consumable)

        return ConfigurationsHolder(configurationGroup, bucket, consumable, resolvable)
    }

    private fun createBucket(name: String): Configuration {
        return configurationContainer.create(name, AS_BUCKET)
    }

    private fun createConsumable(name: String): Configuration {
        return configurationContainer.create(name, AS_CONSUMABLE)
    }

    private fun createResolvable(name: String): Configuration {
        return configurationContainer.create(name, AS_RESOLVABLE)
    }

    private fun getCustomConfigurationName(capitalizedSuffix: String): String {
        return "${Constants.CUSTOM_CONFIGURATIONS_PREFIX}$capitalizedSuffix"
    }
}