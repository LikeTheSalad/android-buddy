package com.likethesalad.android.buddylib

import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.tasks.CreateJarDescriptionProperties
import com.likethesalad.android.common.models.AndroidBuddyExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer


@Suppress("UnstableApiUsage")
open class AndroidBuddyLibraryPlugin : Plugin<Project> {

    private lateinit var project: Project

    companion object {
        private const val PROCESS_JAVA_RESOURCES_TASK_NAME = "processResources"
        private const val CREATE_JAR_DESCRIPTION_PROPERTIES_TASK_NAME = "createJarDescriptionProperties"
        private const val COPY_DESCRIPTION_PROPERTIES_TASK_NAME = "copyDescriptionProperties"
        private const val EXTENSION_NAME = "androidBuddyLibrary"
    }

    override fun apply(project: Project) {
        this.project = project
        project.pluginManager.apply(JavaLibraryPlugin::class.java)
        val extension = project.extensions.create(EXTENSION_NAME, AndroidBuddyExtension::class.java)
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

        val createJarDescriptionProperties = project.tasks.register(
            CREATE_JAR_DESCRIPTION_PROPERTIES_TASK_NAME,
            CreateJarDescriptionProperties::class.java,
            LibraryInjector.getCreateJarDescriptionPropertiesArgs()
        )
        createJarDescriptionProperties.configure {
            it.inputClassNames.set(extension.pluginNames)
            it.inputClassPaths.plus(getClassesOutputDirs(sourceSets))
            it.outputDir.set(project.file("${project.buildDir}/${it.name}"))
        }

        val javaProcessResourcesTask = project.tasks.named(PROCESS_JAVA_RESOURCES_TASK_NAME, Copy::class.java)

        project.tasks.register(COPY_DESCRIPTION_PROPERTIES_TASK_NAME, Copy::class.java) {
            it.from(createJarDescriptionProperties)
            it.into({ "${javaProcessResourcesTask.get().destinationDir}/META-INF/android-buddy-plugins" })

            it.dependsOn(javaProcessResourcesTask)
        }
    }

    private fun getClassesOutputDirs(sourceSets: SourceSetContainer): FileCollection {
        return sourceSets.getByName("main").output.classesDirs
    }
}