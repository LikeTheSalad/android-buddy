package com.likethesalad.android.buddy.extension

import com.android.build.api.transform.QualifiedContent
import com.likethesalad.android.buddy.extension.libraries.LibrariesPolicyExtension
import com.likethesalad.android.buddy.extension.libraries.TransformationScopeExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class AndroidBuddyExtension(objectFactory: ObjectFactory) {

    val librariesPolicy = objectFactory.newInstance(LibrariesPolicyExtension::class.java)
    val transformationScope = objectFactory.newInstance(TransformationScopeExtension::class.java)

    fun librariesPolicy(action: Action<LibrariesPolicyExtension>) {
        action.execute(librariesPolicy)
    }

    fun transformationScope(action: Action<TransformationScopeExtension>) {
        action.execute(transformationScope)
    }
}