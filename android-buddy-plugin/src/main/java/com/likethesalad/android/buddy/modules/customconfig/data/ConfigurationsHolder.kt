package com.likethesalad.android.buddy.modules.customconfig.data

import org.gradle.api.artifacts.Configuration

data class ConfigurationsHolder(
    val group: ConfigurationGroup,
    val bucket: Configuration,
    val resolvable: Configuration?
)