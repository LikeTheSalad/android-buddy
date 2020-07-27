package com.likethesalad.android.buddy.modules.customconfig.utils

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationType
import com.likethesalad.android.common.utils.Constants

@AutoFactory
class ConfigurationNamesGenerator(
    private val configurationGroup: ConfigurationGroup,
    private val buildTypeName: String
) {
    private val capitalizedBuildTypeName = buildTypeName.capitalize()

    fun getAndroidBuddyBucketName(): String {
        return getCustomConfigurationName(configurationGroup.bucketType)
    }

    fun getAndroidBuddyResolvableName(): String {
        return getCustomConfigurationName(configurationGroup.resolvableType)
    }

    fun getAndroidBucketName(): String {
        return if (buildTypeName.isEmpty()) {
            configurationGroup.bucketType.configurationName
        } else {
            "$buildTypeName${configurationGroup.bucketType.capitalizedName}"
        }
    }

    private fun getCustomConfigurationName(configurationType: ConfigurationType): String {
        return "${Constants.CUSTOM_CONFIGURATIONS_PREFIX}$capitalizedBuildTypeName${configurationType.capitalizedName}"
    }
}