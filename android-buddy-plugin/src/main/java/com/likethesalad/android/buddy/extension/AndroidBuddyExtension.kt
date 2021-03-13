package com.likethesalad.android.buddy.extension

import com.likethesalad.android.buddy.extension.libraries.LibrariesOptions
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class AndroidBuddyExtension(objectFactory: ObjectFactory) {

    val librariesOptions = objectFactory.newInstance(LibrariesOptions::class.java)

    fun librariesPolicy(action: Action<LibrariesOptions>) {
        action.execute(librariesOptions)
    }
}