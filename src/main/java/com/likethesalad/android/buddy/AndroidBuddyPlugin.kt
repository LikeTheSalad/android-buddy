package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.likethesalad.android.buddy.di.AppInjector
import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import com.likethesalad.android.common.base.BuddyPlugin
import com.likethesalad.android.common.models.AndroidBuddyExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import java.io.File

@Suppress("UnstableApiUsage")
open class AndroidBuddyPlugin : Plugin<Project>, BuddyPlugin, FileTreeIteratorProvider, PluginClassNamesProvider {

    private lateinit var project: Project
    private lateinit var androidBuddyExtension: AndroidBuddyExtension
    var appExtension: AppExtension? = null

    companion object {
        private const val EXTENSION_NAME = "androidBuddy"
    }

    override fun apply(project: Project) {
        AppInjector.init(this)
        this.project = project
        AppInjector.getByteBuddyDependencyHandler().addDependencies(project.dependencies, project.properties)
        androidBuddyExtension = createExtension()
        appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension?.registerTransform(AppInjector.getByteBuddyTransform())
    }

    private fun createExtension(): AndroidBuddyExtension {
        return project.extensions.create(EXTENSION_NAME, AndroidBuddyExtension::class.java)
    }

    override fun createFileTreeIterator(folder: File): Iterator<File> {
        return project.fileTree(folder).iterator()
    }

    override fun getPluginClassNames(): Set<String> {
        return androidBuddyExtension.pluginNames.get()
    }

    override fun getLogger(): Logger {
        return project.logger
    }
}