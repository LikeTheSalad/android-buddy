package com.likethesalad.android.buddy.extension.libraries


import com.likethesalad.android.buddy.configuration.TransformationScopeType
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class TransformationScopeExtension @Inject constructor(objectFactory: ObjectFactory) {
    val scope = objectFactory.property(String::class.java)
    val excludePrefixes = objectFactory.setProperty(String::class.java)

    init {
        scope.convention(TransformationScopeType.PROJECT.name)
        excludePrefixes.convention(emptySet())
    }
}