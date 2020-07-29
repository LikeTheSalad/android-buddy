package com.likethesalad.android.buddy.entension

import com.likethesalad.android.buddy.entension.dependencies.DependenciesConfig
import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
class AndroidBuddyExtension(objectFactory: ObjectFactory) {

    val dependencies = objectFactory.property(DependenciesConfig::class.java)
}