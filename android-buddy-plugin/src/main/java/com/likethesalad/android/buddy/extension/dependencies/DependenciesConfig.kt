package com.likethesalad.android.buddy.extension.dependencies

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class DependenciesConfig @Inject constructor(objectFactory: ObjectFactory) {

    val strictModeEnabled = objectFactory.property(Boolean::class.java)
    val disableAllTransformations = objectFactory.property(Boolean::class.java)
    val alwaysLogTransformationNames = objectFactory.property(Boolean::class.java)

    init {
        strictModeEnabled.convention(false)
        disableAllTransformations.convention(false)
        alwaysLogTransformationNames.convention(true)
    }
}