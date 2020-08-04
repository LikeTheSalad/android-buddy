package com.likethesalad.android.buddy.extension.dependencies

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class DependenciesConfig @Inject constructor(objectFactory: ObjectFactory) {

    val strictMode = objectFactory.newInstance(StrictMode::class.java)
    val disableAllTransformations = objectFactory.property(Boolean::class.java)
    val alwaysLogTransformationNames = objectFactory.property(Boolean::class.java)

    init {
        disableAllTransformations.convention(false)
        alwaysLogTransformationNames.convention(true)
    }

    fun strictMode(action: Action<StrictMode>) {
        action.execute(strictMode)
    }
}