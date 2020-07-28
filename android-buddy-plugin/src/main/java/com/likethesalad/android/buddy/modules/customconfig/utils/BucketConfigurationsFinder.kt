package com.likethesalad.android.buddy.modules.customconfig.utils

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.data.BucketConfiguration
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

@AppScope
class BucketConfigurationsFinder
@Inject constructor(private val gradleConfigurationsProvider: GradleConfigurationsProvider) {

    private val allowedConfigNameRegex = Regex("^(.*)([Ii]mplementation|[Aa]pi)$")
    private val ignoreRegex = Regex("([Tt]est|[Aa]ndroidBuddy)")

    fun searchForAllowedConfigurations(onConfigurationFound: (BucketConfiguration) -> Unit) {
        gradleConfigurationsProvider.getConfigurationContainer().all {
            allowedConfigNameRegex.matchEntire(it.name)?.let { match ->
                if (isAllowed(it.name)) {
                    onConfigurationFound.invoke(getBucketConfiguration(it, match))
                }
            }
        }
    }

    private fun isAllowed(name: String): Boolean {
        return !ignoreRegex.containsMatchIn(name)
    }

    private fun getBucketConfiguration(
        configuration: Configuration,
        matchResult: MatchResult
    ): BucketConfiguration {
        val prefix = matchResult.groupValues[1]
        val suffix = matchResult.groupValues[2]
        return BucketConfiguration(configuration, prefix, suffix)
    }
}