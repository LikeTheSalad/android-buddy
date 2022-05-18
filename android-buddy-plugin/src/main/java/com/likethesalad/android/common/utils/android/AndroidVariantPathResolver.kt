package com.likethesalad.android.common.utils.android

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AndroidVariantPathResolver @AssistedInject constructor(
    @Assisted("variantName") private val variantName: String,
    @Assisted("flavorName") private val flavorName: String,
    @Assisted("buildTypeName") private val buildTypeName: String,
    @Assisted private val flavors: List<String>
) {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("variantName") variantName: String,
            @Assisted("flavorName") flavorName: String,
            @Assisted("buildTypeName") buildTypeName: String,
            flavors: List<String>
        ): AndroidVariantPathResolver
    }

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