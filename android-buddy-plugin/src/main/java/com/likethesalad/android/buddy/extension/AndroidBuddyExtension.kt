package com.likethesalad.android.buddy.extension

import com.likethesalad.android.buddy.extension.libraries.LibrariesOptions
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider

@Suppress("UnstableApiUsage")
open class AndroidBuddyExtension(objectFactory: ObjectFactory) {

    val librariesOptions = objectFactory.newInstance(LibrariesOptions::class.java)

    @JvmOverloads
    fun librariesPolicy(policyNameProvider: Provider<String>, argsProvider: Provider<Iterable<Any>>? = null) {
        librariesOptions.policyName.set(policyNameProvider)
        if (argsProvider != null) {
            librariesOptions.args.set(argsProvider)
        }
    }
}