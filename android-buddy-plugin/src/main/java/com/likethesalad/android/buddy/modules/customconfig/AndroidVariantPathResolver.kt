package com.likethesalad.android.buddy.modules.customconfig

import com.google.auto.factory.AutoFactory

@AutoFactory
class AndroidVariantPathResolver constructor(
    private val variantName: String,
    private val flavorName: String,
    private val buildTypeName: String,
    private val flavors: List<String>
) {
    fun getTopBottomPath(): List<String> {
        val result = mutableListOf<String>()

        if (flavors.isNotEmpty()) {
            result.addAll(flavors.reversed())
        }

        if (flavorName.isNotEmpty() && flavorName !in result) {
            result.add(flavorName)
        }

        result.add(buildTypeName)

        if (variantName != buildTypeName) {
            result.add(variantName)
        }

        return result
    }
}