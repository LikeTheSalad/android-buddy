package com.likethesalad.android.buddy.extension

import com.likethesalad.android.buddy.extension.dependencies.DependenciesConfig
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class AndroidBuddyExtension(objectFactory: ObjectFactory) {

    val dependenciesConfig = objectFactory.newInstance(DependenciesConfig::class.java)

    fun dependenciesConfig(action: Action<DependenciesConfig>) {
        action.execute(dependenciesConfig)
    }
}