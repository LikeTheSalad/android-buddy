package com.likethesalad.android.buddy.extension.libraries

import com.likethesalad.android.buddy.extension.libraries.scope.LibrariesScopeExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class LibrariesPolicyExtension @Inject constructor(objectFactory: ObjectFactory) {
    val scope = objectFactory.newInstance(LibrariesScopeExtension::class.java)

    fun scope(action: Action<LibrariesScopeExtension>) {
        action.execute(scope)
    }
}