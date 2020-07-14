package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.likethesalad.android.buddy.bytebuddy.TransformationLogger
import com.likethesalad.android.buddy.bytebuddy.TransformationLoggerFactory
import com.likethesalad.android.buddy.di.AppInjector
import com.likethesalad.android.buddy.providers.AndroidBootClasspathProvider
import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import com.likethesalad.android.buddy.providers.TransformationLoggerProvider
import com.likethesalad.android.common.models.AndroidBuddyExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

@Suppress("UnstableApiUsage")
open class AndroidBuddyPlugin : Plugin<Project>,
    AndroidBootClasspathProvider, FileTreeIteratorProvider,
    PluginClassNamesProvider, TransformationLoggerProvider {
    private lateinit var project: Project
    private lateinit var androidExtension: AppExtension
    private lateinit var androidBuddyExtension: AndroidBuddyExtension
    private lateinit var transformationLoggerFactory: TransformationLoggerFactory

    companion object {
        private const val EXTENSION_NAME = "androidBuddy"
    }

    override fun apply(project: Project) {
        AppInjector.init(this)
        this.project = project
        this.transformationLoggerFactory = AppInjector.getTransformationLoggerFactory()
        androidBuddyExtension = createExtension()
        androidExtension = project.extensions.getByType(AppExtension::class.java)
        androidExtension.registerTransform(AppInjector.getByteBuddyTransform())
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
        return androidBuddyExtension.pluginNames.get()
    }

    override fun getTransformationLogger(): TransformationLogger {
        return transformationLoggerFactory.create(project.logger)
    }
}