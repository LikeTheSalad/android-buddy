package com.likethesalad.android.buddy.modules.customconfig.utils

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationType
import com.likethesalad.android.common.utils.Constants

@AutoFactory
class CustomConfigurationNamesGenerator(private val group: ConfigurationGroup) {

    companion object {
        private val CAPITALIZED_CUSTOM_CONFIG_NAME = Constants.CUSTOM_CONFIGURATIONS_NAME.capitalize()
    }

    fun getSortedBucketConfigNames(variantPath: List<String>): List<String> {
        val names = mutableListOf(getCustomConfigurationName("", group.bucketType))
        variantPath.forEach {
            names.add(getCustomConfigurationName(it, group.bucketType))
        }
        return names
    }

    fun getResolvableConfigurationName(variantName: String): String {
        return getCustomConfigurationName(variantName, group.resolvableType)
    }

    private fun getCustomConfigurationName(
        prefix: String,
        type: ConfigurationType
    ): String {
        if (prefix.isEmpty()) {
            return "${Constants.CUSTOM_CONFIGURATIONS_NAME}${type.capitalizedName}"
        }

        return "$prefix$CAPITALIZED_CUSTOM_CONFIG_NAME${type.capitalizedName}"
    }
}