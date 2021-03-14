package com.likethesalad.android.buddy.extension

import com.likethesalad.android.buddy.extension.libraries.LibrariesPolicyExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class AndroidBuddyExtension(objectFactory: ObjectFactory) {

    val librariesPolicy = objectFactory.newInstance(LibrariesPolicyExtension::class.java)

    fun librariesPolicy(action: Action<LibrariesPolicyExtension>) {
        action.execute(librariesPolicy)
    }
}