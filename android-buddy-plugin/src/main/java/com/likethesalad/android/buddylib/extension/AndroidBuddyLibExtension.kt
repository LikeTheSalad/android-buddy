package com.likethesalad.android.buddylib.extension

import org.gradle.api.model.ObjectFactory

open class AndroidBuddyLibExtension(objectFactory: ObjectFactory) {

    val pluginNames = objectFactory.setProperty(String::class.java)
}