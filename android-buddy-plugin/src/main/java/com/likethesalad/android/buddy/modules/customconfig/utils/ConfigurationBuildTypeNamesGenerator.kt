package com.likethesalad.android.buddy.modules.customconfig.utils

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationType

@AutoFactory
class ConfigurationBuildTypeNamesGenerator(
    private val configurationGroup: ConfigurationGroup,
    buildTypeName: String
) {
    private val capitalizedBuildTypeName = buildTypeName.capitalize()

    fun getCapitalizedBucketName(): String {
        return getBuildConfigTypeName(configurationGroup.bucketType)
    }

    fun getCapitalizedConsumableName(): String {
        return getBuildConfigTypeName(configurationGroup.consumableType)
    }

    fun getCapitalizedResolvableName(): String {
        return getBuildConfigTypeName(configurationGroup.resolvableType)
    }

    private fun getBuildConfigTypeName(
        configurationType: ConfigurationType
    ): String {
        return "$capitalizedBuildTypeName${configurationType.capitalizedName}"
    }
}