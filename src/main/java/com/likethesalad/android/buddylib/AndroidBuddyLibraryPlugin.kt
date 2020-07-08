package com.likethesalad.android.buddylib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin

open class AndroidBuddyLibraryPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(JavaLibraryPlugin::class.java)
    }
}