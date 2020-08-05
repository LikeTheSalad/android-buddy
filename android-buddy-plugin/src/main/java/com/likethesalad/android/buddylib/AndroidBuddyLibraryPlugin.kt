package com.likethesalad.android.buddylib

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.LibraryVariant
import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension
import com.likethesalad.android.buddylib.tasks.CreateJarDescriptionProperties
import com.likethesalad.android.common.base.BuddyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.logging.Logger


@Suppress("UnstableApiUsage")
open class AndroidBuddyLibraryPlugin : Plugin<Project>, BuddyPlugin {

    private lateinit var project: Project
    private lateinit var androidExtension: LibraryExtension

    companion object {
        private const val CREATE_JAR_DESCRIPTION_PROPERTIES_TASK_NAME = "createJarDescriptionProperties"
        private const val EXTENSION_NAME = "androidBuddyLibrary"
    }

    override fun apply(project: Project) {
        LibraryInjector.init(this)
        this.project = project
        androidExtension = project.extensions.getByType(LibraryExtension::class.java)
        LibraryInjector.getDependencyHandlerUtil().addDependencies()
        val extension = project.extensions.create(EXTENSION_NAME, AndroidBuddyLibExtension::class.java)

        androidExtension.libraryVariants.all {
            createTasksFor(extension, it)
        }
    }

    private fun createTasksFor(
        extension: AndroidBuddyLibExtension,
        variant: LibraryVariant
    ) {
        val createJarDescriptionProperties = project.tasks.register(
            "${CREATE_JAR_DESCRIPTION_PROPERTIES_TASK_NAME}For${variant.name.capitalize()}",
            CreateJarDescriptionProperties::class.java,
            LibraryInjector.getCreateJarDescriptionPropertiesArgs()
        )
        createJarDescriptionProperties.configure {
            it.inputClassNames = extension.pluginNames
            it.outputDir.set(project.file("${project.buildDir}/${it.name}"))
        }

        variant.processJavaResourcesProvider.configure {
            it.from(createJarDescriptionProperties)
        }
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
}