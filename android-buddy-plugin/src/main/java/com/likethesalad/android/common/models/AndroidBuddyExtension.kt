package com.likethesalad.android.common.models

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class AndroidBuddyExtension @Inject constructor(objectFactory: ObjectFactory) {

    val pluginNames = objectFactory.setProperty(String::class.java)
}