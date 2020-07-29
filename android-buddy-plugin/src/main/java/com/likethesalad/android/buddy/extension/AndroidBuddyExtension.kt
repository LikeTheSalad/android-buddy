package com.likethesalad.android.buddy.extension

import com.likethesalad.android.buddy.extension.dependencies.DependenciesConfig
import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class AndroidBuddyExtension(objectFactory: ObjectFactory) {

    val dependencies = objectFactory.property(DependenciesConfig::class.java)
}