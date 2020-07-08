package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.likethesalad.android.buddy.providers.AndroidBootClasspathProvider
import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import com.likethesalad.android.buddy.utils.DaggerInjector
import com.likethesalad.android.common.models.AndroidBuddyExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

open class AndroidBuddyPlugin : Plugin<Project>,
    AndroidBootClasspathProvider, FileTreeIteratorProvider,
    PluginClassNamesProvider {
    private lateinit var project: Project
    private lateinit var androidExtension: AppExtension
    private lateinit var androidBuddyExtension: AndroidBuddyExtension

    companion object {
        private const val EXTENSION_NAME = "androidBuddy"
    }

    override fun apply(project: Project) {
        DaggerInjector.init(this)
        this.project = project
        androidBuddyExtension = createExtension()
        androidExtension = project.extensions.getByType(AppExtension::class.java)
        androidExtension.registerTransform(DaggerInjector.getByteBuddyTransform())
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
        return androidBuddyExtension.getTransformations().map { it.plugin }.toSet()
    }
}