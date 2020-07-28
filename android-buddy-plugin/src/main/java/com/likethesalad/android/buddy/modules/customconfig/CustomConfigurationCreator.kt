package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.utils.BucketConfigurationsFinder
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.common.utils.Constants
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

@AppScope
class CustomConfigurationCreator
@Inject constructor(
    private val bucketConfigurationsFinder: BucketConfigurationsFinder,
    private val gradleConfigurationsProvider: GradleConfigurationsProvider
) {

    companion object {
        private val CUSTOM_CONFIGURATIONS_CAPITALIZED_NAME = Constants.CUSTOM_CONFIGURATIONS_NAME.capitalize()
    }

    fun createAndroidBuddyConfigurations() {
        val configurationContainer = gradleConfigurationsProvider.getConfigurationContainer()
        val asBucket = Action<Configuration> {
            it.isCanBeResolved = false
            it.isCanBeConsumed = false
        }

        bucketConfigurationsFinder.searchForAllowedConfigurations {
            val customName = getCustomConfigurationName(it.prefix, it.suffix)
            val customConfig = configurationContainer.create(customName, asBucket)
            it.configuration.extendsFrom(customConfig)
        }
    }

    private fun getCustomConfigurationName(prefix: String, suffix: String): String {
        if (prefix.isNotEmpty()) {
            return "$prefix$CUSTOM_CONFIGURATIONS_CAPITALIZED_NAME$suffix"
        }

        return "${Constants.CUSTOM_CONFIGURATIONS_NAME}${suffix.capitalize()}"
    }
}