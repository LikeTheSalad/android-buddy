package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradleConfigurationsFinder
@Inject constructor(private val gradleConfigurationsProvider: GradleConfigurationsProvider) {

    private val allowedConfigNameRegex = Regex("^(?!androidBuddy)(.*([Ii]mplementation|[Aa]pi))$")
    private val testRegex = Regex("[Tt]est")

    fun searchForAllowedConfigurations(onConfigurationFound: (Configuration) -> Unit) {
        gradleConfigurationsProvider.getConfigurationContainer().all {
            if (isAllowed(it.name)) {
                onConfigurationFound.invoke(it)
            }
        }
    }

    private fun isAllowed(name: String): Boolean {
        return allowedConfigNameRegex.matches(name) && !testRegex.containsMatchIn(name)
    }
}