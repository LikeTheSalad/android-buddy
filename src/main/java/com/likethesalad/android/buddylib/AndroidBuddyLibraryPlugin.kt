package com.likethesalad.android.buddylib

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile

open class AndroidBuddyLibraryPlugin : Plugin<Project> {

    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project
        project.pluginManager.apply(JavaLibraryPlugin::class.java)
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        project.tasks.named("compileJava", JavaCompile::class.java) {
            println("JAJJAJAJA: ${sourceSets.getByName("main")}")
        }
    }
}