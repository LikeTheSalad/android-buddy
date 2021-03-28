package com.likethesalad.android.buddylib.extension

import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class AndroidBuddyLibExtension(objectFactory: ObjectFactory) {

    val id = objectFactory.property(String::class.java)
    val exposedTransformationNames = objectFactory.setProperty(String::class.java)
}