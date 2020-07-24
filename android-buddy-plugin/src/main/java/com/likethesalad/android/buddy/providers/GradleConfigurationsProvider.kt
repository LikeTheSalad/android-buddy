package com.likethesalad.android.buddy.providers

import org.gradle.api.artifacts.ConfigurationContainer

interface GradleConfigurationsProvider {
    fun getConfigurationContainer(): ConfigurationContainer
}