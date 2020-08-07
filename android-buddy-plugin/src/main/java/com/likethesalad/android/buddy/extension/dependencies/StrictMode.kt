package com.likethesalad.android.buddy.extension.dependencies

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class StrictMode @Inject constructor(objectFactory: ObjectFactory) {

    val enabled = objectFactory.property(Boolean::class.java)

    init {
        enabled.convention(false)
    }
}