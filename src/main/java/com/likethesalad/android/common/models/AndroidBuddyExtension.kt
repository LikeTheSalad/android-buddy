package com.likethesalad.android.common.models

open class AndroidBuddyExtension {

    private var transformationPlugins = setOf<String>()

    fun setPlugins(names: Set<String>) {
        transformationPlugins = names
    }

    fun getTransformations(): Set<TransformationDeclaration> {
        return transformationPlugins.map {
            TransformationDeclaration(it)
        }.toSet()
    }
}