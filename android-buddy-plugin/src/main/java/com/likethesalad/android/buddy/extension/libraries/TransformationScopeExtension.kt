package com.likethesalad.android.buddy.extension.libraries

import com.android.build.api.transform.QualifiedContent
import com.likethesalad.android.buddy.extension.libraries.scope.LibrariesScopeExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class TransformationScopeExtension @Inject constructor(objectFactory: ObjectFactory) {
    val scope = objectFactory.setProperty(String::class.java)
    val excludePrefixes = objectFactory.setProperty(String::class.java)

    init {
        scope.convention(mutableSetOf(QualifiedContent.Scope.PROJECT.name))
        excludePrefixes.convention(emptySet())
    }
}