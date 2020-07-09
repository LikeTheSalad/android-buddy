package com.likethesalad.android.buddylib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin

open class AndroidBuddyLibraryPlugin : Plugin<Project> {

    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project
        project.pluginManager.apply(JavaLibraryPlugin::class.java)
    }
}