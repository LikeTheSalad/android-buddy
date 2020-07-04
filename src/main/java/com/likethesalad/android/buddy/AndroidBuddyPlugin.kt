package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.likethesalad.android.buddy.di.AndroidBuddyModule
import com.likethesalad.android.buddy.di.DaggerAndroidBuddyComponent
import com.likethesalad.android.buddy.models.AndroidBuddyExtension
import com.likethesalad.android.buddy.utils.AndroidBootClasspathProvider
import com.likethesalad.android.buddy.utils.FileTreeIteratorProvider
import com.likethesalad.android.buddy.utils.PluginClassNamesProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class AndroidBuddyPlugin : Plugin<Project>, AndroidBootClasspathProvider, FileTreeIteratorProvider,
    PluginClassNamesProvider {
    private lateinit var project: Project
    private lateinit var androidExtension: AppExtension
    private lateinit var androidBuddyExtension: AndroidBuddyExtension

    companion object {
        private const val EXTENSION_NAME = "androidBuddy"
    }

    override fun apply(project: Project) {
        val transform = DaggerAndroidBuddyComponent.builder()
            .androidBuddyModule(AndroidBuddyModule(this))
            .build()
            .transform()
        this.project = project
        androidBuddyExtension = createExtension()
        androidExtension = project.extensions.getByType(AppExtension::class.java)
        androidExtension.registerTransform(transform)
    }

    private fun createExtension(): AndroidBuddyExtension {
        return project.extensions.create(EXTENSION_NAME, AndroidBuddyExtension::class.java)
    }

    override fun getBootClasspath(): Set<File> {
        return androidExtension.bootClasspath.toSet()
    }

    override fun createFileTreeIterator(folder: File): Iterator<File> {
        return project.fileTree(folder).iterator()
    }

    override fun getPluginClassNames(): Set<String> {
        return androidBuddyExtension.plugins.toSet()
    }
}