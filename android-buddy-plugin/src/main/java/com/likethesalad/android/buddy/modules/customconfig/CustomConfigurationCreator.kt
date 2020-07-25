package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.common.utils.Constants
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

@AppScope
class CustomConfigurationCreator
@Inject constructor(
    private val gradleConfigurationsFinder: GradleConfigurationsFinder,
    private val gradleConfigurationsProvider: GradleConfigurationsProvider
) {

    fun createAndroidBuddyConfigurations() {
        val configurationContainer = gradleConfigurationsProvider.getConfigurationContainer()
        val asConsumable = Action<Configuration> {
            it.isCanBeResolved = false
            it.isCanBeConsumed = true
        }

        gradleConfigurationsFinder.searchForAllowedConfigurations {
            val customConfig = configurationContainer.create(getCustomConfigurationName(it.name), asConsumable)
            it.extendsFrom(customConfig)
        }
    }

    private fun getCustomConfigurationName(originalName: String): String {
        return "${Constants.CUSTOM_CONFIGURATIONS_PREFIX}${originalName.capitalize()}"
    }
}