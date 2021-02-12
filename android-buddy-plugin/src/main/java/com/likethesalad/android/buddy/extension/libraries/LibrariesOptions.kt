package com.likethesalad.android.buddy.extension.libraries

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class LibrariesOptions @Inject constructor(objectFactory: ObjectFactory) {
    val policyName = objectFactory.property(String::class.java)
    val args = objectFactory.listProperty(Any::class.java)

    init {
        policyName.convention(LibrariesPolicyType.USE_ALL.value)
        args.convention(emptyList())
    }
}