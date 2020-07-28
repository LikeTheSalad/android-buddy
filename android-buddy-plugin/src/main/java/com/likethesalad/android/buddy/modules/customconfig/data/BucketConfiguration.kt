package com.likethesalad.android.buddy.modules.customconfig.data

import org.gradle.api.artifacts.Configuration

data class BucketConfiguration(
    val configuration: Configuration,
    val prefix: String,
    val suffix: String
)