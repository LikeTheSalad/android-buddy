package com.likethesalad.android.buddy.extension.libraries.scope

import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class LibrariesScopeExtension @Inject constructor(objectFactory: ObjectFactory) {
    val type = objectFactory.property(String::class.java)
    val args = objectFactory.listProperty(Any::class.java)

    init {
        type.convention(LibrariesScopeType.USE_ALL.value)
        args.convention(emptyList())
    }
}