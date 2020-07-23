package com.likethesalad.android.buddy.utils

class AndroidVariantPathResolver constructor(
    private val variantName: String,
    private val buildTypeName: String,
    private val flavors: List<String>
) {
    fun getTopBottomPath(): List<String> {
        val result = mutableListOf<String>()

        if (flavors.isNotEmpty()) {
            result.addAll(flavors.reversed())
        }

        val concatenatedFlavors = getFlavorsCamelcaseConcat(flavors)
        if (concatenatedFlavors.isNotEmpty()) {
            result.add(concatenatedFlavors)
        }

        result.add(buildTypeName)

        if (variantName != buildTypeName) {
            result.add(variantName)
        }

        return result
    }

    private fun getFlavorsCamelcaseConcat(flavors: List<String>): String {
        if (flavors.size > 1) {
            return flavors.drop(1).fold(flavors.first()) { accumulated, currentItem ->
                "$accumulated${currentItem.capitalize()}"
            }
        }

        return ""
    }
}