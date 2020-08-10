package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.utils.BucketConfigurationsFinder
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.common.utils.Constants
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

@AppScope
class CustomBucketConfigurationCreator
@Inject constructor(
    private val bucketConfigurationsFinder: BucketConfigurationsFinder,
    private val gradleConfigurationsProvider: GradleConfigurationsProvider
) {

    private val configurationContainer by lazy {
        gradleConfigurationsProvider.getConfigurationContainer()
    }

    companion object {
        private val CUSTOM_CONFIGURATIONS_CAPITALIZED_NAME = Constants.CUSTOM_CONFIGURATIONS_NAME.capitalize()
    }

    fun createAndroidBuddyConfigurations() {
        bucketConfigurationsFinder.searchForAllowedConfigurations {
            val customName = getCustomConfigurationName(it.prefix, it.suffix)
            val customConfig = createBucketConfig(customName)
            it.configuration.extendsFrom(customConfig)
        }
    }

    private fun createBucketConfig(name: String): Configuration {
        val config = configurationContainer.maybeCreate(name)
        config.isCanBeConsumed = false
        config.isCanBeResolved = false

        return config
    }

    private fun getCustomConfigurationName(prefix: String, suffix: String): String {
        if (prefix.isNotEmpty()) {
            return "$prefix$CUSTOM_CONFIGURATIONS_CAPITALIZED_NAME$suffix"
        }

        return "${Constants.CUSTOM_CONFIGURATIONS_NAME}${suffix.capitalize()}"
    }
}