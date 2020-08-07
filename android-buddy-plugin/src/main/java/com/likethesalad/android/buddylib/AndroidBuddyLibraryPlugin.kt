package com.likethesalad.android.buddylib

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.providers.AndroidBuddyLibExtensionProvider
import com.likethesalad.android.buddylib.providers.IncrementalDirProvider
import com.likethesalad.android.buddylib.providers.TaskContainerProvider
import com.likethesalad.android.common.base.BuddyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskContainer
import java.io.File


@Suppress("UnstableApiUsage")
open class AndroidBuddyLibraryPlugin : Plugin<Project>, BuddyPlugin, TaskContainerProvider,
    AndroidBuddyLibExtensionProvider, IncrementalDirProvider {

    private lateinit var project: Project
    private lateinit var libExtension: AndroidBuddyLibExtension
    private lateinit var androidExtension: LibraryExtension

    companion object {
        private const val EXTENSION_NAME = "androidBuddyLibrary"
    }

    override fun apply(project: Project) {
        LibraryInjector.init(this)
        this.project = project
        androidExtension = project.extensions.getByType(LibraryExtension::class.java)
        libExtension = project.extensions.create(EXTENSION_NAME, AndroidBuddyLibExtension::class.java)
        LibraryInjector.getDependencyHandlerUtil().addDependencies()
        LibraryInjector.getCreateMetadataTaskGenerator().createTaskPerVariant()
    }

    override fun getLogger(): Logger {
        return project.logger
    }

    override fun getDependencyHandler(): DependencyHandler {
        return project.dependencies
    }

    override fun getRepositoryHandler(): RepositoryHandler {
        return project.repositories
    }

    override fun getAndroidExtension(): BaseExtension {
        return androidExtension
    }

    override fun getTaskContainer(): TaskContainer {
        return project.tasks
    }

    override fun getExtension(): AndroidBuddyLibExtension {
        return libExtension
    }

    override fun createIncrementalDir(dirName: String): File {
        return project.file("${project.buildDir}/intermediates/incremental/$dirName")
    }
}