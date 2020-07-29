package com.likethesalad.android.buddy.extension.dependencies

import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class DependenciesConfig(objectFactory: ObjectFactory) {

    val strictModeEnabled = objectFactory.property(Boolean::class.java)
    val disableAllTransformations = objectFactory.property(Boolean::class.java)
    val alwaysLogTransformationNames = objectFactory.property(Boolean::class.java)

    init {
        strictModeEnabled.set(false)
        disableAllTransformations.set(false)
        alwaysLogTransformationNames.set(true)
    }
}